package com.example.admin.healthtrack.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.healthtrack.R;
import com.example.admin.healthtrack.activity.MainActivity;
import com.example.admin.healthtrack.adapter.ScheduleAdapter;
import com.example.admin.healthtrack.models.Medicament;
import com.example.admin.healthtrack.models.TodoItem;
import com.example.admin.healthtrack.views.FinishedDialog;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import es.dmoral.toasty.Toasty;

import static android.content.Context.MODE_PRIVATE;
import static android.support.constraint.Constraints.TAG;

public class ScheduleFragment extends Fragment implements ScheduleAdapter.ItemClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String IIN;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View rootView;
    FirebaseDatabase database;
    ArrayList<TodoItem> todos;
    ScheduleAdapter adapter;
    RecyclerView recyclerView;
    int finishedCount = 0;
    public ScheduleFragment() {
        // Required empty public constructor
    }

    public static ScheduleFragment newInstance(String param1, String param2) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_schedule, container, false);

        SharedPreferences prefs = getActivity().getSharedPreferences("FitApp", MODE_PRIVATE);
        Boolean logged= prefs.getBoolean("logged", false);
        if (logged) {
            IIN = prefs.getString("IIN", "0");
            Log.d("mLog", "IIN: "+IIN);
        }

        Calendar startDate = Calendar.getInstance();
        database = FirebaseDatabase.getInstance();
        startDate.add(Calendar.MONTH, -1);
        todos = new ArrayList<>();
        adapter = new ScheduleAdapter(getContext(), todos);
        adapter.setClickListener(this);
        recyclerView = rootView.findViewById(R.id.scheduleRv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        /* ends after 1 month from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(rootView, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(7)
                .build();

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                Toasty.info(getContext(), date.get(Calendar.DAY_OF_MONTH)+"/"+ date.get(Calendar.MONTH), Toast.LENGTH_SHORT, true).show();
            }
        });
        getMedicaments();
        return rootView;
    }
    void getMedicaments(){
        DatabaseReference myRef = database.getReference("Patients").child(IIN).child("treatment");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                int treatmentId = dataSnapshot.getValue(Integer.class);
                Log.d("mLog", "Id is: " + treatmentId);
                DatabaseReference medicamentRef = database.getReference("medicaments");
                medicamentRef.orderByChild("treatmentID").equalTo(treatmentId).addChildEventListener(medicamentRefListener);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("mLog", "Failed to read value.", error.toException());
            }
        });
    }

    ChildEventListener medicamentRefListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            Medicament medicament = (Medicament) dataSnapshot.getValue(Medicament.class);
            Log.d("mLog", "onChildAdded: "+ medicament);
            for(int i = 0; i < medicament.count; i++) {
                Log.d("mLog", "getTime: "+medicament.getTime(i));
                int hour = (int)medicament.getTime(i)/3600;
                int minutes = ((int) medicament.getTime(i)%3600) / 60;
                String time = hour + ":" + minutes;
                if(minutes <10) time+="0";
                todos.add(new TodoItem(time, medicament.name + " " + medicament.dosage + "мг", false));
            }
            adapter.notifyDataSetChanged();
            recyclerView.setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.progressbar).setVisibility(View.GONE);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    public void onItemClick(View view, int position) {
        TodoItem item = todos.get(position);
        if(!item.isFinished()) {
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.finished);
            TextView time = view.findViewById(R.id.time);
            TextView text = view.findViewById(R.id.text);
            if(!checkBox.isChecked())  {
                view.setEnabled(false);
                view.setAlpha(0.5f);
                time.setPaintFlags(time.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                text.setPaintFlags(time.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                checkBox.setChecked(true);
                finishedCount++;
                Toasty.success(getContext(), "Молоцом!").show();
                if(finishedCount == todos.size()) {
                    FinishedDialog dialog = new FinishedDialog(getActivity());
                    dialog.show();
                }
            }
        }
        updateCircleState();
    }
    void updateCircleState(){
        ArcProgress arc = rootView.findViewById(R.id.arc_progress);
        float percent = finishedCount* 100/todos.size() ;
        Log.d("mLog", "updateCircleState: "+ percent+"  "+finishedCount);
        arc.setProgress((int) percent);
    }
}

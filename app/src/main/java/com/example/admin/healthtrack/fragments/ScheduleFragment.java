package com.example.admin.healthtrack.fragments;

import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.healthtrack.R;
import com.example.admin.healthtrack.adapter.ScheduleAdapter;
import com.example.admin.healthtrack.models.TaskItem;
import com.example.admin.healthtrack.models.TodoItem;
import com.example.admin.healthtrack.views.ConfirmDialog;
import com.example.admin.healthtrack.views.FinishedDialog;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import es.dmoral.toasty.Toasty;

import static android.content.Context.MODE_PRIVATE;

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
    FirebaseFirestore database;
    ArrayList<TodoItem> todos;
    ArrayList<TaskItem> tasks;
    ScheduleAdapter adapter;
    RecyclerView recyclerView;
    ArcProgress arcProgress;
    int finishedCount = 0;
    CollectionReference treatmentCourses;
    ProgressBar progressBar;
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
        IIN = "990192934567";
        Calendar startDate = Calendar.getInstance();

        database = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        database.setFirestoreSettings(settings);

        startDate.add(Calendar.MONTH, -1);
        todos = new ArrayList<>();
        tasks = new ArrayList<>();
        adapter = new ScheduleAdapter(getContext(), todos);
        adapter.setClickListener(this);
        recyclerView = rootView.findViewById(R.id.scheduleRv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        progressBar = rootView.findViewById(R.id.progressbar);
        arcProgress  = (ArcProgress) rootView.findViewById(R.id.arc_progress);
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
                //Toasty.info(getContext(), date.get(Calendar.DAY_OF_MONTH)+"/"+ date.get(Calendar.MONTH), Toast.LENGTH_SHORT, true).show();
                progressBar.setVisibility(View.VISIBLE);
                dayUpdated(date);
            }
        });

        treatmentCourses = database.collection("treatmentCourses");
        treatmentCourses.whereEqualTo("patientId", IIN)
        .get()
        .addOnCompleteListener(onCompleteListener)
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toasty.warning(getContext(),"Couldn't get any data").show();
            }
        });
        return rootView;
    }

    OnCompleteListener onCompleteListener = new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                    Log.d("mLog", document.getId() + " => " + document.getData());
                    CollectionReference collectionReference = treatmentCourses.document(document.getId()).collection("tasks");
                    collectionReference.get().addOnCompleteListener(onTasksLoadCompleted);
                    //Log.d("mLog", "onComplete: "+ documentReference.getPath());

                }
            } else {
                Log.w("mLog", "Error getting documents.", task.getException());
            }
        }
    };

    OnCompleteListener onTasksLoadCompleted = new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                    TaskItem mTask = queryDocumentSnapshot.toObject(TaskItem.class);
                    tasks.add(mTask);
                    if (mTask.isGoingToday(Calendar.getInstance())){
                        TodoItem todo = new TodoItem(mTask.getStringTime(), mTask.getTitle(),  false);
                        todos.add(todo);
                    }
                }
                adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            } else {
                Log.w("mLog", "Error getting documents.", task.getException());
            }
        }
    };

    private void dayUpdated(Calendar date){
        todos.clear();
        finishedCount = 0;
        arcProgress.setProgress(0);
        for(TaskItem mTask: tasks){
            if (mTask.isGoingToday(date)){
                TodoItem todo = new TodoItem(mTask.getStringTime(), mTask.getTitle(),  false);
                todos.add(todo);
            }
        }
        adapter = new ScheduleAdapter(getContext(), todos);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
    }


    @Override
    public void onItemClick(final View view, int position) {
        TodoItem item = todos.get(position);
        if(!item.isFinished()) {
            final CheckBox checkBox = (CheckBox) view.findViewById(R.id.finished);
            final TextView time = view.findViewById(R.id.time);
            final TextView text = view.findViewById(R.id.text);
            if(!checkBox.isChecked())  {
                ConfirmDialog confirmDialog = new ConfirmDialog(getActivity());
                confirmDialog.setClickListener(new ConfirmDialog.mClickListener() {
                    @Override
                    public void onClicked(View v) {
                        if(v.getId() == R.id.submit){
                            view.setEnabled(false);
                            view.setAlpha(0.5f);
                            time.setPaintFlags(time.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            text.setPaintFlags(time.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            checkBox.setChecked(true);
                            finishedCount++;
                            //Toasty.success(getContext(), "Молоцом!").show();
                            if(finishedCount == todos.size()) {
                                FinishedDialog dialog = new FinishedDialog(getActivity());
                                dialog.show();
                            }
                            updateCircleState();
                        }
                    }
                });
                confirmDialog.show();
            }
        }

    }
    void updateCircleState(){
        ArcProgress arc = rootView.findViewById(R.id.arc_progress);
        float percent = finishedCount* 100/todos.size() ;
        Log.d("mLog", "updateCircleState: "+ percent+"  "+finishedCount);
        arc.setProgress((int) percent);
    }
}

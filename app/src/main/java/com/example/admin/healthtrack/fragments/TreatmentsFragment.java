package com.example.admin.healthtrack.fragments;

import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.example.admin.healthtrack.R;
import com.example.admin.healthtrack.adapter.TreatmentAdapter;
import com.example.admin.healthtrack.models.Medicament;
import com.example.admin.healthtrack.models.Treatment;
import com.example.admin.healthtrack.views.MedicamentDialog;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class TreatmentsFragment extends Fragment implements TreatmentAdapter.ItemClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    FirebaseDatabase database;
    ArrayList<Treatment> treatments;
    TreatmentAdapter adapter;
    RecyclerView recyclerView;
    String IIN;
    View rootView;
    public TreatmentsFragment() {
        // Required empty public constructor
    }

    public static TreatmentsFragment newInstance(String param1, String param2) {
        TreatmentsFragment fragment = new TreatmentsFragment();
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
        rootView =  inflater.inflate(R.layout.fragment_treatments, container, false);
        SharedPreferences prefs = getActivity().getSharedPreferences("FitApp", MODE_PRIVATE);
        Boolean logged= prefs.getBoolean("logged", false);
        if (logged) {
            IIN = prefs.getString("IIN", "0");
            Log.d("mLog", "IIN: "+IIN);
        }
        database = FirebaseDatabase.getInstance();
        treatments = new ArrayList<>();
        adapter = new TreatmentAdapter(treatments );
        adapter.setClickListener(this);
        recyclerView = rootView.findViewById(R.id.treatmentsRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        getTreatments();
        return rootView;
    }
    void getTreatments(){
        DatabaseReference myRef = database.getReference("Treatments");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //int treatmentId = dataSnapshot.getValue(Integer.class);
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Treatment treatment = snapshot.getValue(Treatment.class);
                    treatment.setMedicaments( new ArrayList<Medicament>());
                    treatments.add(treatment);
                    Log.d("mLog", "got data: " + treatment);
                    DatabaseReference medicamentRef = database.getReference("medicaments");
                    medicamentRef.orderByChild("treatmentID").equalTo(treatment.id).addChildEventListener(medicamentRefListener);
                }


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
            for(int j = 0; j < treatments.size(); j++) {
                Log.d("mLog", "searching: "+j+ " ids: "+ treatments.get(j).id+"  "+medicament.getTreatmentID());
                if (treatments.get(j).id == medicament.getTreatmentID()) {
                    Log.d("mLog", "is now equal");
                    treatments.get(j).medicaments.add(medicament);
                    adapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.progressbar).setVisibility(View.GONE);
                }
            }

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
    public void onItemClick(Medicament med) {
        MedicamentDialog medicamentDialog = new MedicamentDialog(getActivity(), med);
        medicamentDialog.show();
        Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();
    }
}

package com.example.admin.healthtrack.views;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.admin.healthtrack.R;
import com.example.admin.healthtrack.models.Medicament;

public class MedicamentDialog extends Dialog implements
        View.OnClickListener {

    public Medicament medicament;
    public Dialog d;
    public TextView mName, mDosage, mPeritem, mEvery, mDays, mTime;
    Button ok;

    public MedicamentDialog(Activity a, Medicament m) {
        super(a);
        medicament = m;
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_medicament);
        mName = (TextView) findViewById(R.id.medicament_name);
        mDosage = (TextView) findViewById(R.id.dosage);
        mPeritem = (TextView) findViewById(R.id.per_diem);
        mEvery = (TextView) findViewById(R.id.every);
        mDays = (TextView) findViewById(R.id.amount_of_days);
        mTime = (TextView) findViewById(R.id.eating_time);
        ok = (Button) findViewById(R.id.submit);
        ok.setOnClickListener(this);


        mName.setText(medicament.getName());
        mDosage.setText(String.valueOf(medicament.getDosage()));
        mPeritem.setText(String.valueOf(medicament.getCount()));
        mEvery.setText("Через день");
        mDays.setText(String.valueOf(medicament.getDays()));
        mTime.setText("После обеда");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                this.dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
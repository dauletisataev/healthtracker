package com.example.admin.healthtrack.views;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.admin.healthtrack.R;
import com.example.admin.healthtrack.adapter.ScheduleAdapter;

public class ConfirmDialog extends Dialog implements View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button yes, cancel;
    mClickListener mClickListener;
    public ConfirmDialog(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirm_finished);
        yes = (Button) findViewById(R.id.submit);
        cancel = (Button) findViewById(R.id.cancel);
        yes.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                mClickListener.onClicked(v);
                break;
            case R.id.cancel:
                mClickListener.onClicked(v);
                break;
            default:
                break;
        }
        dismiss();
    }
    public interface mClickListener {
        public void onClicked(View v);
    }
    public  void setClickListener(ConfirmDialog.mClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
}
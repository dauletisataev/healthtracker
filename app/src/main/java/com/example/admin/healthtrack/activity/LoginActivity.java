package com.example.admin.healthtrack.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.admin.healthtrack.R;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {


    private EditText editTextMobile;
    String lastChar = " ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextMobile = findViewById(R.id.phoneNumber);
        editTextMobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) editTextMobile.setText("+77");
            }
        });
        SharedPreferences prefs = getSharedPreferences("FitApp", MODE_PRIVATE);
        Boolean logged= prefs.getBoolean("logged", false);
        if (logged) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String IIN = ((EditText) findViewById(R.id.iinText)).getText().toString();
                if(IIN.length() != 12) {
                    Toasty.error(getApplicationContext(), "ИИН должен состоять из 12 цифр").show();
                    return;
                }
                String mobile = editTextMobile.getText().toString().trim();

                if(mobile.isEmpty() || mobile.length() < 10){
                    editTextMobile.setError("Enter a valid mobile");
                    editTextMobile.requestFocus();
                    return;
                }

                Intent intent = new Intent(LoginActivity.this, VerifyActivity.class);
                intent.putExtra("mobile", mobile);
                intent.putExtra("IIN", IIN);
                startActivity(intent);
                finish();
            }
        });
    }


}
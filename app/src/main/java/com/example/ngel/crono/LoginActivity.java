package com.example.ngel.crono;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.onBackPressed();
    }

    @Override
    public void onBackPressed() {} //para que no pueda regresar al activity anterior
}

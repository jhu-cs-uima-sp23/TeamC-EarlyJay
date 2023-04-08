package com.example.empty;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class Login extends AppCompatActivity {

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = getApplicationContext();


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        edit = sharedPreferences.edit();

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");

        String uid = sharedPreferences.getString("uid", "");
        if (uid.equals("")) {
            uid = createTransactionID();
            edit.putString("uid", uid);
            edit.apply();
            LogInHelper loginHelper = new LogInHelper(uid);
            reference.child(uid).setValue(loginHelper);
        }

        reference = reference.child(uid);








        Button enter = findViewById(R.id.let_s_go);
        enter.setOnClickListener(v -> {
            startActivity(new Intent(Login.this, MainActivity.class));
        });

    }


    public String createTransactionID() {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }
}
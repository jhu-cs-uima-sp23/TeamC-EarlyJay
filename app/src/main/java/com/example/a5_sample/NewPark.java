package com.example.a5_sample;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewPark extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_park);

        Button sbtn = (Button) findViewById(R.id.save_btn);
        sbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText parkNameEditText = findViewById(R.id.new_park);
                String parkName = parkNameEditText.getText().toString().trim();

                if (!parkName.isEmpty()) {
                    Park newPark = new Park(parkName, 0);
                    MainActivity.parks.add(newPark);
                    MainActivity.pa.notifyDataSetChanged();
                }

                finish();
            }

        });

        Button qbtn = (Button) findViewById(R.id.quit_btn);
        qbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
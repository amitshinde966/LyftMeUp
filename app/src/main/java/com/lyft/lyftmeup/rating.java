package com.lyft.lyftmeup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

public class rating extends AppCompatActivity {


    Button submit;
    RatingBar ratingbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        submit = findViewById(R.id.btnsubmit);
        ratingbar = findViewById(R.id.ratingBar);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rating=String.valueOf(ratingbar.getRating());
                Toast.makeText(getApplicationContext(), "ThankYou!! for rating : "+rating, Toast.LENGTH_LONG).show();

            }
        });


    }




}

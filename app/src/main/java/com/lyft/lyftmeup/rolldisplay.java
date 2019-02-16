package com.lyft.lyftmeup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class rolldisplay extends AppCompatActivity implements View.OnClickListener {

    EditText name;
    EditText phoneno;
    EditText aadhar;
    EditText sex;
    Button user;
    Button rider;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rolldisplay);

         name = findViewById(R.id.txtname);
        phoneno = findViewById(R.id.txtphone);
        aadhar = findViewById(R.id.txtaadhar);
        sex = findViewById(R.id.txtgen);
        user= findViewById(R.id.btnuser);
        rider=findViewById(R.id.btnrider);

        user.setOnClickListener(this);
        rider.setOnClickListener(this);



        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users").child(firebaseUser.getUid().toString());


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnuser:

                //sendCode();
                break;
            case R.id.btnrider:

               // resendCode();
                break;

            // Do something
        }

    }
}

class User{
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAadhar() {
        return userAadhar;
    }

    public void setUserAadhar(String userAadhar) {
        this.userAadhar = userAadhar;
    }

    String userName;
    String userAadhar;
    public User(String name,String aadhar)
    {
        this.userName=name;
        this.userAadhar=aadhar;
    }


}

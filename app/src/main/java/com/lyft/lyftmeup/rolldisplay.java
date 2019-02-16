package com.lyft.lyftmeup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    Button signot;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rolldisplay);

         name = (EditText) findViewById(R.id.txtname);
        phoneno = (EditText) findViewById(R.id.txtphone);
        aadhar = (EditText) findViewById(R.id.txtaadhar);
        sex = (EditText) findViewById(R.id.txtsex);
        user= (Button) findViewById(R.id.btnuser);
        rider= (Button) findViewById(R.id.btnrider);

        rider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(rolldisplay.this, DriverMapActivity.class);
                startActivity(i);
                finish();
            }
        });

        signot= (Button) findViewById(R.id.signout);
        signot.setOnClickListener(this);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users").child(firebaseUser.getUid().toString());

      ref.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {

              String na = dataSnapshot.child("userName").getValue(String.class);
              String aa = dataSnapshot.child("userAadhar").getValue(String.class);
              String se = dataSnapshot.child("userSex").getValue(String.class);
              String ph = dataSnapshot.child("userPhone").getValue(String.class);

            String temp1,temp2,temp3,temp4;
              temp1=na;
              temp2=aa;
              temp3=se;
              temp4=ph;
             name.setText(temp1);
              aadhar.setText(temp2);
              sex.setText(temp3);
              phoneno.setText(temp4);
            //  aadhar.setText(kills);
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
            case R.id.signout:

                firebaseAuth.getInstance().signOut();
                finish();

            break;
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

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    String userName;
    String userAadhar;
    String userSex;
    String userPhone;
    public User(String name,String aadhar,String sex,String phone)
    {
        this.userName=name;
        this.userAadhar=aadhar;
        this.userSex=sex;
        this.userPhone=phone;
    }

}

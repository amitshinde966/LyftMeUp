package com.lyft.lyftmeup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class adddata extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth faAuth;
    private FirebaseUser fUser;
    private DatabaseReference databaseReference;
    private EditText name;
    private EditText aadharcard;
    private EditText sex;
    private EditText phone;
    private Button submit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adddata);

        name = (EditText) findViewById(R.id.textname);
        aadharcard = (EditText) findViewById(R.id.textaadhar);
        sex = (EditText) findViewById(R.id.textsex);
        phone = (EditText) findViewById(R.id.textphone);
        submit = (Button) findViewById(R.id.btnsubmit);

        submit.setOnClickListener(this);


        faAuth = FirebaseAuth.getInstance();
        fUser = faAuth.getCurrentUser();


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnsubmit:

             String n = name.getText().toString();
               String a = aadharcard.getText().toString();
                String p = phone.getText().toString();
                String s = sex.getText().toString();



               databaseReference = FirebaseDatabase.getInstance().getReference("Users");


                UserData udata = new UserData(n,a,s,p);
                databaseReference.child(fUser.getUid().toString()).setValue(udata);
                Toast.makeText(this,"User Info Added", Toast.LENGTH_LONG).show();
                startActivity(new Intent(adddata.this,rolldisplay.class));
                finish();
                break;


        }
    }


}
class UserData{
    String userName;
    String userAadhar;
    String userSex;
    String userPhone;
    public UserData(String name,String aadhar,String sex,String phone)
    {
        this.userName=name;
        this.userAadhar=aadhar;
        this.userSex=sex;
        this.userPhone=phone;
    }
}

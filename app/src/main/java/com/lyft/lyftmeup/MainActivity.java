package com.lyft.lyftmeup;

        import android.content.Intent;
        import android.support.annotation.NonNull;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.FirebaseException;
        import com.google.firebase.FirebaseTooManyRequestsException;
        import com.google.firebase.auth.AuthResult;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.auth.PhoneAuthCredential;
        import com.google.firebase.auth.PhoneAuthProvider;

        import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PhoneAuth";

    private EditText phoneText;
    private EditText codeText;
    private Button verifyButton;
    private Button sendButton;
    private Button resendButton;
    private Button signoutButton;
    private TextView statusText;

    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;

    private FirebaseAuth fbAuth;
    private FirebaseUser firebaseUser;
    private FirebaseAuth.AuthStateListener mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phoneText = (EditText) findViewById(R.id.phoneText);
        codeText = (EditText) findViewById(R.id.codeText);
        verifyButton = (Button) findViewById(R.id.verifyButton);
        sendButton = (Button) findViewById(R.id.sendButton);
        resendButton = (Button) findViewById(R.id.resendButton);
        signoutButton = (Button) findViewById(R.id.signoutButton);
        statusText = (TextView) findViewById(R.id.statusText);

        verifyButton.setOnClickListener(this);
        sendButton.setOnClickListener(this);
        resendButton.setOnClickListener(this);
        signoutButton.setOnClickListener(this);

        verifyButton.setEnabled(false);
        resendButton.setEnabled(false);
        signoutButton.setEnabled(false);
        statusText.setText("Signed Out");

        fbAuth = FirebaseAuth.getInstance();


        mAuth = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null)
                {
                    startActivity(new Intent(MainActivity.this,rolldisplay.class));
                    finish();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
fbAuth.addAuthStateListener(mAuth);
    }

    public void sendCode() {

        String phoneNumber = phoneText.getText().toString();

        setUpVerificatonCallbacks();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                verificationCallbacks);
        Toast.makeText(this,"sms sent"+phoneNumber, Toast.LENGTH_LONG).show();
    }

    private void setUpVerificatonCallbacks() {

        verificationCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(
                            PhoneAuthCredential credential) {

                        signoutButton.setEnabled(true);
                        statusText.setText("Signed In");
                        resendButton.setEnabled(false);
                        verifyButton.setEnabled(false);
                        codeText.setText("");
                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            Log.d(TAG, "Invalid credential: "
                                    + e.getLocalizedMessage());
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // SMS quota exceeded
                            Log.d(TAG, "SMS Quota exceeded.");
                        }
                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {

                        phoneVerificationId = verificationId;
                        resendToken = token;

                        verifyButton.setEnabled(true);
                        sendButton.setEnabled(false);
                        resendButton.setEnabled(true);
                    }
                };
    }

    public void verifyCode() {

        String code = codeText.getText().toString();

        PhoneAuthCredential credential =
                PhoneAuthProvider.getCredential(phoneVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            signoutButton.setEnabled(true);
                            codeText.setText("");
                            statusText.setText("Signed In");
                            resendButton.setEnabled(false);
                            verifyButton.setEnabled(false);


                           startActivity(new Intent(MainActivity.this,adddata.class));
                            finish();
                        } else {
                            if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {

                            }
                        }
                    }
                });
    }

    public void resendCode() {

        String phoneNumber = phoneText.getText().toString();

        setUpVerificatonCallbacks();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                verificationCallbacks,
                resendToken);
    }

    public void signOut() {
        fbAuth.signOut();
        statusText.setText("Signed Out");
        signoutButton.setEnabled(false);
        sendButton.setEnabled(true);
    }

    @Override
    public void onClick(View view) {


        switch (view.getId()) {
            case R.id.sendButton:
                if(phoneText.length() != 10)
                {
                    Toast.makeText(this,"Invalid Phone Number", Toast.LENGTH_LONG).show();
                }
else{
                sendCode();
                break;}
            case R.id.resendButton:

                resendCode();
                break;
            case R.id.verifyButton:

               verifyCode();
                break;
            case R.id.signoutButton:

               signOut();
                break;
                // Do something
        }
    }
}
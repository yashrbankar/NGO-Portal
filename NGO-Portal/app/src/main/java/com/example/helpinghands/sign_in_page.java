package com.example.helpinghands;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;


public class sign_in_page extends AppCompatActivity {


    private static final int RC_SIGN_IN = 0 ;
    EditText phone_no_edittext;
    Button get_otp_button;
    ProgressBar progressBar_sign_in;
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private String Tag = "sign_in_page";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_page);

        phone_no_edittext=findViewById(R.id.phone_no_edittext);
        get_otp_button=findViewById(R.id.get_otp_button);
        progressBar_sign_in=findViewById(R.id.progressBar_sign_in);
        signInButton=findViewById(R.id.sign_in_button);
        mAuth=FirebaseAuth.getInstance();

        GoogleSignInOptions mGoogleSignInOptions= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient= GoogleSignIn.getClient(this,mGoogleSignInOptions);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;
                    // ...
                }
            }
        });

        get_otp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!phone_no_edittext.getText().toString().trim().isEmpty()){
                    if((phone_no_edittext.getText().toString().trim()).length() == 10){

                        progressBar_sign_in.setVisibility(View.VISIBLE);
                        get_otp_button.setVisibility(View.INVISIBLE);

                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                "+91" + phone_no_edittext.getText().toString(),
                                30,
                                TimeUnit.SECONDS,
                                sign_in_page.this,
                                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                    @Override
                                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                        progressBar_sign_in.setVisibility(View.GONE);
                                        get_otp_button.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onVerificationFailed(@NonNull FirebaseException e) {
                                        progressBar_sign_in.setVisibility(View.GONE);
                                        get_otp_button.setVisibility(View.VISIBLE);
                                        Toast.makeText(sign_in_page.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCodeSent(@NonNull String backendotp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                        progressBar_sign_in.setVisibility(View.GONE);
                                        get_otp_button.setVisibility(View.VISIBLE);
                                        Intent intent=new Intent(getApplicationContext(),OTP_Verification.class);
                                        intent.putExtra("mobile",phone_no_edittext.getText().toString());
                                        intent.putExtra("backendotp",backendotp);
                                        startActivity(intent);
                                    }
                                }
                        );



                    }else {
                        Toast.makeText(sign_in_page.this,"Please Enter Correct Number",Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(sign_in_page.this,"Enter Mobile Number",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

         if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {

            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Toast.makeText(this, "Sign in Successfully", Toast.LENGTH_SHORT).show();

            firebaseAuthWithGoogle(account);

        } catch (ApiException e) {

            Toast.makeText(this, "Sign in Failed", Toast.LENGTH_SHORT).show();
            firebaseAuthWithGoogle(null);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(sign_in_page.this, "Successfully", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(getApplicationContext());

                            Intent intent= new Intent(getApplicationContext(),setup_profile.class);
                            startActivity(intent);
                            finish();

                        } else {

                            Toast.makeText(sign_in_page.this, "Sign in Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
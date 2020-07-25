package com.ronymayukh.weapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneNumberVerification extends AppCompatActivity {

    String phoneNumber,verificationId;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    EditText otp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_verification);

        progressBar =(ProgressBar)findViewById(R.id.progressBar);

        mAuth=FirebaseAuth.getInstance();

        phoneNumber= getIntent().getStringExtra("phonenumber");

        otp=(EditText) findViewById(R.id.editTextOTP);

        sendVerificationCode(phoneNumber);

    }

    private void verifyCode(String code){
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationId,code);
        SignInWithCredential(credential);
    }

    private void SignInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent=new Intent(PhoneNumberVerification.this,SetupProfile.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("phonenumber",phoneNumber);
                    startActivity(intent);

                }else{
                    Toast.makeText(PhoneNumberVerification.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void sendVerificationCode(String mobile) {
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            Toast.makeText(getApplicationContext(),"Code Sent",Toast.LENGTH_LONG).show();
            verificationId=s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code=phoneAuthCredential.getSmsCode();
            if(code!=null){
                otp.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(PhoneNumberVerification.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    };

    public void onVerifyClick(View view){
        String code=otp.getText().toString();
        if(code.isEmpty() || code.length()<6){
            otp.setError("Enter a valid code...");
            otp.requestFocus();
            return;
        }
        verifyCode(code);
    }

}


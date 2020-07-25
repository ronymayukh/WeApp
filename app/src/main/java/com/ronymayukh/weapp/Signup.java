package com.ronymayukh.weapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;

public class Signup extends AppCompatActivity {

    Spinner spinner;
    EditText phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        phone=(EditText)findViewById(R.id.editTextPhone);
        spinner=(Spinner) findViewById(R.id.spinnerCountry);
        spinner.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,CountryData.countryName));

    }

    public void onOTP(View view){
        String code,number;
        code = CountryData.countryCode.get(spinner.getSelectedItemPosition());
        number=phone.getText().toString().trim();
        if(number.isEmpty() || number.length()!=10){
            phone.setError("Please enter a number");
            phone.requestFocus();
            return;
        }

        String phoneNumber="+" + code + number;
        Intent intent=new Intent(Signup.this,PhoneNumberVerification.class);
        intent.putExtra("phonenumber",phoneNumber);
        startActivity(intent);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            Intent intent=new Intent(this,ChatList.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
package com.example.anuj.auth;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.anuj.auth.SessionManagement.ServiceManager;
import com.example.anuj.auth.SessionManagement.SessionManager;
import com.example.anuj.auth.model.userModel;
import com.example.anuj.auth.smsService.sms;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignupActivity extends AppCompatActivityExt {

    private EditText name, rsiID, dob, contact, email, signupotp;

    private EditText pass, passConfirm;
    String msg;
    String s_mob;
    private LinearLayout passwordLayout;
    int otpnum;
    Button sendotp,verifyotp,confirm;
    String num;
    DatabaseReference mDatabase;

    boolean flag = false;

    SessionManager sessionManager;
    private String id = null;

//    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Utils.darkenStatusBar(this, R.color.colorPrimary);

        sessionManager = new SessionManager(this);

        id = getIntent().getExtras().getString("ID");

//        name = findViewById(R.id.signName);
        rsiID = findViewById(R.id.signID);
//        dob = findViewById(R.id.signdob);
        contact = findViewById(R.id.signContact);
//        email = findViewById(R.id.signEmail);

        passwordLayout = findViewById(R.id.passwordLayout);

        pass = findViewById(R.id.signupPass);
        passConfirm = findViewById(R.id.signupRePass);
        sendotp = findViewById(R.id.signupSendOtp);
        verifyotp = findViewById(R.id.signupVerifyOtp);
        signupotp = findViewById(R.id.signupOTP);
        confirm = findViewById(R.id.signupConfirm);


        mDatabase = FirebaseDatabase.getInstance().getReference().child("UserSignIn");


        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

//                String s_name = dataSnapshot.child(id).child("name").getValue().toString();
//                String s_dob = dataSnapshot.child(id).child("dob").getValue().toString();
//                String s_email = dataSnapshot.child(id).child("email").getValue().toString();
                s_mob = dataSnapshot.child(id).child("mobno").getValue().toString();
/*
                if (s_name != null && s_name != "") {
                    name.setText(s_name);
                    name.setEnabled(false);
                }

                if (s_dob != null && s_dob != "") {
                    dob.setText(s_dob);
                    dob.setEnabled(false);
                }

                if (s_email != null && s_email != "") {
                    email.setText(s_email.toLowerCase());
                    email.setEnabled(false);
                }
*/
                if (s_mob != null && s_mob != "" && s_mob.length() == 10) {
//                    contact.setText(s_mob);
                    contact.setText(s_mob.charAt(0)+"XXXXXXX"+s_mob.charAt(8)+""+s_mob.charAt(9));
                    contact.setEnabled(false);
                }
                else {
                    contact.setText("Contact RSAMI to update your mobile number");
                    contact.setEnabled(false);
                    Toast.makeText(SignupActivity.this, "Contact RSAMI to update your mobile number", Toast.LENGTH_LONG).show();
                    sendotp.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        rsiID.setText(id);
        rsiID.setEnabled(false);
        otpnum = (int)(Math.random() * 100000 + 1);

        sendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // Log.e("otp",otpnum+"");
                //Toast.makeText(SignupActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                msg = "Your OTP to set password is: "+otpnum+", regards RSAMI!";
                num = s_mob;
                //Toast.makeText(SignupActivity.this, ""+msg+" "+num, Toast.LENGTH_SHORT).show();
                new Thread(new sms(msg, num)).start();
                signupotp.setVisibility(View.VISIBLE);
                sendotp.setVisibility(View.INVISIBLE);
                verifyotp.setVisibility(View.VISIBLE);

            }
        });

        verifyotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((signupotp.getText()+"").equals(otpnum+"")){
                    pass.setVisibility(View.VISIBLE);
                    passConfirm.setVisibility(View.VISIBLE);
                    confirm.setVisibility(View.VISIBLE);

                }
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if((passConfirm.getText()+"").equals(pass.getText()+"")){
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference = database.getReference().child("UserSignIn").child(id);
                    userModel u = new userModel();
                    u.pass = pass.getText()+"";
                    u.rsiID = id;
                    u.mobno = num;
                    reference.setValue(u);
                    Toast.makeText(SignupActivity.this, "Done.", Toast.LENGTH_SHORT).show();

                }
                else
                    Toast.makeText(SignupActivity.this, "Password Do Not Match", Toast.LENGTH_SHORT).show();

            }
        });





    }


}

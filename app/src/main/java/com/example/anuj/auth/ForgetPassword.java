package com.example.anuj.auth;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anuj.auth.SessionManagement.ServiceManager;
import com.example.anuj.auth.SessionManagement.SessionManager;
import com.example.anuj.auth.model.userModel;
import com.example.anuj.auth.smsService.sms;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ForgetPassword extends AppCompatActivityExt {

    Button send,verify;
    EditText otp,passw,repass;
    String num;
    TextView phone;
    String msg="";
    int otpnum;
    Button reset;
    userModel user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        Utils.darkenStatusBar(this, R.color.colorPrimary);

        phone = findViewById(R.id.forgetNum);
        otp = findViewById(R.id.forgetOtp);
        verify = findViewById(R.id.verifyOtp);
        send = findViewById(R.id.forgetSendOtp);
        passw = findViewById(R.id.forgetPass);
        repass = findViewById(R.id.forgetRePass);
        reset = findViewById(R.id.forgetReset);
        user = (userModel) getIntent().getSerializableExtra("User");
        num = user.mobno;

        if(num.length() != 10) {
            phone.setText("Contact RSAMI to update your mobile number");
            send.setVisibility(View.GONE);
            findViewById(R.id.otpMessage).setVisibility(View.GONE);
        }
        else
            phone.setText(num.charAt(0)+"XXXXXXX"+num.charAt(8)+""+num.charAt(9));

        otpnum = (int)(Math.random() * 100000 + 1);
       // Log.e("otp",otpnum+"");
        //  msg = "Forgot Password! No problem, your OTP to reset password is "+otpnum+", regards RSAMI!";
        msg = "Forgot Password! No problem, your OTP to reset password is "+otpnum+",regards RSAMI!";
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new sms(msg, num)).start();
                send.setVisibility(View.INVISIBLE);
                verify.setVisibility(View.VISIBLE);

                otp.setVisibility(View.VISIBLE);
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((otp.getText() + "").equals(otpnum + "")) {
                    // Enter Your Passwords

                    verify.setVisibility(View.INVISIBLE);
                    passw.setVisibility(View.VISIBLE);
                    reset.setVisibility(View.VISIBLE);
                    repass.setVisibility(View.VISIBLE);


                } else
                {
                    // sorry number is wrong
                    Toast.makeText(ForgetPassword.this, "Error OTP does not match !", Toast.LENGTH_SHORT).show();
                }
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference().child("UserSignIn").child(user.rsiID).child("pass");
                //Toast.makeText(ForgetPassword.this, ""+user.rsiID+" "+user.pass, Toast.LENGTH_SHORT).show();
                if((passw.getText()+"").equals(repass.getText()+"")){
                    reference.setValue(passw.getText()+"");
                    Intent intent = new Intent(ForgetPassword.this,LoginActivity.class);
                    user.pass = passw.getText()+"";
                    intent.putExtra("User",user);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(ForgetPassword.this, "Password Do Not Match", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}

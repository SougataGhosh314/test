package com.example.anuj.auth;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anuj.auth.SessionManagement.ServiceManager;
import com.example.anuj.auth.SessionManagement.SessionManager;
import com.example.anuj.auth.model.userModel;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivityExt {

    private FirebaseAuth mAuth;

    private EditText mMobno;
    private EditText mPass;
    String em, pa;
    private Button signInBtn;
    ProgressDialog mProgress;
    String id;

    static userModel user;
    SessionManager session;

    TextView signUp, forgotPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Utils.darkenStatusBar(this, R.color.colorPrimary);

        session = new SessionManager(this);
        if (session.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, NavigationActivity.class);
            startActivity(intent);
            finish();
        }

        user = (userModel) getIntent().getSerializableExtra("userModel");

//        Toast.makeText(this, user.rsiID, Toast.LENGTH_SHORT).show();
/*
        id = getIntent().getStringExtra("ID");
        em = getIntent().getStringExtra("email");
        pa = getIntent().getStringExtra("pass");
*/
        mAuth = FirebaseAuth.getInstance();

        mMobno = findViewById(R.id.signin_mobno_field);
        mPass = findViewById(R.id.singin_pass_field);
        signInBtn = findViewById(R.id.signin_button);

        signUp = findViewById(R.id.sign_up);
        signUp.setText(Html.fromHtml("<u>Sign Up</u>"));

        forgotPass = findViewById(R.id.forgot_pass);
        forgotPass.setText(Html.fromHtml("<u>Forgot Password</u>"));

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                intent.putExtra("ID", user.rsiID);
                startActivity(intent);
            }
        });

        mProgress = new ProgressDialog(this);

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(LoginActivity.this, "RESET PASSWORD", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this,ForgetPassword.class);
                intent.putExtra("User",user);
                startActivity(intent);
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    mProgress.setMessage("Verifying...");
                    mProgress.show();

                    if (TextUtils.isDigitsOnly(mMobno.getText().toString()) && user.mobno.equals(mMobno.getText().toString()) && user.pass.equals(mPass.getText() + "")) {

                        Intent intent = new Intent(LoginActivity.this, NavigationActivity.class);
                        //intent.putExtra("ID", id);
                        //intent.putExtra("email",em);
                        //intent.putExtra("pass", pa);
                        session.createLoginSession(user.mobno, null, user.rsiID, user.pass, null, null, null, null, null);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mProgress.dismiss();
                        startActivity(intent);
                    } else
                        Toast.makeText(LoginActivity.this, "Wrong Number or Password", Toast.LENGTH_SHORT).show();

                    mProgress.dismiss();
                }
                catch (Exception e) {
                    Toast.makeText(LoginActivity.this, "Please Sign Up if here for the first time!", Toast.LENGTH_SHORT).show();
                    mProgress.dismiss();
                }


            }
        });

    }



}


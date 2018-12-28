package com.example.anuj.auth;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.anuj.auth.SessionManagement.ServiceManager;
import com.example.anuj.auth.SessionManagement.SessionManager;
import com.example.anuj.auth.model.userModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivityExt {

    private String validID = "123";
    private String verifyID = null;
    private EditText etAlpha, etNum;
    private Button submitButton;

    private String et = null;
    private int size = 1;

    SessionManager sessionManager;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);

        }

        etAlpha = findViewById(R.id.rsiIDalpha);
        etNum = findViewById(R.id.rsiIDnum);
        submitButton = findViewById(R.id.submitID);

        etAlpha.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                // TODO Auto-generated method stub
                if(etAlpha.getText().toString().length()==size)
                {
                    etNum.requestFocus();
                }
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        etNum.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                // TODO Auto-generated method stub
                if(etNum.getText().toString().length()==0)
                {
                    etAlpha.requestFocus();
                }
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        mProgress = new ProgressDialog(MainActivity.this);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                et = etAlpha.getText().toString() + "-" + etNum.getText().toString();

                verifyID = et;

                mProgress.setMessage("Verifying...");
                mProgress.show();

                validate();

            }
        });

    }


    public void onBackPressed() {
        Toast.makeText(this, "Press Back again to Exit.",
                Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(a);
                System.exit(0);
            }
        }, 1000);

    }

    void validate() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("UserSignIn");

        myRef.addValueEventListener(new ValueEventListener() {
            private static final String TAG = "MainActivity";

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                et = etAlpha.getText().toString() + "-" + etNum.getText().toString();

                String em = dataSnapshot.child(et.toUpperCase()).child("mobno").getValue(String.class);
                String pa = dataSnapshot.child(et.toUpperCase()).child("pass").getValue(String.class);
                userModel u = dataSnapshot.child(et.toUpperCase()).getValue(userModel.class);
                //Toast.makeText(MainActivity.this, ""+em, Toast.LENGTH_SHORT).show();


                if (dataSnapshot.child(et.toUpperCase()).exists()) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    //intent.putExtra("ID", et.getText() + "");
                    //intent.putExtra("email",em);
                    //intent.putExtra("pass", pa);
                    intent.putExtra("userModel", u);
                    mProgress.dismiss();
                    startActivity(intent);

                } else {

                    Toast.makeText(MainActivity.this, "Invalid ID", Toast.LENGTH_SHORT).show();
                    mProgress.dismiss();
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
              //  Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }
}

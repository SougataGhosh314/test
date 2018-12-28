package com.example.anuj.auth;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anuj.auth.SessionManagement.ServiceManager;
import com.example.anuj.auth.SessionManagement.SessionManager;
import com.example.anuj.auth.model.reciptModel;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONException;

public class FeedbackActivity extends AppCompatActivityExt {



    Button submitButton;

    EditText myFeedbackEditText;
    TextView memNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        //Utils.darkenStatusBar(this, R.color.colorPrimary);

        myFeedbackEditText = findViewById(R.id.myFeedback);
        memNo = findViewById(R.id.memberNo_f);
        memNo.setText(SessionManager.getMemberShipNo());
        submitButton = findViewById(R.id.submitFeedbackButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendFeedBack();
            }
        });
    }

    public void sendFeedBack(){

        String[] addresses = {"ankurdewan2000@yahoo.co.in", "suryabhai.raj@gmail.com"};

        String uriText =
                "mailto:rsipune@gmail.com" +"?cc="+"anujsingh9710@gmail.com"+
                        "&subject=" + Uri.encode("RSAMI feedback") +
                        "&body=" + Uri.encode("From: "+memNo.getText().toString()+"\n\n"+myFeedbackEditText.getText().toString());

        Uri uri = Uri.parse(uriText);

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.setData(uri);
        startActivityForResult(Intent.createChooser(intent, "Send Feedback"), 1001);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(this, "Feedback Sent.", Toast.LENGTH_SHORT).show();
    }
}

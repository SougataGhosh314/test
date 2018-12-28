package com.example.anuj.auth;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anuj.auth.SessionManagement.ServiceManager;
import com.example.anuj.auth.model.reciptModel;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class QrScanActivity extends AppCompatActivityExt {

    TextView result;
    Button scanBtn;
    List<String> list;

    private String m_name, m_date;

    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);

        Utils.darkenStatusBar(this, R.color.colorPrimary);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Movies");
        mDatabase.keepSynced(true);

        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        m_date = formatter.format(todayDate);

//        Toast.makeText(this, m_date, Toast.LENGTH_SHORT).show();

        result = findViewById(R.id.res);
        scanBtn = findViewById(R.id.scn_btn);

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QrScanActivity.this, BarcodeScan.class);
                startActivityForResult(intent, 0);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra("barcode");
                    //result.setText("result : "+barcode.displayValue);
                    reciptModel r = new reciptModel();
                    String str = barcode.displayValue;

                    try {
                        r.getObject(str);
                        if(r.getDate().equals(m_date)) {
                            list = r.getSeatsList();
                            Intent intent = new Intent(QrScanActivity.this, checkSeat.class);
                            intent.putExtra("Seats",r);
                            startActivity(intent);
                            finish();
                            for(int i=0;i<list.size();i++)
                            {
                               // Log.i("QR LIST",list.get(i)+"");
                            }
                        }
                        else {
                            Toast.makeText(this, "Wrong QR code! Kindly check the date.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error generating qr", Toast.LENGTH_LONG);
                    }
                } else {
                    result.setText("No info retrieved!");
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

}

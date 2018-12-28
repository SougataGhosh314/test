package com.example.anuj.auth;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.anuj.auth.SessionManagement.ServiceManager;
import com.example.anuj.auth.model.concurrencyModel;
import com.example.anuj.auth.model.reciptModel;
import com.example.anuj.auth.model.transec;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class CountActivity extends AppCompatActivityExt implements AdapterView.OnItemSelectedListener {

    // private EditText dependentCount;
    // private EditText guestCount;

    private int totalPrice;

    private Button confirmButton;

    public boolean isMemberAttending;
    public int dep_ct = 0;
    public int guest_ct = 0;

    private static final int depPrice = 1;
    private static final int guestPrice = 2;

    private String post_key = null;

    private Spinner spinner1, spinner2;

    private String movie_title = null;
    private String movie_date = null;

    private String id = null;

    private reciptModel r = null;

    Long currentTimeStamp = Long.parseLong("0");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count);


        Utils.darkenStatusBar(this, R.color.colorPrimary);

        post_key = getIntent().getExtras().getString("post_key");

        confirmButton = findViewById(R.id.confirm_button);

        //////////////
        spinner1 = findViewById(R.id.spinner_dependants);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array
                .dep_no, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        spinner1.setSelection(0);
        spinner1.setOnItemSelectedListener(this);
        /////////////////

        //////////////
        spinner2 = findViewById(R.id.spinner_guests);
        ArrayAdapter<CharSequence> adapter_2 = ArrayAdapter.createFromResource(this, R.array
                .guests_no, android.R.layout.simple_spinner_item);

        adapter_2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter_2);
        spinner2.setSelection(0);
        spinner2.setOnItemSelectedListener(this);
        /////////////////


        // dependentCount = findViewById(R.id.dep_count);
        // guestCount = findViewById(R.id.guest_count);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dep_ct >= 0 && dep_ct <= 4 && guest_ct >= 0 && guest_ct <= 6) {
                    if (isMemberAttending) {
                        if (guest_ct + dep_ct >= 0) {
                            if (isMemberAttending) {
                                totalPrice = depPrice + dep_ct * depPrice + guest_ct * guestPrice;
                            } else {
                                totalPrice = dep_ct * depPrice + guest_ct * guestPrice;
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(CountActivity.this);
                            builder.setTitle("Confirmation");
                            builder.setMessage("Total Price: ₹" + Long.toString(totalPrice));
                            builder.setCancelable(false);
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    transec t = new transec();
                                    if (isMemberAttending)
                                        t.setSeat_count(dep_ct + guest_ct + 1);
                                    else
                                        t.setSeat_count(dep_ct + guest_ct);
                                    t.setPrice(totalPrice);
                                    t.setPost_key(post_key);
                                    Intent i = new Intent(CountActivity.this, SeatBooking.class);
                                    i.putExtra("Object", t);
                                    startActivity(i);
                                }
                            });

                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                            builder.show();
                        } else
                            Toast.makeText(CountActivity.this, "Guest without Member/Dependant not permitted!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (guest_ct + dep_ct >= 1 && dep_ct > 0) {
                            if (isMemberAttending) {
                                totalPrice = depPrice + dep_ct * depPrice + guest_ct * guestPrice;
                            } else {
                                totalPrice = dep_ct * depPrice + guest_ct * guestPrice;
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(CountActivity.this);
                            builder.setTitle("Confirmation");
                            builder.setMessage("Total Price: " + Long.toString(totalPrice));
                            builder.setCancelable(false);
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    transec t = new transec();
                                    if (isMemberAttending)
                                        t.setSeat_count(dep_ct + guest_ct + 1);
                                    else
                                        t.setSeat_count(dep_ct + guest_ct);
                                    t.setPrice(totalPrice);
                                    t.setPost_key(post_key);
                                    Intent i = new Intent(CountActivity.this, SeatBooking.class);
                                    i.putExtra("Object", t);
                                    startActivity(i);
                                }
                            });

                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                            builder.show();
                        } else
                            Toast.makeText(CountActivity.this, "Guest without Member/Dependant not permitted!", Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(CountActivity.this, "Guest without Member/Dependant not permitted!", Toast.LENGTH_SHORT).show();


            }
        });

        cleanSeats();

    }

    private void cleanSeats() {



       // Log.e("curretTime",currentTimeStamp+"");

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Movies");
        mDatabase.keepSynced(true);

        final DatabaseReference reference = mDatabase.child(post_key).child("hall").child("Users");

        final DatabaseReference refr = FirebaseDatabase.getInstance().getReference().child("Movies").child(post_key).child("hall").child("Users");
        refr.child(-1+"").child("time").setValue(ServerValue.TIMESTAMP);


        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int x = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    concurrencyModel r = snapshot.getValue(concurrencyModel.class);
                  //  Log.e("times",r.getTime()+" "+r.getUser()+" "+r.getSeat());
                    if(x==0){
                        currentTimeStamp = r.getTime();
                        x++;
                    }
                    else {
                        if(currentTimeStamp - r.getTime()>300000){
                         //   Log.e("timeDiff",""+(r.getTime()-currentTimeStamp));
                            FirebaseDatabase.getInstance().getReference().child("Movies").child(post_key).child("hall").child("Users").child(r.getSeat()).removeValue();
                        }
                    }
                 //   Log.e("timeDiff",""+(r.getTime()-currentTimeStamp));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        switch (parent.getId()) {
            case R.id.spinner_dependants:
                dep_ct = position;
                // Whatever you want to happen when the second item gets selected
                break;
            case R.id.spinner_guests:
                guest_ct = position;

                // Whatever you want to happen when the second item gets selected
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.check_1:
                if (checked) {
                    isMemberAttending = true;
                } else {
                    isMemberAttending = false;
                    break;
                }

        }
    }

}
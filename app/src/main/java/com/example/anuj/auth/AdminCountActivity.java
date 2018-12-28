package com.example.anuj.auth;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.anuj.auth.SessionManagement.ServiceManager;
import com.example.anuj.auth.model.reciptModel;
import com.example.anuj.auth.model.transec;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminCountActivity extends AppCompatActivityExt implements AdapterView.OnItemSelectedListener {

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

    private EditText rsiId, mobNo;

    private String id = null;
    private String num = null;

    private reciptModel r = null;

    private LinearLayout rsi, book;

    boolean f = false;

    DatabaseReference mDatabaseReference, mDatabase, getmDatabaseMovie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_count);

        Utils.darkenStatusBar(this, R.color.colorPrimary);

        post_key = getIntent().getExtras().getString("post_key");
        movie_title = getIntent().getExtras().getString("movie_n");
        movie_date = getIntent().getExtras().getString("movie_d");

        confirmButton = findViewById(R.id.admin_confirm_button);

        rsi = findViewById(R.id.rsiInfo);
        book = findViewById(R.id.bookingInfo);

        rsiId = findViewById(R.id.admin_rsiID);
        mobNo = findViewById(R.id.admin_mobno);

        //////////////
        spinner1 = findViewById(R.id.admin_spinner_dependants);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array
                .dep_no, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        spinner1.setSelection(0);
        spinner1.setOnItemSelectedListener(this);
        /////////////////

        //////////////
        spinner2 = findViewById(R.id.admin_spinner_guests);
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

                if(f) {

                    //Toast.makeText(AdminCountActivity.this, Integer.toString(dep_ct), Toast.LENGTH_SHORT).show();
                    //Toast.makeText(AdminCountActivity.this, Integer.toString(guest_ct), Toast.LENGTH_SHORT).show();


                    if (dep_ct >= 0 && dep_ct <= 4 && guest_ct >= 0 && guest_ct <= 6) {
                        if (isMemberAttending) {
                            if (guest_ct + dep_ct >= 0) {
                                if (isMemberAttending) {
                                    totalPrice = depPrice + dep_ct * depPrice + guest_ct * guestPrice;
                                } else {
                                    totalPrice = dep_ct * depPrice + guest_ct * guestPrice;
                                }
                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminCountActivity.this);
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
                                        Intent i = new Intent(AdminCountActivity.this, SeatBooking.class);
                                        i.putExtra("Object", t);
                                        i.putExtra("rsiID", id);
                                        i.putExtra("mobno", num);
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
                                Toast.makeText(AdminCountActivity.this, "Guest without Member/Dependant not permitted!", Toast.LENGTH_SHORT).show();
                        } else {
                            if (guest_ct + dep_ct >= 1 && dep_ct > 0) {
                                if (isMemberAttending) {
                                    totalPrice = depPrice + dep_ct * depPrice + guest_ct * guestPrice;
                                } else {
                                    totalPrice = dep_ct * depPrice + guest_ct * guestPrice;
                                }
                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminCountActivity.this);
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
                                        Intent i = new Intent(AdminCountActivity.this, SeatBooking.class);
                                        i.putExtra("Object", t);
                                        i.putExtra("rsiID", id);
                                        i.putExtra("mobno", num);
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
                                Toast.makeText(AdminCountActivity.this, "Guest without Member/Dependant not permitted!", Toast.LENGTH_SHORT).show();
                        }
                    } else
                        Toast.makeText(AdminCountActivity.this, "Guest without Member/Dependant not permitted!", Toast.LENGTH_SHORT).show();
                }

                else {

                    id = rsiId.getText().toString();
                    num = mobNo.getText().toString();

                    if(!id.toUpperCase().equals(Global.AdminID)) {
                        if(id != null && id != "" && num != null && num != "") {
                            mDatabase = FirebaseDatabase.getInstance().getReference("UserSignIn");
                            mDatabase.keepSynced(true);

                            mDatabase.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.child(id.toUpperCase()).exists() && dataSnapshot.child(id.toUpperCase()).child("mobno").getValue().toString().equals(num)) {


                                        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Tickets").child(id.toUpperCase());
                                        mDatabaseReference.keepSynced(true);

                                        //Toast.makeText(AdminCountActivity.this, id, Toast.LENGTH_SHORT).show();

                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                boolean flag = true;

                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    reciptModel r = snapshot.getValue(reciptModel.class);
                                                    //Toast.makeText(AdminCountActivity.this, r.getMovieNmae(), Toast.LENGTH_SHORT).show();
                                                    if (r.getMovieNmae().equals(movie_title) && r.getDate().equals(movie_date)) {
                                                        Toast.makeText(AdminCountActivity.this, "Cannot book multiple tickets for the same person!", Toast.LENGTH_SHORT).show();
                                                        flag = false;
                                                        break;
                                                    }
                                                }

                                                if(flag) {

                                                    f = true;
                                                    confirmButton.setText("CONFIRM");
                                                    book.setVisibility(View.VISIBLE);
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    } else {
                                        Toast.makeText(AdminCountActivity.this, "Invalid ID or mobile number", Toast.LENGTH_SHORT).show();
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                        else {
                            Toast.makeText(AdminCountActivity.this, "All fields are necessary", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(AdminCountActivity.this, "Admin cannot book tickets for itself!", Toast.LENGTH_SHORT).show();
                    }


                }


            }
        });

    }


    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        switch (parent.getId()) {
            case R.id.admin_spinner_dependants:
                dep_ct = position;
                // Whatever you want to happen when the second item gets selected
                break;
            case R.id.admin_spinner_guests:
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
            case R.id.admin_check_1:
                if (checked) {
                    isMemberAttending = true;
                } else {
                    isMemberAttending = false;
                    break;
                }

        }
    }
}

package com.example.anuj.auth;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anuj.auth.SessionManagement.ServiceManager;
import com.example.anuj.auth.SessionManagement.SessionManager;
import com.example.anuj.auth.model.reciptModel;
import com.example.anuj.auth.smsService.sms;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class Ticket extends AppCompatActivityExt {

    TextView movieName, date, time, userID, cost, seatList;
    ImageView qrImage;
    String text = null;
    reciptModel r = null;

    private static final String TAG = "MainActivity";

    private String payeeAddress = "8698670658@upi";
    private String payeeName = "RAJ KUMAR";
    private String transactionNote = "Movie Seat Booking";
    private String amount = "0";
    private String currencyUnit = "INR";
    int selectedSeats = 0, totalSeats = 7;
    final int maxSeats = 10;
    List<String> mappedValues = new ArrayList<String>();
    List<String> seatNum = new ArrayList<String>();

    private DatabaseReference mDatabase;

    Gson gson = null;

    private String post_key = null;

    private String id, mob = null;

    static SessionManager sessionManager;

    Button confirm;
    boolean flag = false;
    boolean test_flag = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipt);

        Utils.darkenStatusBar(this, R.color.colorPrimary);

        post_key = getIntent().getExtras().getString("post_key");

        sessionManager = new SessionManager(this);


        gson = new Gson();

        r = (reciptModel) getIntent().getSerializableExtra("Recipt");
        amount = Integer.toString(r.getCost());

        confirm = findViewById(R.id.payMoney);

        try {
            //15 minutes
            if (System.currentTimeMillis() + 1800000 >= formatDate(r.getDate(), r.getMovietime())) {
                Toast.makeText(this, "Cannot book Tickets 30 min before the show!", Toast.LENGTH_SHORT).show();
                confirm.setVisibility(View.GONE);
                test_flag = false;
            }

        }
        catch (Exception e) {

        }

        if(sessionManager.getMemberShipNo().equals(Global.AdminID)) {

            id = getIntent().getExtras().getString("rsiID");
            mob = getIntent().getExtras().getString("mobno");
            // mob = SessionManager.getPno();
            r.setUserID(id);
            r.setProvisional(true);

            // Toast.makeText(this, id, Toast.LENGTH_SHORT).show();

            confirm.setText("Confirm Booking");
            flag = true;

        }
        else {
            r.setProvisional(false);
        }

        mapSeat();

        getInstances();

        seatNum = r.getSeatsList();

        checkValidity(seatNum);

        setValues();

        if(sessionManager.getMemberShipNo().equals(Global.AdminID)) {
            try {
                GenrateQR();
            } catch (WriterException e) {
                Toast.makeText(Ticket.this, "error generating qr !", Toast.LENGTH_LONG);
            }
        }

//        addTicket();

        if(test_flag) {
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    try {
                        //15 minutes
                        if (System.currentTimeMillis() + 1800000 >= formatDate(r.getDate(), r.getMovietime())) {
                            Toast.makeText(Ticket.this, "Cannot book Tickets 30 min before the show!", Toast.LENGTH_SHORT).show();
                            confirm.setVisibility(View.GONE);
                            test_flag = false;
                            flag = true;
                        }

                    }
                    catch (Exception e) {

                    }

                    if(!flag) {
                        Uri uri = Uri.parse("upi://pay?pa=" + payeeAddress + "&pn=" + payeeName + "&tn=" + transactionNote +
                                "&am=" + amount + "&cu=" + currencyUnit);


                      //  Log.d(TAG, "onClick: uri: " + uri);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        Intent chooser = Intent.createChooser(intent, "Pay with...");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            startActivityForResult(chooser, 1);


                        }
                    }
                    else {
                        if(sessionManager.getMemberShipNo().equals(Global.AdminID)) {
                            confirmBook();
                        }
                    }

                }
            });
        }

    }

    void checkValidity(final List<String> seats){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Movies").child(post_key).child("hall").child("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> s = seats;
                for(int i = 0;i<s.size();i++){
                    String a = dataSnapshot.child(seats.get(i)+"").child("user").getValue(String.class);
                    if(SessionManager.getMemberShipNo().equals(Global.AdminID)) {
                        if(!r.getUserID().equals(a)){
                            //Toast.makeText(Ticket.this, "Please Select Seats Again !", Toast.LENGTH_SHORT).show();

                            // show alert and go back
                            AlertDialog.Builder Alert = new AlertDialog.Builder(Ticket.this);
                            Alert.setCancelable(false)
                                    .setTitle("ALERT!!")
                                    .setMessage("Your all seats are not available please select again and don't select "+mappedValues.get(Integer.parseInt(seats.get(i))));
                            Alert.setNegativeButton("Select Again", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    finish();
                                }
                            });
                            Alert.create();
                            Alert.show();

                        }
                    }
                    else {
                        if(!SessionManager.getMemberShipNo().equals(a)){
                            //Toast.makeText(Ticket.this, "Please Select Seats Again !", Toast.LENGTH_SHORT).show();

                            // show alert and go back
                            AlertDialog.Builder Alert = new AlertDialog.Builder(Ticket.this);
                            Alert.setCancelable(false)
                                    .setTitle("ALERT!!")
                                    .setMessage("Your all seats are not available please select again and don't select "+mappedValues.get(Integer.parseInt(seats.get(i))));
                            Alert.setNegativeButton("Select Again", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    finish();
                                }
                            });
                            Alert.create();
                            Alert.show();

                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    void mapSeat() {
        int i, j = 0, k = 1, flg = 0;    // 0-> 22 ****** 1-> 20
        for (i = 0; i < 358; i++) {

            if (flg == 0) {

                mappedValues.add((char) ('B' + j) + "" + k);
                k++;

            }
            if (flg == 1) {

                mappedValues.add((char) ('B' + j) + "" + k);
                k++;

            }

            if (k == 23 && flg == 0) {
                j++;
                k = 1;
                flg = 1;
            }
            if (k == 21 && flg == 1) {
                j++;
                k = 1;
                flg = 0;
            }


           // Log.e("map", mappedValues.get(i));
        }

    }

    void confirmBook() {

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Movies");
        mDatabase.keepSynced(true);

        DatabaseReference reference = mDatabase.child(post_key).child("hall").child("status");
/*
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference().child("hallB").child("status");
*/
        for (int i = 0; i < seatNum.size(); i++) {
            reference.child(seatNum.get(i)).setValue("R");
        }

        addTicket();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       // Log.d(TAG, "onActivityResult: requestCode: " + requestCode);
        //Log.d(TAG, "onActivityResult: resultCode: " + resultCode);
        //txnId=UPI20b6226edaef4c139ed7cc38710095a3&responseCode=00&ApprovalRefNo=null&Status=SUCCESS&txnRef=undefined
        //txnId=UPI608f070ee644467aa78d1ccf5c9ce39b&responseCode=ZM&ApprovalRefNo=null&Status=FAILURE&txnRef=undefined

        if (data != null) {
           // Log.d(TAG, "onActivityResult: data: " + data.getStringExtra("response"));
            String res = data.getStringExtra("response");
            String search = "SUCCESS";
            if (res.toLowerCase().contains(search.toLowerCase())) {
                Toast.makeText(this, "Payment Successful", Toast.LENGTH_LONG).show();
//                SessionManager.AddRecipt(r);
                //addTicket();

                try {
                    GenrateQR();
                } catch (WriterException e) {
                    Toast.makeText(Ticket.this, "Error generating qr !", Toast.LENGTH_LONG);
                }

                confirmBook();
            } else {
                Toast.makeText(this, "Payment Failed!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean addTicket() {

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Tickets").child(r.getUserID());

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                DatabaseReference newTicket = mDatabase.push();
                newTicket.setValue(r);

                sendSms();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (sessionManager.getMemberShipNo().equals(Global.AdminID)) {
            final DatabaseReference mDatabaseAdmin = FirebaseDatabase.getInstance().getReference().child("Tickets").child(Global.AdminID);

            mDatabaseAdmin.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    DatabaseReference newTicket = mDatabaseAdmin.push();
                    newTicket.setValue(r);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        Intent i = new Intent(Ticket.this, MovieActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        startActivity(i);

        return true;
    }

    public static Long formatDate (String date, String time) throws ParseException {

        try {
            DateFormat formatter;
            formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date _date = formatter.parse(date + " " + time + ":00");
            java.sql.Timestamp timeStampDate = new Timestamp(_date.getTime());

//            Toast.makeText(this, timeStampDate.toString(), Toast.LENGTH_SHORT).show();
            return timeStampDate.getTime();
        } catch (ParseException e) {
            System.out.println("Exception :" + e);
        }

        return Long.parseLong("0");
    }

    private void sendSms() {
        String yourSeat = "";
        List<String> l = r.getSeatsList();
        for(int i=0;i<l.size();i++){
            yourSeat+=mappedValues.get(Integer.parseInt(l.get(i)))+", ";
        }
        String msg = "Hi "+r.getUserID()+", Your Tickets for the movie "+r.getMovieNmae()+" have been booked. Your seats are: "+yourSeat+". Show starts at "+r.getMovietime()+", on "+r.getDate()+" Enjoy the show, regards RSAMI.";
        // String msg = "ticket booked "+r.getMovieNmae();
        if(sessionManager.getMemberShipNo().equals(Global.AdminID)) {
            //msg = "Hi "+r.getUserID()+", Your Tickets for the movie "+r.getMovieNmae()+" have been booked. Your seats are: "+yourSeat+". Show starts at "+r.getMovietime()+", on "+r.getDate()+" Provisional Ticket, " + amount + " ₹ to be paid on arrival, regards RSAMI.";
            msg = "Dear, "+r.getUserID().toUpperCase()+" "+r.getMovieNmae().toUpperCase()+",%n"+yourSeat+" booked.%nTime "+r.getMovietime()+"hrs "+r.getDate()+"%nPROVISIONAL TICKET, payment for ₹" + amount + " will be deduced.";
            new Thread(new sms(msg,mob)).start();
        }
        else {
            new Thread(new sms(msg,SessionManager.getPno())).start();
        }
    }

    private void GenrateQR() throws WriterException {
        qrImage.setVisibility(View.VISIBLE);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
//        text = r.getMovieNmae() + r.getTimestamp() + r.getUserID();
        String text = gson.toJson(r);
        BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,
                500, 500);
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        Bitmap bitmap = null;
        bitmap = barcodeEncoder.createBitmap(bitMatrix);
        qrImage.setImageBitmap(bitmap);
    }

    private void setValues() {
        movieName.setText(r.getMovieNmae());
        date.setText(r.getDate());
        time.setText(r.getMovietime() + " hrs");
        userID.setText(r.getUserID());
        cost.setText(String.valueOf(r.getCost()));
        String str = null;
        str = "";
        List<String> list = r.getSeatsList();
        Iterator it = list.iterator();
        int c = 0;
        seatList.setText("");
        while (it.hasNext() && c < 10) {
            str += mappedValues.get(Integer.parseInt(it.next().toString())) + ", ";
            c++;
        }
        str = str.substring(0, str.length() - 2);
        seatList.setText(str);
    }

    private void getInstances() {
        movieName = findViewById(R.id.movieName);
        date = findViewById(R.id.movieDate);
        time = findViewById(R.id.movieTime);
        userID = findViewById(R.id.userID);
        cost = findViewById(R.id.cost);
        seatList = findViewById(R.id.seatsList);
        qrImage = findViewById(R.id.qrImage);
    }
}
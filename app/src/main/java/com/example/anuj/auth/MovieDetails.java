package com.example.anuj.auth;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anuj.auth.SessionManagement.ServiceManager;
import com.example.anuj.auth.SessionManagement.SessionManager;
import com.example.anuj.auth.model.reciptModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.ParseException;

public class MovieDetails extends AppCompatActivityExt {

    private static TextView uiname, uidate, uitime, dur, lang, cert;
    private static ImageView imageMainPage;
    private static Button watchTrailer, bookNow;

    private String post_key = null;

    private DatabaseReference mDatabase;
    DatabaseReference mDatabaseTickets;

    SessionManager sessionManager;

    String movie_title;
    String movie_date;
    String movie_time;
    String movie_image;

    String movie_duration;
    String movie_language;
    String movie_certification;

    String movie_trailer;

    private String id;

    private boolean flag = true, processed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Utils.darkenStatusBar(this, R.color.colorPrimary);

        uiname = findViewById(R.id.movieName);
        uidate = findViewById(R.id.movieDate);
        uitime = findViewById(R.id.movie_timing);

        dur = findViewById(R.id.durationID);
        lang = findViewById(R.id.language);
        cert = findViewById(R.id.certification);

//        watchTrailer = findViewById(R.id.watchTrailer);
        bookNow = findViewById(R.id.bookNow);
        imageMainPage = findViewById(R.id.imageMainPage);

        id = sessionManager.getMemberShipNo();

        post_key = getIntent().getExtras().getString("post_key");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Movies");
        mDatabase.keepSynced(true);

        mDatabaseTickets = FirebaseDatabase.getInstance().getReference().child("Tickets").child(id);
        mDatabaseTickets.keepSynced(true);


//        Toast.makeText(this, post_key, Toast.LENGTH_SHORT).show();

        if (post_key != null) {
            mDatabase.child(post_key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    movie_title = dataSnapshot.child("name").getValue().toString();
                    movie_date = dataSnapshot.child("date").getValue().toString();
                    movie_time = dataSnapshot.child("timing").getValue().toString();
                    movie_image = dataSnapshot.child("image_url").getValue().toString();

                    movie_duration = dataSnapshot.child("duration").getValue().toString();
                    movie_language = dataSnapshot.child("language").getValue().toString();
                    movie_certification = dataSnapshot.child("certification").getValue().toString();

//                    movie_trailer = dataSnapshot.child("trailer").getValue().toString();

                    uiname.setText(movie_title);
                    uidate.setText(movie_date);
                    uitime.setText(movie_time + " hrs");

                    dur.setText(movie_duration + " hrs");
                    lang.setText(movie_language);
                    cert.setText(movie_certification);


                    Picasso.get().load(movie_image).networkPolicy(NetworkPolicy.OFFLINE).into(imageMainPage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(movie_image).into(imageMainPage);
                        }
                    });


                    imageMainPage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        if(!id.equals(Global.AdminID)) {
            mDatabaseTickets.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        reciptModel r = snapshot.getValue(reciptModel.class);
                        if (r.getMovieNmae().equals(movie_title) && r.getDate().equals(movie_date)) {
                            bookNow.setBackgroundColor(Color.GRAY);
                            bookNow.setText("Already Booked");
                            flag = false;
                            break;
                        }
                    }
                    processed = true;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

/*
        watchTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(movie_trailer)));
            }
        });

*/

        bookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(id.equals(Global.AdminID)) {
                    Intent i = new Intent(MovieDetails.this, AdminCountActivity.class);
                    i.putExtra("post_key", post_key);
                    i.putExtra("movie_n", movie_title);
                    i.putExtra("movie_d", movie_date);
                    startActivity(i);
                }
                else {
                    if (flag) {
                        if (processed) {
                            Intent i = new Intent(MovieDetails.this, CountActivity.class);
                            i.putExtra("post_key", post_key);
                            startActivity(i);
                        } else {
                            Toast.makeText(MovieDetails.this, "Connection Slow, please wait!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MovieDetails.this, "Cannot book twice for the same show!!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        flag = true;
        processed = false;
    }
}

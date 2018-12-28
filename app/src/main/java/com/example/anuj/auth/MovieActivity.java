package com.example.anuj.auth;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anuj.auth.SessionManagement.ServiceManager;
import com.example.anuj.auth.SessionManagement.SessionManager;
import com.example.anuj.auth.model.MovieContent;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MovieActivity extends AppCompatActivityExt {


    ProgressDialog mProgress;

    private RecyclerView movie_list_view;

    private Query query;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<MovieContent, MovieViewHolder> adapter;
    private FirebaseAuth mAuth;

    private Button removeDoneBtn;
    public static boolean hide = true;
    String em, pa, id;

    SessionManager sessionManager;

    String modelDateTime = null;
    String curDateTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        // Pass on the activity and color resourse
        Utils.darkenStatusBar(this, R.color.colorPrimary);


        sessionManager = new SessionManager(this);

        id = SessionManager.getMemberShipNo();

        //Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show();

        mAuth = FirebaseAuth.getInstance();

        Calendar calendar = Calendar.getInstance();

        int _year = calendar.get(Calendar.YEAR);
        int _month = calendar.get(Calendar.MONTH);
        int _day = calendar.get(Calendar.DAY_OF_MONTH);

        String date = _day + "-" + _month + "-" + _year;
/*
        try {
            DateFormat formatter;
            formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date _date = (Date) formatter.parse("24-12-2018 19:00:00");
            java.sql.Timestamp timeStampDate = new java.sql.Timestamp(_date.getTime());


            Toast.makeText(this, Long.toString(timeStampDate.getTime()), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, Long.toString(System.currentTimeMillis()), Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            System.out.println("Exception :" + e);
        }

        try {
            Long i = formatDate("24-12-2018", "14:00");
            if(System.currentTimeMillis() > i)
                Toast.makeText(this, "asdasdasdasdasd", Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            e.printStackTrace();
        }
*/
        query = FirebaseDatabase.getInstance().getReference().child("Movies").orderByChild("date").startAt(date);
        query.keepSynced(true);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Movies");
        mDatabase.keepSynced(true);

        mStorage = FirebaseStorage.getInstance().getReference();

        removeDoneBtn = findViewById(R.id.remove_done);
        removeDoneBtn.setVisibility(View.GONE);

        movie_list_view = findViewById(R.id.movie_view_list);
        movie_list_view.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<MovieContent> options = new FirebaseRecyclerOptions.Builder<MovieContent>()
                .setQuery(query, MovieContent.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<MovieContent, MovieViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MovieViewHolder holder, int position, @NonNull final MovieContent model) {

                final String post_key = getRef(position).getKey();

                holder.setTitle(model.getName());
                holder.setDate(model.getDate());
                holder.setTime(model.getTiming());
                holder.setImage(getApplicationContext(), model.getImage_url());
                holder.setDeleteButton();

                try {
                                                    //15 minutes
                    if (System.currentTimeMillis() + 900000 >= formatDate(model.getDate(), model.getTiming())) {
                        holder.mView.setVisibility(View.GONE);
                        holder.mView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                    }

                }
                catch (Exception e) {

                }



                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent postIntent = new Intent(MovieActivity.this, MovieDetails.class);
                        postIntent.putExtra("post_key", post_key);
                        startActivity(postIntent);
                    }
                });

                holder.removeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(MovieActivity.this);
                        builder.setTitle("Confirmation");
                        builder.setMessage("Confirm delete " + (model.getName()));
                        builder.setCancelable(false);
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDatabase.child(post_key).removeValue();
                            }
                        });

                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        builder.show();
                    }
                });

            }

            @NonNull
            @Override
            public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.movie_view, parent, false);

                return new MovieViewHolder(view);
            }
/*
            @NonNull
            @Override
            public MovieContent getItem(int position) {
                return super.getItem(getItemCount() -1 - position);
            }
*/
        };

        movie_list_view.setAdapter(adapter);
//        adapter.startListening();

        removeDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MovieActivity.this, "remove done", Toast.LENGTH_SHORT).show();
//                removeBtn.setVisibility(View.GONE);
                hide = true;
                removeDoneBtn.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
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


    public static class MovieViewHolder extends RecyclerView.ViewHolder {

        View mView;
        Button removeBtn;

        public MovieViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setTitle(String mTitle) {
            TextView blog_title = mView.findViewById(R.id.movie_title);
            blog_title.setText(mTitle);
        }

        public void setDate(String mDate) {
            TextView blog_cont = mView.findViewById(R.id.movie_date);
            blog_cont.setText(mDate);
        }

        public void setTime(String mTime) {
            TextView user_name_txt = mView.findViewById(R.id.movie_time);
            user_name_txt.setText(mTime + " hrs");
        }

        public void setDeleteButton() {
            removeBtn = mView.findViewById(R.id.remove_btn);
            if (!hide)
                removeBtn.setVisibility(View.VISIBLE);
            else
                removeBtn.setVisibility(View.GONE);
        }

        public void setImage(final Context context, final String mUrl) {

            final ImageView movie_image = mView.findViewById(R.id.movie_image);
            Picasso.get().load(mUrl).placeholder(R.drawable.insert_photo).networkPolicy(NetworkPolicy.OFFLINE).into(movie_image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(mUrl).into(movie_image);
                }
            });

            movie_image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!id.equals(Global.AdminID)) {
            MenuItem menuItem1 = menu.findItem(R.id.action_add);
            menuItem1.setVisible(false);
            MenuItem menuItem2 = menu.findItem(R.id.action_remove);
            menuItem2.setVisible(false);
            MenuItem menuItem3 = menu.findItem(R.id.action_scanner);
            menuItem3.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_add) {
            startActivity(new Intent(MovieActivity.this, AddMovie.class));
        }

        if (item.getItemId() == R.id.action_remove) {
            hide = false;
            removeDoneBtn.setVisibility(View.VISIBLE);
            //Toast.makeText(this, "remove initiated", Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
        }

        if (item.getItemId() == R.id.action_logout) {

            mProgress = new ProgressDialog(this);
            mProgress.setMessage("Signing Out");
            mProgress.show();

            sessionManager.logoutUser();

/*
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Intent logoutIntent = new Intent(MovieActivity.this, LoginActivity.class);
                    logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    mProgress.dismiss();
                    startActivity(logoutIntent);
                    //finish();
                }
            });
            */
        }

        if (item.getItemId() == R.id.action_scanner) {
            startActivity(new Intent(MovieActivity.this, QrScanActivity.class));
        }

        if (item.getItemId() == R.id.action_show_tickets) {
            startActivity(new Intent(MovieActivity.this, MyTickets.class));
        }

        return super.onOptionsItemSelected(item);
    }
}

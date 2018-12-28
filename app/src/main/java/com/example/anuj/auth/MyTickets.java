package com.example.anuj.auth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.example.anuj.auth.SessionManagement.ServiceManager;
import com.example.anuj.auth.SessionManagement.SessionManager;
import com.example.anuj.auth.model.TicketContent;
import com.example.anuj.auth.model.reciptModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MyTickets extends AppCompatActivityExt {

    SessionManager sessionManager;

    private RecyclerView ticket_list_view;

    private String id = "";

    private Query query;
    private FirebaseRecyclerAdapter<TicketContent, TicketViewHolder> adapter;
    private DatabaseReference mDatabaseTickets;
    static List<String> mappedValues = new ArrayList<String>();

    Gson gson = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);

        Utils.darkenStatusBar(this, R.color.colorPrimary);

        sessionManager = new SessionManager(this);

        id = SessionManager.getMemberShipNo();

        gson = new Gson();

        mapSeat();

        if(sessionManager.getMemberShipNo().equals(Global.AdminID)) {
            query = FirebaseDatabase.getInstance().getReference().child("Tickets").child(sessionManager.getMemberShipNo()).orderByChild("userID");
        }
        else {
            query = FirebaseDatabase.getInstance().getReference().child("Tickets").child(sessionManager.getMemberShipNo()).orderByChild("date");
        }
        query.keepSynced(true);

        mDatabaseTickets = FirebaseDatabase.getInstance().getReference().child("Tickets").child(id);
        mDatabaseTickets.keepSynced(true);

        ticket_list_view = findViewById(R.id.ticket_view_list);
        ticket_list_view.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<TicketContent> options = new FirebaseRecyclerOptions.Builder<TicketContent>()
                .setQuery(query, TicketContent.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<TicketContent, TicketViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final TicketViewHolder holder, int position, @NonNull TicketContent model) {
                final String post_key = getRef(position).getKey();

                reciptModel r = new reciptModel();

                r.setCost(model.getCost());
                r.setDate(model.getDate());
                r.setMovieNmae(model.getMovieNmae());
                r.setMovietime(model.getMovietime());
                r.setSeatsList(model.getSeatsList());
                r.setTimestamp(model.getTimestamp());
                r.setUserID(model.getUserID());
                r.setProvisional(model.isProvisional());

                String text = gson.toJson(r);

                holder.setImage(MyTickets.this, text);
                holder.setTitle(model.getMovieNmae());
                holder.setDate(model.getDate());
                holder.setTime(model.getMovietime());
                holder.setSeats(model.getSeatsList());

                if(model.isProvisional()) {
                    holder.mView.findViewById(R.id.ticket_view).setBackgroundColor(getResources().getColor(R.color.colourRed));
                    LinearLayout pro_layout = holder.mView.findViewById(R.id.pro_id);
                    pro_layout.setVisibility(View.VISIBLE);

                    TextView rsi_id_text = pro_layout.findViewById(R.id.ticket_rsi_id);

                    rsi_id_text.setText(model.getUserID());

                }

                holder.qr_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.imagePopup.viewPopup();
                    }
                });
            }

            @NonNull
            @Override
            public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.ticket_view, parent, false);

                return new TicketViewHolder(view);
            }
        };

        ticket_list_view.setAdapter(adapter);


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

    public static class TicketViewHolder extends RecyclerView.ViewHolder {

        View mView;
        ImageView qr_image;
        ImagePopup imagePopup;

        public TicketViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            qr_image = mView.findViewById(R.id.ticket_movie_qr_image);

        }

        public void setTitle(String mTitle) {
            TextView movie_title = mView.findViewById(R.id.ticket_movie_title);
            movie_title.setText(mTitle);
        }

        public void setDate(String mDate) {
            TextView movie_date = mView.findViewById(R.id.ticket_movie_date);
            movie_date.setText(mDate);
        }

        public void setTime(String mTime) {
            TextView movie_time = mView.findViewById(R.id.ticket_movie_time);
            movie_time.setText(mTime + " hrs");
        }

        public void setSeats(List<String> seatsList) {
            TextView seatList = mView.findViewById(R.id.ticket_movie_seats);

            Iterator it = seatsList.iterator();
            int c = 0;
            String str = "";
            seatList.setText("");
            while (it.hasNext() && c <= 11) {
                str += mappedValues.get(Integer.parseInt(it.next().toString())) + ", ";
                c++;
            }
            str = str.substring(0, str.length() - 2);
            seatList.setText(str);

        }

        public void setImage(final Context context, final String text) {

            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = null;
            try {
                bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 500, 500);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = null;
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qr_image.setImageBitmap(bitmap);

            qr_image.setScaleType(ImageView.ScaleType.FIT_CENTER);

            imagePopup = new ImagePopup(context);

            imagePopup.setWindowHeight(800); // Optional
            imagePopup.setWindowWidth(800); // Optional
            imagePopup.setBackgroundColor(Color.BLACK);  // Optional
            imagePopup.setFullScreen(true); // Optional
            imagePopup.setHideCloseIcon(true);  // Optional
            imagePopup.setImageOnClickClose(true);  // Optional

            imagePopup.initiatePopup(this.qr_image.getDrawable());

        }

    }
}

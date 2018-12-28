package com.example.anuj.auth;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.anuj.auth.SessionManagement.ServiceManager;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class AddMovie extends AppCompatActivityExt {

    private static final int GALLERY_REQUEST_CODE = 1001;

    private ImageButton addImg;
    private EditText titleTxt;
    private EditText dateTxt;
    private EditText timeTxt;
    private EditText duration, language, certificate, trailerUrl;

    private Button addBtn;

    private Uri uri = null;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private ProgressDialog mProgress;

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private int _year, _month, _day;
    private int booking_limit = 5;

    TimePickerDialog timePickerDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);

        Utils.darkenStatusBar(this, R.color.colorPrimary);

        addImg = findViewById(R.id.add_image_btn);
        titleTxt = findViewById(R.id.title_txt);
        dateTxt = findViewById(R.id.date_txt);
        timeTxt = findViewById(R.id.time_txt);

        duration = findViewById(R.id.durationID);
        language = findViewById(R.id.langID);
        certificate = findViewById(R.id.catID);
//        trailerUrl = findViewById(R.id.trailerUrlID);

        addBtn = findViewById(R.id.add_btn);

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Movies");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        //mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("users").child(mUser.getUid().toString());

        mProgress = new ProgressDialog(AddMovie.this);

        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });


        dateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                _year = calendar.get(Calendar.YEAR);
                _month = calendar.get(Calendar.MONTH);
                _day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(AddMovie.this,
                        mDateSetListener,
                        _year, _month, _day);

                //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialog.show();
            }
        });

        timeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog = new TimePickerDialog(AddMovie.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String h = "";
                        String m = "";
                        if(hourOfDay/10 == 0)
                            h = "0"+hourOfDay;
                        else
                            h = hourOfDay+"";
                        if(minute/10 == 0)
                            m = "0"+minute;
                        else
                            m = minute+"";

                        timeTxt.setText(h + ":" + m);
                    }
                }, 0, 0, false);

                timePickerDialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = dayOfMonth + "-" + month + "-" + year;
                if (_year <= year && _month <= month && dayOfMonth - _day < booking_limit && dayOfMonth - _day >= 0)
                    dateTxt.setText(date);
                else {
                    dateTxt.setText("");
                    Toast.makeText(AddMovie.this, "Invalid date", Toast.LENGTH_SHORT).show();
                }

            }
        };


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String title_val = titleTxt.getText().toString();
                final String date_val = dateTxt.getText().toString();
                final String time_val = timeTxt.getText().toString();

                final String dur = duration.getText().toString();
                final String lang = language.getText().toString();
                final String cert = certificate.getText().toString();
//                final String trailer = trailerUrl.getText().toString();

                String _date[] = date_val.split("-");

                if (_date.length == 3 && _year <= Integer.parseInt(_date[2]) && _month <= Integer.parseInt(_date[1]) && Integer.parseInt(_date[0]) - _day < booking_limit && Integer.parseInt(_date[0]) - _day >= 0) {
                    if (!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(date_val) && !TextUtils.isEmpty(time_val) && uri != null) {

                        String rand_name = UUID.randomUUID().toString();

                        final StorageReference path = mStorage.child("movie_images").child(rand_name);

                        mProgress.setMessage("Adding Movie");
                        mProgress.show();

                        path.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                return path.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    final Uri imageUrl = task.getResult();

                                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            DatabaseReference newMovie = mDatabase.push();

                                            newMovie.child("name").setValue(title_val);
                                            newMovie.child("date").setValue(date_val);
                                            newMovie.child("timing").setValue(time_val);
                                            newMovie.child("image_url").setValue(imageUrl.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mProgress.dismiss();
                                                    Toast.makeText(AddMovie.this, "Movie Added", Toast.LENGTH_SHORT).show();

                                                    finish();
                                                }
                                            });

                                            DatabaseReference ref = newMovie.child("hall").child("status");
                                            int i, j, k;
                                            for (i = 0; i < 358; i++) {
                                                ref.child(i + "").setValue("A");
                                            }

                                            newMovie.child("duration").setValue(dur);
                                            newMovie.child("language").setValue(lang);
                                            newMovie.child("certification").setValue(cert);
//                                        newMovie.child("trailer").setValue(trailer);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                } else {
                                    Toast.makeText(AddMovie.this, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } else {
                        Toast.makeText(AddMovie.this, "Must add something to each field!!!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(AddMovie.this, "Invalid date!", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            uri = data.getData();
            addImg.setImageURI(uri);
            addImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

    }
}

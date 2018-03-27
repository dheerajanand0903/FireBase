package com.example.dheeraj.college;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class singlePostActivity extends AppCompatActivity {

    private String post_key = null;
    private DatabaseReference databaseReference;
    private ImageView imageView;
    private Button applyButton;
    ;
    private Toolbar toolbar;

    Bitmap bitmap1, bitmap2;
    DisplayMetrics displayMetrics;
    int width, height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);


        toolbar = (Toolbar) findViewById(R.id.toolbarSinglePost);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Initializing Views....
        imageView = (ImageView) findViewById(R.id.imageSinglePost);
        applyButton = (Button) findViewById(R.id.applyButton);


        post_key = getIntent().getExtras().getString("Post Id");


        databaseReference = FirebaseDatabase.getInstance().getReference().child("/Personal");
        databaseReference.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String post_image = (String) dataSnapshot.child("img").getValue();

                Glide.with(singlePostActivity.this)
                        .load(post_image)
                        .into(imageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //On Button Clicked**********************

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //    callLoginDialog();
//                Intent intent=new Intent(singlePostActivity.this,profile.class);
//                intent.putExtra("Post Id",post_key);
//                startActivity(intent);


                WallpaperManager myWallpaperManager
                        = WallpaperManager.getInstance(getApplicationContext());

                try {
                    bitmap1 = ( (BitmapDrawable) imageView.getDrawable() ).getBitmap();

                    if (bitmap1 != null)
                        GetScreenWidthHeight();
                    SetBitmapSize();
                    myWallpaperManager.setBitmap(bitmap2);
                    myWallpaperManager.suggestDesiredDimensions(width, height);
                    Toast.makeText(singlePostActivity.this, "Wallpaper Set Successfully", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    //e.printStackTrace();

                    String error=e.getMessage().trim();
                    Toast.makeText(singlePostActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void GetScreenWidthHeight() {

        displayMetrics = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        width = displayMetrics.widthPixels;

        height = displayMetrics.heightPixels;

    }

    public void SetBitmapSize() {

        bitmap2 = Bitmap.createScaledBitmap(bitmap1, width, height, false);

    }

}

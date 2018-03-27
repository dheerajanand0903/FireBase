package com.example.dheeraj.college;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import static android.widget.GridLayout.VERTICAL;

public class LoginActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static Bundle mBundleRecyclerViewState;
    public FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    static boolean calledAlready = false;
    private static String List_State_key="RecyclerState";
    Parcelable mListState;

    private RecyclerView mList_item;
    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("My World");
        setSupportActionBar(toolbar);


        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(LoginActivity.this);

        Firebase.setAndroidContext(this);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        /*
        Button signOut=(Button)findViewById(R.id.buttonLogOut);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));

            }
        });*/


        //Recycler View
        mList_item = findViewById(R.id.recyclerView);
        mList_item.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, VERTICAL, false);
 //       gridLayoutManager.setReverseLayout(true);
//        gridLayoutManager.setStackFromEnd(true);
        mList_item.setLayoutManager(gridLayoutManager);

        //Swipe Refresh layout



        //Send A Query in the database

        if (!calledAlready) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }

        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference().child("/Personal");

        //query=firebaseFirestore.collection("Posts");

    }


    @Override
    protected void onStart() {
        super.onStart();


        //Building Lists**********************

        FirebaseRecyclerAdapter <listClass, listViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter <listClass, listViewHolder>(
                        listClass.class,
                        R.layout.list_item,
                        listViewHolder.class,
                        databaseReference
                ) {
                    @Override
                    protected void populateViewHolder(listViewHolder viewHolder, listClass model, final int position) {

                        final String postKey = getRef(position).getKey();

                        progressBar.setVisibility(View.GONE);

                        viewHolder.setImage(model.getImg());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(LoginActivity.this, singlePostActivity.class);
                                intent.putExtra("Post Id", postKey);
                                startActivity(intent);
                            }
                        });

                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                                String error = databaseError.getMessage();
                                Toast.makeText(LoginActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                };

        mList_item.setAdapter(firebaseRecyclerAdapter);


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.item1:
                Intent intent = new Intent(LoginActivity.this, profile.class);
                startActivity(intent);
                break;
            case R.id.signOutBtn:
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //View Holder For RecyclerView
    public static class listViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public listViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setImage(String img) {
            ImageView imageView = itemView.findViewById(R.id.image);
            Picasso.with(mView.getContext())
                    .load(img)
                    .into(imageView);

        }
    }


    @Override
    protected void onPause()
    {
        super.onPause();

        // save RecyclerView state
        mBundleRecyclerViewState = new Bundle();
        Parcelable listState = mList_item.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(List_State_key, listState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // restore RecyclerView state
        if (mBundleRecyclerViewState != null) {
            Parcelable listState = mBundleRecyclerViewState.getParcelable(List_State_key);
            mList_item.getLayoutManager().onRestoreInstanceState(listState);
        }
    }

}

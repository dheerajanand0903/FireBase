package com.example.dheeraj.college;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile extends LoginActivity {

    private CircleImageView setupImage;
    private Uri resultUri = null;
    private Boolean isChanged = false;
    private Boolean isClicked=false;
    private ProgressBar progressBar;
    private EditText nameEditText;
    private EditText ageEditText;
    private String name;
    private String age;
    private String userID;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("Profile");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();


        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        //Enabling offline capabilities
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);

        //setting up views
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        nameEditText = findViewById(R.id.userName_editText);
        ageEditText = findViewById(R.id.age);

        setupImage = findViewById(R.id.imageUpload);
        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Checking and requesting runtime permissions

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(profile.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(profile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {

                        //Start Cropping Image

                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1, 1)
                                .start(profile.this);
                    }
                }
            }
        });

        //Fetching From FireBase

        firebaseFirestore.collection("Users").document(userID).get().addOnCompleteListener(new OnCompleteListener <DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task <DocumentSnapshot> task) {

                if(isClicked){
                    finish();
                }else {


                    if (task.isSuccessful()) {

                        if (task.getResult().exists()) {

                            String name = task.getResult().getString("Name");
                            String age = task.getResult().getString("Age");
                            String image = task.getResult().getString("Image");
                            resultUri = Uri.parse(image);

                            nameEditText.setText(name);

                            ageEditText.setText(age);


                            //Setting PlaceHolder Image to show Image while Loading the Image*********************

                            RequestOptions placeHolderRequest = new RequestOptions();
                            placeHolderRequest.placeholder(R.mipmap.ic_perm_identity_black_48dp);

                            Glide.with(profile.this).setDefaultRequestOptions(placeHolderRequest)
                                    .load(image)
                                    .into(setupImage);

                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    } else {

                        String error = task.getException().getMessage();
                        Toast.makeText(profile.this, "Upload Your Image Again" + "/n" + error, Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.INVISIBLE);

                    }
                }

            }
        });


        Button doneButton;
        doneButton = findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                name = nameEditText.getText().toString().trim();
                age = ageEditText.getText().toString().trim();

                if (isChanged && !TextUtils.isEmpty(name) && !TextUtils.isEmpty(age) ) {

                    if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(age) && resultUri != null) {

                        StorageReference imgPath = storageReference.child("Profile Images").child(userID).child(".jpg");
                        imgPath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener <UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task <UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()) {

                                    storeFireStore(task, name, age);

                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(profile.this, "Error: " + error, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }else if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(age)){

                    // Toast.makeText(profile.this, "Enter All Fields", Toast.LENGTH_LONG).show();
                            //progressBar.setVisibility(View.GONE);
                            storeFireStore(null,name,age);

                }else {

                    Toast.makeText(profile.this, "Enter All Fields", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void storeFireStore(@NonNull Task <UploadTask.TaskSnapshot> task, String name, String age) {

        Uri downloadUri;

        if(task!=null) {

            downloadUri = task.getResult().getDownloadUrl();

        }else {

            downloadUri=resultUri;
        }

        Map <String, String> userMap2 = new HashMap <>();
        userMap2.put("Image", downloadUri.toString());
        userMap2.put("Name",name);
        userMap2.put("Age",age);

        firebaseFirestore.collection("Users").document(userID).set(userMap2).addOnCompleteListener(new OnCompleteListener <Void>() {
            @Override
            public void onComplete(@NonNull Task <Void> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(profile.this, "Done", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(profile.this, LoginActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(profile.this, "Error: " + error, Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                isChanged=true;
                setupImage.setImageURI(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(profile.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        isClicked=true;
        super.onBackPressed();
        finish();
    }
}

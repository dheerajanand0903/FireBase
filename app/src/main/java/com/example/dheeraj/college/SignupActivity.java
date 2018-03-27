package com.example.dheeraj.college;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
private Button signUpRegister;
private EditText editTextemail;
private EditText editTextpassword;
private EditText editTextUserName;

private ProgressDialog progressDialog;
private FirebaseAuth firebaseAuth;
private FirebaseFirestore firebaseFirestore;

String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

//Authenticating Object Of Firebase.. It Will initialize our firebase auth object....***********
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();


        progressDialog=new ProgressDialog(this);
        signUpRegister=(Button)findViewById(R.id.buttonRegister);

        editTextemail=(EditText)findViewById(R.id.email);
        editTextpassword=(EditText)findViewById(R.id.pswrd);
        editTextUserName=(EditText)findViewById(R.id.userName_editText);
        signUpRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }
    public void registerUser(){
        String email=editTextemail.getText().toString().trim();
        String password=editTextpassword.getText().toString().trim();
        String UserName=editTextUserName.getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please Enter your Email",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please Enter your Password",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(UserName)){
            Toast.makeText(this,"Please Enter your Name",Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Registering.. Please Wait");
        progressDialog.show();


        //It creates a user on the firebase console with the given email and password..**********
        // The .addOnCompleteLister method is used to get listened when the registration is complete(THIS is Optional task as we are doing here)
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this,  new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            //User is successfully registered
                            Toast.makeText(SignupActivity.this,"Registered",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else {
                            String errorMesssage=task.getException().getMessage();
                            Toast.makeText(SignupActivity.this,"Error: "+errorMesssage,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

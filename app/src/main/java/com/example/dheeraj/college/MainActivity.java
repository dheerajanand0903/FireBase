package com.example.dheeraj.college;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Explode;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

private LinearLayout linearLayout,linearLayout1;
private Button subButton,loginButton;
private EditText editTextemail,editTextpassword;

private FirebaseAuth firebaseAuth;
private ProgressDialog progressDialog;
private Animation downtoup,toptodown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For Full Screen Code *******************************************************
 /*      requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
*/
        setContentView(R.layout.activity_main);


        firebaseAuth=FirebaseAuth.getInstance();
        //For Checking If The user is already loggin in ?? **************
        if (firebaseAuth.getCurrentUser()!=null){
            finish();
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }

        linearLayout=(LinearLayout)findViewById(R.id.linearLayout2);
        linearLayout1=(LinearLayout)findViewById(R.id.linearLayout1);
        toptodown= AnimationUtils.loadAnimation(this,R.anim.toptodown);
        downtoup=AnimationUtils.loadAnimation(this,R.anim.downtoup);
        linearLayout.setAnimation(downtoup);
        linearLayout1.setAnimation(toptodown);

        progressDialog=new ProgressDialog(this);


        setLoginButton();
        setSubButton();

    }


    private void setLoginButton(){
        loginButton=(Button)findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }



    private void setSubButton(){
        subButton=(Button)findViewById(R.id.signUpButton);
        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setEnterTransition(new Explode());
                }
                Intent intent=new Intent(MainActivity.this,SignupActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                }
                else {
                    startActivity(intent);
                }
            }
        });
    }



    public void login(){
        editTextemail=(EditText)findViewById(R.id.userName_editText);
        editTextpassword=(EditText)findViewById(R.id.password_editText);
       String email=editTextemail.getText().toString().trim();
       String password=editTextpassword.getText().toString().trim();

       if(TextUtils.isEmpty(email)){
           Toast.makeText(this,"Please Enter your Email",Toast.LENGTH_SHORT).show();
           return;
       }
       if(TextUtils.isEmpty(password)){
           Toast.makeText(this,"Please Enter your Password",Toast.LENGTH_SHORT).show();
           return;
       }
        progressDialog.setMessage("Logging You In..");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            finish();
                            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                        }
                        else{
                            String errorMessage=task.getException().getMessage();
                            Toast.makeText(getApplicationContext(),"Error: "+errorMessage,Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
}

package com.addevelopment.whatsappclonefull;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.addevelopment.whatsappclonefull.Models.Users;
import com.addevelopment.whatsappclonefull.databinding.ActivitySigninBinding;
import com.addevelopment.whatsappclonefull.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    ActivitySignupBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase mDatabase;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("Wait Your Account Creating...");

        binding.signupGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignupActivity.this, "Please click on already have an Account then press google signing", Toast.LENGTH_SHORT).show();
            }
        });


        binding.signupAlreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignupActivity.this,SigninActivity.class);
                startActivity(i);
                finish();
            }
        });

        //signupButton OnClick Listnere
        binding.signupSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSignup();
            }
        });
    }

    // Function call when user click on sigup
    // from sigupup button onClikListener
    private void goSignup() {
        progressDialog.show();

        String email = binding.signupEmail.getText().toString();
        String pass  = binding.signupPassword.getText().toString();
        String username = binding.signupUserName.getText().toString();

        if(username.isEmpty()){
            binding.signupUserName.getText().toString();
        }
        else if(email.isEmpty()){
            binding.signupEmail.setError("Field Reqired!!!!");
        }
        else if(pass.isEmpty()){
            binding.signupPassword.getText().toString();
        }
        else if(!(username.isEmpty()&&email.isEmpty()&&pass.isEmpty())){
            auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();
                    if(task.isSuccessful()){
                        Users users = new Users(username,email,pass);
                        String id = task.getResult().getUser().getUid();
                        mDatabase.getReference().child("Users").child(id).setValue(users);

                        Intent i = new Intent(SignupActivity.this,SigninActivity.class);
                        startActivity(i);
                        Toast.makeText(SignupActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                    }
                    else{

                        Toast.makeText(SignupActivity.this, "Authentication Failed! "
                                +task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    }

                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();

        if(user != null){
            Intent i = new Intent(SignupActivity.this,MainActivity.class);
            startActivity(i);
            finish();
        }

    }
}
package com.addevelopment.whatsappclonefull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.addevelopment.whatsappclonefull.Models.Users;
import com.addevelopment.whatsappclonefull.databinding.ActivitySigninBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class SigninActivity extends AppCompatActivity {

    ActivitySigninBinding binding;
    ProgressDialog progressDialog;

    GoogleSignInClient mgoogleSignInClient;
    FirebaseAuth auth;
    FirebaseDatabase mDatabase;

    private static final String TAG = "SigninActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mgoogleSignInClient = GoogleSignIn.getClient(this , gso);
        mDatabase = FirebaseDatabase.getInstance();

        auth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Wait You're Signing...");


        binding.signinNotHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SigninActivity.this,SignupActivity.class);
                startActivity(i);
                finish();
            }
        });

        //signupButton OnClick Listnere
        binding.signinSigninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignin();
            }
        });

        binding.signinGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });


    }
    // Function call when user click on sigin
    // from sigupin button onClikListener
    private void goToSignin() {
        progressDialog.show();

        String email = binding.signinEmail.getText().toString();
        String pass  = binding.signinPassword.getText().toString();

        if(email.isEmpty()){
            binding.signinEmail.setError("Field Reqired!!!!");
        }
        else if(pass.isEmpty()){
            binding.signinPassword.getText().toString();
        }
        else if(!(email.isEmpty()&&pass.isEmpty())){
            auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();
                    if(task.isSuccessful()){
                        Intent i = new Intent(SigninActivity.this, MainActivity.class);
                        startActivity(i);
                    }
                    else{
                        Toast.makeText(SigninActivity.this, "Signin Failed"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(SigninActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(auth.getCurrentUser() != null ){
            Intent i = new Intent(SigninActivity.this,MainActivity.class);
            startActivity(i);
            finish();
        }

    }

    int RC_SIGN_IN = 44;
    private void googleSignIn(){
        Intent signInIntent = mgoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try{
                Log.e(TAG, "onActivityResult: "+RC_SIGN_IN );
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "onActivityResult: "+ account.getId());
                firebaseAuthWithGoogle(account.getIdToken());


            } catch (ApiException e) {
                Log.w(TAG, "onActivityResult: ", e );
            }
        }

    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = auth.getCurrentUser();
                            Users userModel = new Users();
                            userModel.setUserName(user.getDisplayName());
                            userModel.setProfilePic(user.getPhotoUrl().toString());
                            userModel.setStatus("Hey,I'm using chirping");
                            mDatabase.getReference().child("Users").child(user.getUid()).setValue(userModel);


                            Intent i = new Intent(SigninActivity.this,MainActivity.class);
                            startActivity(i);
                            Toast.makeText(SigninActivity.this, "Signin With Google Successfully", Toast.LENGTH_SHORT).show();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }

                        // ...
                    }
                });
    }
}
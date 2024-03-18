package com.addevelopment.whatsappclonefull;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.Toast;

import com.addevelopment.whatsappclonefull.Adapter.FragmentAdapter;
import com.addevelopment.whatsappclonefull.Models.Users;
import com.addevelopment.whatsappclonefull.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;

    GoogleSignInClient mgoogleSignInClient;
    ActivityMainBinding binding;
    Users u;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        u = new Users();

        firebaseMethod();


        binding.mainActivityViewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager()));
        binding.mainActivityTabLayout.setupWithViewPager(binding.mainActivityViewPager);




    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){

            case R.id.main_menu_signout:
                mgoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        auth.signOut();
                        Intent i = new Intent(MainActivity.this,SigninActivity.class);
                        startActivity(i);
                        finish();
                        Toast.makeText(MainActivity.this, "Signout SuccessFully", Toast.LENGTH_SHORT).show();
                    }
                });
                break;

            case R.id.main_menu_setting:
                Intent j = new Intent(MainActivity.this,SettingActivity.class);
                startActivity(j);
                finish();
                break;

            case R.id.main_menu_create_group:
                Intent i = new Intent(MainActivity.this,GroupChating.class);
                startActivity(i);
                finish();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void firebaseMethod() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mgoogleSignInClient = GoogleSignIn.getClient(this , gso);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (user == null){
            Intent i = new Intent(MainActivity.this,SigninActivity.class);
            startActivity(i);
            finish();
        }
        firebaseDatabase.getReference().child("Users").child(auth.getCurrentUser().getUid())
                .child("onlineStatus").setValue("true");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        firebaseDatabase.getReference().child("Users").child(auth.getCurrentUser().getUid())
                .child("onlineStatus").setValue("false");
    }
}
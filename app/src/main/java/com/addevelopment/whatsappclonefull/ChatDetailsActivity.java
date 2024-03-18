package com.addevelopment.whatsappclonefull;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.addevelopment.whatsappclonefull.Adapter.ChatAdapter;
import com.addevelopment.whatsappclonefull.Models.MessagesModel;
import com.addevelopment.whatsappclonefull.databinding.ActivityChatDetailsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class ChatDetailsActivity extends AppCompatActivity {

    ActivityChatDetailsBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase mDatabase;
    String senderId,recieverId ;
    String onlineStatus;
    EmojIconActions emojIconActions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        //Intent Function
        getReceverDataFromIntent();
        emojIconActions = new EmojIconActions(this,binding.getRoot(),binding.activityDetailsMessageBox,binding.activityDetailsEmojis);
        emojIconActions.ShowEmojIcon();

        binding.activityDetailsBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatDetailsActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        final ArrayList<MessagesModel> messagesModelsList = new ArrayList<>();
        final ChatAdapter chatAdapter = new ChatAdapter(messagesModelsList,this);

        final String senderRoom = senderId + recieverId;
        final String receiverRoom =  recieverId + senderId;

        binding.activityDetailsRecyclerview.setAdapter(chatAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        binding.activityDetailsRecyclerview.setLayoutManager(manager);

        // for get messagges
        mDatabase.getReference().child("Chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messagesModelsList.clear();
                        for(DataSnapshot snapshot1 : snapshot.getChildren()){
                            MessagesModel model = snapshot1.getValue(MessagesModel.class);
                            messagesModelsList.add(model);
                        }
                        chatAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

        binding.activityDetailsSendFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = binding.activityDetailsMessageBox.getText().toString();
                if(!message.equals("")) {
                    final MessagesModel model = new MessagesModel(senderId, message);
                    model.setTimestamp(new Date().getTime());
                    binding.activityDetailsMessageBox.setText("");
                    mDatabase.getReference().child("Chats")
                            .child(senderRoom)
                            .push()
                            .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mDatabase.getReference()
                                    .child("Chats")
                                    .child(receiverRoom)
                                    .push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                }
                            });
                        }
                    });
                }
            }
        });// send fab click listener end

        binding.activityDetailsAttachFile.setOnClickListener(v -> {

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ChatDetailsActivity.this,
                    R.style.BottomSheetDialogTheme);

            View bottomSheetView = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.attach_bottom_sheet,findViewById(R.id.attach_container));

            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();
        });

    }//onCreate End

    private void getReceverDataFromIntent() {
        senderId = auth.getUid();
        recieverId = getIntent().getStringExtra("userId");
        String revieverName = getIntent().getStringExtra("userName");
        String revieverPic = getIntent().getStringExtra("profilePic");
        onlineStatus = getIntent().getStringExtra("OnlineStatus");

        if(onlineStatus.equals("true")){
            binding.greenDot.setVisibility(View.VISIBLE);
        }

        binding.activityDetailsUserName.setText(revieverName);
        Picasso.get()
                .load(revieverPic)
                .placeholder(R.drawable.ic_user)
                .into(binding.activityDetailsProfilePic) ;
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ChatDetailsActivity.this,MainActivity.class);
        startActivity(i);
        finish();
    }
}
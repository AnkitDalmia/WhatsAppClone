package com.addevelopment.whatsappclonefull;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.addevelopment.whatsappclonefull.Adapter.ChatAdapter;
import com.addevelopment.whatsappclonefull.Models.MessagesModel;
import com.addevelopment.whatsappclonefull.databinding.ActivityGroupChatingBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class GroupChating extends AppCompatActivity {

    ActivityGroupChatingBinding binding;
    FirebaseDatabase mDatabase;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;

    ArrayList<MessagesModel> messagesModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        ChatAdapter adapter = new ChatAdapter(messagesModelList, this);

        binding.groupChatRecyclerview.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        binding.groupChatRecyclerview.setLayoutManager(manager);

        final String senderId = auth.getUid();
        final String senderName = firebaseUser.getDisplayName();

        binding.groupChatUserName.setText("World Chat");

        mDatabase.getReference().child("Group Chat")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messagesModelList.clear();
                        for(DataSnapshot snapshot1:snapshot.getChildren()){
                            MessagesModel model = snapshot1.getValue(MessagesModel.class);
                            messagesModelList.add(model);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.groupChatSendFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = binding.groupChatMessageBox.getText().toString();
                if (!message.equals("")) {
                    final MessagesModel model = new MessagesModel(senderId, message);
                    model.setTimestamp(new Date().getTime());
                    binding.groupChatMessageBox.setText("");

                    mDatabase.getReference().child("Group Chat")
                            .push().setValue(model)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            });
                }
            }
        });

        binding.groupChatBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GroupChating.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
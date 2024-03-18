package com.addevelopment.whatsappclonefull.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.addevelopment.whatsappclonefull.ChatDetailsActivity;
import com.addevelopment.whatsappclonefull.DateChecker;
import com.addevelopment.whatsappclonefull.Models.Users;
import com.addevelopment.whatsappclonefull.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.viewHolder> {

    ArrayList<Users>   list;
    Context context;
    String formtedDate,formtedDate1;

    private static final String TAG = "UserAdapter.this";//1616176400719

    public UserAdapter(ArrayList<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_show_user,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        Users users = list.get(position);
            Picasso.get().load(users.getProfilePic()).placeholder(R.drawable.ic_user).into(holder.sampleProfilePic);
            holder.sampleUserName.setText(users.getUserName());

            FirebaseDatabase.getInstance().getReference().child("Chats")
                    .child(FirebaseAuth.getInstance().getUid() + users.getUserId())
                    .orderByChild("timestamp")
                    .limitToLast(1)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChildren()) {
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {


                                    String date = snapshot1.child("timestamp").getValue().toString();
                                    if(date != null) {

                                        DateChecker dc = new DateChecker();
                                        Long l = Long.parseLong(date);
                                        SimpleDateFormat formatee = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
                                        formtedDate = formatee.format(l);
                                        try {
                                            formtedDate1 = dc.dateChecker(formtedDate);
                                        } catch (ParseException e) {
                                            Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
                                        }


                                        holder.sampleLastMessageTime.setText(formtedDate1);
                                    }

                                    holder.sampleLastMessage.setText(snapshot1.child("message").getValue().toString());

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, ChatDetailsActivity.class);
                    i.putExtra("userId", users.getUserId());
                    i.putExtra("profilePic", users.getProfilePic());
                    i.putExtra("userName", users.getUserName());
                    i.putExtra("OnlineStatus",users.isOnlineStatus());
                    context.startActivity(i);
                }
            });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView sampleProfilePic;
        TextView sampleLastMessage,sampleUserName,sampleLastMessageTime;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            sampleProfilePic = itemView.findViewById(R.id.activity_details_profile_pic);
            sampleUserName = itemView.findViewById(R.id.activity_details_user_name);
            sampleLastMessage = itemView.findViewById(R.id.sample_last_message);
            sampleLastMessageTime = itemView.findViewById(R.id.sample_last_message_time);



        }
    }
}

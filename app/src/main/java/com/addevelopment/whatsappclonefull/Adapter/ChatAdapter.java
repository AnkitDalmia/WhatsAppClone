package com.addevelopment.whatsappclonefull.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.addevelopment.whatsappclonefull.DateChecker;
import com.addevelopment.whatsappclonefull.Models.MessagesModel;
import com.addevelopment.whatsappclonefull.R;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter {

    ArrayList<MessagesModel> messagesModelList;
    Context context;

    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;

    public ChatAdapter(ArrayList<MessagesModel> messagesModels, Context context) {
        this.messagesModelList = messagesModels;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == SENDER_VIEW_TYPE){
            View view = LayoutInflater.from(context).inflate(R.layout.smaple_sender_layout,parent,false);
            return new SenderViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.smaple_recever_layout,parent,false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(messagesModelList.get(position).getuId().equals(FirebaseAuth.getInstance().getUid())){
            return SENDER_VIEW_TYPE;

        }
        else{
            return RECEIVER_VIEW_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        String formtedDate = null;
        String formtedDate1 = null;
        MessagesModel messagesModel = messagesModelList.get(position);
        Long date = messagesModel.getTimestamp();
        if(date > 0) {

            DateChecker dc = new DateChecker();

            SimpleDateFormat formatee = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            formtedDate = formatee.format(date);
            try {
                formtedDate1 = dc.dateChecker(formtedDate);
            } catch (ParseException e) {
                Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
            }

            if(holder.getClass() == SenderViewHolder.class) {
                ((SenderViewHolder) holder).senderTime.setText(formtedDate1);
            }
            else{
                ((ReceiverViewHolder) holder).receiverTime.setText(formtedDate1);
            }
        }


        if(holder.getClass() == SenderViewHolder.class){
            ((SenderViewHolder) holder).senderMsg.setText(messagesModel.getMessage());
        }
        else{
            ((ReceiverViewHolder) holder).receivingMsg.setText(messagesModel.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messagesModelList.size();
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder{

        TextView receivingMsg,receiverTime;


        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);

            receivingMsg = itemView.findViewById(R.id.sample_receiver_message);
            receiverTime = itemView.findViewById(R.id.receiver_time);

        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder{

        TextView senderMsg,senderTime;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMsg = itemView.findViewById(R.id.sample_sender_message);
            senderTime = itemView.findViewById(R.id.sender_time);
        }
    }

}

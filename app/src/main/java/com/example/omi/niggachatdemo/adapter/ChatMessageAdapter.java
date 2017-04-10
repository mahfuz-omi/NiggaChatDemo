package com.example.omi.niggachatdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.omi.niggachatdemo.application.NiggaChatApplication;
import com.example.omi.niggachatdemo.R;
import com.example.omi.niggachatdemo.model.ChatMessage;

import java.util.ArrayList;

/**
 * Created by omi on 11/17/2016.
 */

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.MyViewHolder> {

    //0 means other-message, 1 means my message,2 means my message with image(sent)
    public ArrayList<ChatMessage> chatMessages;
    Context context;

    public ChatMessageAdapter(ArrayList<ChatMessage> chatMessages, Context context) {
        this.chatMessages = chatMessages;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if(!this.chatMessages.get(position).getFrom().equals(((NiggaChatApplication)context.getApplicationContext()).getFull_name()))
        {
            return 0;
        }

        else
        {
            if(this.chatMessages.get(position).isShowSentImage())
                return 2;
            else
                return 1;
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType)
        {
            case 0:
            {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_row_other, parent, false);
                return new MyViewHolder(itemView);

            }

            case 1:
            {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_row_my, parent, false);
                TextView from = (TextView) itemView.findViewById(R.id.from);
                from.setVisibility(View.INVISIBLE);
                return new MyViewHolder(itemView);

            }
            case 2:
            {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_row_my, parent, false);
                TextView from = (TextView) itemView.findViewById(R.id.from);
                from.setVisibility(View.INVISIBLE);
                ImageView sent = (ImageView)itemView.findViewById(R.id.sent);
                sent.setVisibility(View.VISIBLE);
                return new MyViewHolder(itemView);

            }
        }

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_row_other, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ChatMessage chatMessage = this.chatMessages.get(position);
        holder.from.setText(chatMessage.getFrom());
        holder.message.setText(chatMessage.getMessage());
        holder.time.setText(chatMessage.getTime());
    }

    @Override
    public int getItemCount() {
        return this.chatMessages.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView from, message, time;

        public MyViewHolder(View view)
        {
            super(view);
            from = (TextView) view.findViewById(R.id.from);
            message = (TextView) view.findViewById(R.id.message);
            time = (TextView) view.findViewById(R.id.time);
        }
    }

}

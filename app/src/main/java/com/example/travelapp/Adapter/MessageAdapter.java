package com.example.travelapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelapp.Domain.Message;
import com.example.travelapp.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);

        if (message.isUser()) {
            holder.layoutUserMessage.setVisibility(View.VISIBLE);
            holder.layoutBotMessage.setVisibility(View.GONE);
            holder.textUserMessage.setText(message.getContent());
        } else {
            holder.layoutUserMessage.setVisibility(View.GONE);
            holder.layoutBotMessage.setVisibility(View.VISIBLE);
            holder.textBotMessage.setText(message.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutUserMessage, layoutBotMessage;
        TextView textUserMessage, textBotMessage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutUserMessage = itemView.findViewById(R.id.layoutUserMessage);
            layoutBotMessage = itemView.findViewById(R.id.layoutBotMessage);
            textUserMessage = itemView.findViewById(R.id.textUserMessage);
            textBotMessage = itemView.findViewById(R.id.textBotMessage);
        }
    }
}


package com.atcampus.morefriendsmorefun.Adapter;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atcampus.morefriendsmorefun.Model.ChatModel;
import com.atcampus.morefriendsmorefun.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private static final int LEFT_TYPE_CHAT = 0;
    private static final int RIGHT_TYPE_CHAT = 1;
    private List<ChatModel> chatModels;
    String image;
    FirebaseUser mFirebaseUser;

    public ChatAdapter(List<ChatModel> chatModels, String image) {
        this.chatModels = chatModels;
        this.image = image;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == RIGHT_TYPE_CHAT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_sender,parent,false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_reciever,parent,false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {

        String chatMsg = chatModels.get(position).getChatMessage();
        String chatTime = chatModels.get(position).getTime();

        //convert and format time
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(chatTime));
        String dateFormat = DateFormat.format("dd/MM/yy hh:mm aa",calendar).toString();

        holder.chatText.setText(chatMsg);
        holder.timeText.setText(chatTime);
        try{
            Picasso.get().load(image).into(holder.imageView);
        }catch (Exception e){
            Picasso.get().load(image).into(holder.imageView);
        }

        //seen or delivery
        if (position == chatModels.size()-1){
            if (chatModels.get(position).isSeen()){
                holder.isSeenText.setText("Seen");
            }else{
                holder.isSeenText.setText("Delivered");
            }
        }else{
            holder.isSeenText.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemViewType(int position) {
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatModels.get(position).getSender().equals(mFirebaseUser.getUid())){
            return RIGHT_TYPE_CHAT;
        }else {
            return LEFT_TYPE_CHAT;
        }
    }

    @Override
    public int getItemCount() {
        return chatModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView chatText, timeText, isSeenText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.reciever_img);
            chatText = itemView.findViewById(R.id.reciever_text);
            timeText = itemView.findViewById(R.id.reciever_time);
            isSeenText = itemView.findViewById(R.id.reciever_seen);

        }
    }
}

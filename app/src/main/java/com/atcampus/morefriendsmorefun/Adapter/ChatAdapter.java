package com.atcampus.morefriendsmorefun.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.atcampus.morefriendsmorefun.Activity.ChatActivity;
import com.atcampus.morefriendsmorefun.Model.ChatModel;
import com.atcampus.morefriendsmorefun.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private static final int LEFT_TYPE_CHAT = 0;
    private static final int RIGHT_TYPE_CHAT = 1;
    private List<ChatModel> chatModels;
    String image;
    FirebaseUser mFirebaseUser;
    private Context mCtx;

    public ChatAdapter(List<ChatModel> chatModels, String image,Context mCtx) {
        this.chatModels = chatModels;
        this.image = image;
        this.mCtx = mCtx;
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
    public void onBindViewHolder(@NonNull final ChatAdapter.ViewHolder holder, final int position) {

        String chatMsg = chatModels.get(position).getMessage();
        String chatTime = chatModels.get(position).getTimeStamp();

        //convert and format time
        String formattedDate = null;
        try {
            Long timeInMilis = Long.parseLong(chatTime);
            Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
            calendar.setTimeInMillis(timeInMilis);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
            formattedDate = simpleDateFormat.format(calendar.getTime());
        }catch (Exception e){

        }


        holder.chatText.setText(chatMsg);
        holder.timeText.setText(formattedDate);
        try{
            Picasso.get().load(image).into(holder.imageView);
        }catch (Exception e){
//            Picasso.get().load(image).into(holder.imageView);
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

        holder.chatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(v.getRootView().getContext());
                dialog.setTitle("Delete Chat");
                dialog.setMessage("Are you sure to delete this message?");
                dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(mCtx,"Message deleted",Toast.LENGTH_SHORT).show();
                        deleteMessage(position);
                    }
                });

                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.create().show();
            }
        });

    }

    private void deleteMessage(int i) {
        final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String chatTimeStamp = chatModels.get(i).getTimeStamp();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("chats");
        Query query = databaseReference.orderByChild("timeStamp").equalTo(chatTimeStamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    if (ds.child("sender").getValue().equals(myUid)){
                        ds.getRef().removeValue();
//                        HashMap<String,Object> hashMap = new HashMap<>();
//                        hashMap.put("message","This message was delete...");
//                        ds.getRef().updateChildren(hashMap);
                        Toast.makeText(mCtx,"Message deleted",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(mCtx,"You can only delete your message.",Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
        private ConstraintLayout chatLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.reciever_img);
            chatText = itemView.findViewById(R.id.reciever_text);
            timeText = itemView.findViewById(R.id.reciever_time);
            isSeenText = itemView.findViewById(R.id.reciever_seen);
            chatLayout = itemView.findViewById(R.id.chatLayout);

        }
    }
}

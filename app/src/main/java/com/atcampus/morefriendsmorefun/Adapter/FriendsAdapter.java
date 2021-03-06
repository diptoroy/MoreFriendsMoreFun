package com.atcampus.morefriendsmorefun.Adapter;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atcampus.morefriendsmorefun.Activity.ChatActivity;
import com.atcampus.morefriendsmorefun.Model.FriendsModel;
import com.atcampus.morefriendsmorefun.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private List<FriendsModel> friendsModels;

    public FriendsAdapter(List<FriendsModel> friendsModels) {
        this.friendsModels = friendsModels;
    }

    @NonNull
    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FriendsAdapter.ViewHolder holder, int position) {

        String pEmail = friendsModels.get(position).getEmail();
        String pName = friendsModels.get(position).getName();
        String pImage = friendsModels.get(position).getImage();
        final String pUid = friendsModels.get(position).getUid();

        holder.email.setText(pEmail);
        holder.name.setText(pName);
        try {
            Picasso.get().load(pImage).placeholder(R.drawable.profile).into(holder.image);
        }catch (Exception e){

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    Toast.makeText(itemView.getContext(),""+email,Toast.LENGTH_SHORT).show();
                Intent chatIntent = new Intent(holder.itemView.getContext(), ChatActivity.class);
                chatIntent.putExtra("pUid",pUid);
                holder.itemView.getContext().startActivity(chatIntent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return friendsModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView email,name;
        ImageView image;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            email = itemView.findViewById(R.id.profile_emaile);
            name = itemView.findViewById(R.id.profile_namee);
            image = itemView.findViewById(R.id.profile_imagee);


        }

    }
}

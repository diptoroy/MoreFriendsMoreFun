package com.atcampus.morefriendsmorefun.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.atcampus.morefriendsmorefun.Activity.MainActivity;
import com.atcampus.morefriendsmorefun.Activity.RegisterActivity;
import com.atcampus.morefriendsmorefun.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    TextView userName,userEmail,userPhone,userBio;
    CircleImageView userImage;
    ImageView coverImage;
    FloatingActionButton editBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        userImage = view.findViewById(R.id.profile_image);
        userName = view.findViewById(R.id.profile_name);
        userEmail = view.findViewById(R.id.profile_email);
        userPhone = view.findViewById(R.id.profile_phone);
        userBio = view.findViewById(R.id.Bio_text);
        coverImage = view.findViewById(R.id.cover_image);
        editBtn = view.findViewById(R.id.edit_btn);

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    String image = ""+ ds.child("image").getValue();
                    String name = ""+ ds.child("name").getValue();
                    String email = ""+ ds.child("email").getValue();
                    String phone = ""+ ds.child("phone").getValue();
                    String bio = ""+ ds.child("bio").getValue();
                    String cover = ""+ ds.child("cover").getValue();

                    userName.setText(name);
                    userEmail.setText(email);
                    userPhone.setText(phone);
                    userBio.setText(bio);
                    try {
                        Picasso.get().load(image).into(userImage);
                        Picasso.get().load(cover).into(coverImage);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.profile).into(userImage);
                        Picasso.get().load(R.drawable.profile).into(coverImage);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditOptionsDialog();
            }
        });

        return view;
    }

    private void showEditOptionsDialog() {

        String editOptions[] = {"Edit cover photo","Edit profile photo","Edit Bio","Edit Name","Edit Phone"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose edit option");
        builder.setItems(editOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(which == 1){
                    //cover
                }else if (which == 2){
                    //photo
                }else if (which == 3){
                    //bio
                }else if (which == 4){
                    //name
                }else if (which == 5){
                    //phone
                }
            }
        });

        builder.create().show();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }
}
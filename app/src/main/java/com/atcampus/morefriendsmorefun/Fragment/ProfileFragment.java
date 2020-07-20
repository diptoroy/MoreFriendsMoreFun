package com.atcampus.morefriendsmorefun.Fragment;

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

    TextView userName,userEmail,userPhone;
    CircleImageView userImage;

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
        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    String image = ""+ ds.child("image").getValue();
                    String name = ""+ ds.child("name").getValue();
                    String email = ""+ ds.child("email").getValue();
                    String phone = ""+ ds.child("phone").getValue();

                    userName.setText(name);
                    userEmail.setText(email);
                    userPhone.setText(phone);
                    try {
                        Picasso.get().load(image).into(userImage);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.profile).into(userImage);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

//    private void checkUserStatus(){
//        FirebaseUser user = mAuth.getCurrentUser();
//        if (user != null){
//            //code
//            userName.setText(user.getEmail());
//        }else {
//
//        }
//    }
//
//    @Override
//    public void onStart() {
//        checkUserStatus();
//        super.onStart();
//    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }
}
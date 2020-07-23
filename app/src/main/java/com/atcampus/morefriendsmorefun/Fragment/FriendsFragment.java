package com.atcampus.morefriendsmorefun.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.atcampus.morefriendsmorefun.Adapter.FriendsAdapter;
import com.atcampus.morefriendsmorefun.Model.FriendsModel;
import com.atcampus.morefriendsmorefun.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {



    public FriendsFragment() {
        // Required empty public constructor
    }

    private RecyclerView friendsRecyclerView;
    private FriendsAdapter friendsAdapter;
    private List<FriendsModel> friendsModelList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        friendsRecyclerView = view.findViewById(R.id.friends_recyclerView);
        friendsRecyclerView.setHasFixedSize(true);
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        friendsModelList = new ArrayList<>();

        getAllUser();
        return view;
    }

    private void getAllUser() {

        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendsModelList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    FriendsModel fModel = ds.getValue(FriendsModel.class);
                    if (!fModel.getProfileUid().equals(mUser.getUid())){
                        friendsModelList.add(fModel);
                    }

                    friendsAdapter = new FriendsAdapter(friendsModelList);
                    friendsRecyclerView.setAdapter(friendsAdapter);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
package com.atcampus.morefriendsmorefun.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.atcampus.morefriendsmorefun.Activity.MainActivity;
import com.atcampus.morefriendsmorefun.Activity.RegisterActivity;
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
    private FirebaseAuth mFirebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        friendsRecyclerView = view.findViewById(R.id.friends_recyclerView);
        friendsRecyclerView.setHasFixedSize(true);
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        friendsModelList = new ArrayList<>();

        mFirebaseAuth = FirebaseAuth.getInstance();

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
                for (DataSnapshot ds : snapshot.getChildren()) {
                    FriendsModel fModel = ds.getValue(FriendsModel.class);
                    if (fModel.getUid() != null && !fModel.getUid().equals(mUser.getUid())) {
                        friendsModelList.add(fModel);
                    }

                    friendsAdapter = new FriendsAdapter(friendsModelList);
                    friendsRecyclerView.setAdapter(friendsAdapter);
                    friendsAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void SearchUsers(final String query) {

        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendsModelList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    FriendsModel fModel = ds.getValue(FriendsModel.class);
                    if (fModel.getUid() != null && !fModel.getUid().equals(mUser.getUid())) {
                        if (fModel.getName().toLowerCase().contains(query.toLowerCase()) ||
                                fModel.getEmail().toLowerCase().contains(query.toLowerCase())) {
                            friendsModelList.add(fModel);
                        }

                    }

                    friendsAdapter = new FriendsAdapter(friendsModelList);
                    friendsAdapter.notifyDataSetChanged();
                    friendsRecyclerView.setAdapter(friendsAdapter);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkUserStatus() {
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            //code
        } else {
            startActivity(new Intent(getActivity(), RegisterActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.searchView);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (!TextUtils.isEmpty(s.trim())) {
                    SearchUsers(s);
                } else {
                    getAllUser();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!TextUtils.isEmpty(s.trim())) {
                    SearchUsers(s);
                } else {
                    getAllUser();
                }
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.signOut) {
            mFirebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
}
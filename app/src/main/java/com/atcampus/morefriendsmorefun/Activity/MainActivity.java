package com.atcampus.morefriendsmorefun.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.atcampus.morefriendsmorefun.Fragment.ChatFragment;
import com.atcampus.morefriendsmorefun.Fragment.FriendsFragment;
import com.atcampus.morefriendsmorefun.Fragment.HomeFragment;
import com.atcampus.morefriendsmorefun.Fragment.NotificationFragment;
import com.atcampus.morefriendsmorefun.Fragment.ProfileFragment;
import com.atcampus.morefriendsmorefun.Model.TokenModel;
import com.atcampus.morefriendsmorefun.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    private FirebaseAuth mFirebaseAuth;

    String mUId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();

        bottomNavigationView = findViewById(R.id.bottomNavigationView2);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        updateToken(FirebaseInstanceId.getInstance().getToken());
    }

    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }

    public void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Token");
        TokenModel mToken = new TokenModel(token);
        reference.child(mUId).setValue(mToken);

    }

    private void checkUserStatus(){
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null){
            //code
            mUId = user.getUid();

            SharedPreferences sharedPreferences = getSharedPreferences("SP_USER",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Current_UserID",mUId);
            editor.apply();
        }else {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            finish();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main,menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//
//        int id = item.getItemId();
//        if (id == R.id.signOut){
//            mFirebaseAuth.signOut();
//            checkUserStatus();
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_friends:
                            selectedFragment = new FriendsFragment();
                            break;
                        case R.id.nav_notification:
                            selectedFragment = new NotificationFragment();
                            break;
                        case R.id.nav_profile:
                            selectedFragment = new ProfileFragment();
                            break;
                        case R.id.nav_chat:
                            selectedFragment = new ChatFragment();
                            break;
                    }
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame, selectedFragment).commit();
                        return true;
                    }
                };

}
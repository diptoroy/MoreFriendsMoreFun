package com.atcampus.morefriendsmorefun.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.atcampus.morefriendsmorefun.Adapter.ChatAdapter;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CircleImageView pImage;
    private TextView pName, pStatus;
    private EditText pChatText;
    private ImageButton pSendBtn;
    private RecyclerView chatRecyclerView;
    private List<ChatModel> chatModelList;
    private ChatAdapter chatAdapter;

    FirebaseAuth mFirebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ValueEventListener seenEventListener;
    DatabaseReference userReference;


    String pUid,pImg, myUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = findViewById(R.id.toolbar);
        pImage = findViewById(R.id.profileImage);
        pName = findViewById(R.id.profileName);
        pStatus = findViewById(R.id.profileStatus);
        pChatText = findViewById(R.id.chat_text);
        pSendBtn = findViewById(R.id.chat_send_btn);
        chatRecyclerView = findViewById(R.id.chat_recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        chatRecyclerView.setHasFixedSize(true);
        chatRecyclerView.setLayoutManager(linearLayoutManager);

        Intent intent = getIntent();
        pUid = intent.getStringExtra("pUid");

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        Query userQuery = databaseReference.orderByChild("uid").equalTo(pUid);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    pImg = "" + ds.child("image").getValue();
                    String name = "" + ds.child("name").getValue();

                    pName.setText(name);
                    try {
                        Picasso.get().load(pImg).placeholder(R.drawable.profile).into(pImage);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.profile).into(pImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        pSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chatMsg = pChatText.getText().toString().trim();
                if (TextUtils.isEmpty(chatMsg)) {
                    Toast.makeText(ChatActivity.this, "Message can't be empty", Toast.LENGTH_SHORT).show();
                } else {
                    sendChat(chatMsg);
                }
            }
        });

        readChat();
        seenChat();

    }

    private void seenChat() {
        userReference = FirebaseDatabase.getInstance().getReference("chats");
        seenEventListener = userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    ChatModel chat = ds.getValue(ChatModel.class);
                    if (chat.getReceiver().equals(myUid) && chat.getSender().equals(pUid)){
                        HashMap<String,Object> hs = new HashMap<>();
                        hs.put("isSeen",true);
                        ds.getRef().updateChildren(hs);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void readChat() {
        chatModelList = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("chats");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatModelList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    ChatModel chat = ds.getValue(ChatModel.class);
                    if (chat.getReceiver().equals(myUid) && chat.getSender().equals(pUid) ||
                            chat.getReceiver().equals(pUid) && chat.getSender().equals(myUid)){
                        chatModelList.add(chat);
                    }
                    chatAdapter = new ChatAdapter(chatModelList,pImg);
                    chatAdapter.notifyDataSetChanged();
                    chatRecyclerView.setAdapter(chatAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendChat(String chatMsg) {
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        String timeStamp = String.valueOf(System.currentTimeMillis());

        HashMap<String,Object> chat = new HashMap<>();
        chat.put("sender",myUid);
        chat.put("receiver",pUid);
        chat.put("message",chatMsg);
        chat.put("timeStamp",timeStamp);
        chat.put("isSeen",false);
        mDatabaseReference.child("chats").push().setValue(chat);

        pChatText.setText("");
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        userReference.removeEventListener(seenEventListener);
    }

    private void checkUserStatus() {
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            //code
            myUid = user.getUid();
        } else {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.searchView).setVisible(false);
        return super.onCreateOptionsMenu(menu);
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
package com.atcampus.morefriendsmorefun.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.atcampus.morefriendsmorefun.Model.NotificationDataModel;
import com.atcampus.morefriendsmorefun.Model.SenderModel;
import com.atcampus.morefriendsmorefun.Model.TokenModel;
import com.atcampus.morefriendsmorefun.R;
import com.atcampus.morefriendsmorefun.Service.ApiService;
import com.atcampus.morefriendsmorefun.Service.Client;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    ApiService apiService;
    boolean notify = false;

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

        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(ApiService.class);

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
                    String typingStatus = ""+ ds.child("typingTo").getValue();

                    if (typingStatus.equals(myUid)){
                        pStatus.setText("typing...");
                    }else {
                        String onlineStatus = ""+ ds.child("onlineStatus").getValue();
                        if (onlineStatus.equals("online")){
                            pStatus.setText(onlineStatus);
                        }else{
                            String formattedDate = null;
                            try {
                                Long timeInMilis = Long.parseLong(onlineStatus);
                                Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                                calendar.setTimeInMillis(timeInMilis);
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                                formattedDate = simpleDateFormat.format(calendar.getTime());
                                pStatus.setText("Last seen at "+formattedDate);
                            }catch (Exception e){

                            }
                        }
                    }


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
                notify = true;
                String chatMessage = pChatText.getText().toString().trim();
                if (TextUtils.isEmpty(chatMessage)) {
                    Toast.makeText(ChatActivity.this, "Message can't be empty", Toast.LENGTH_SHORT).show();
                } else {
                    sendChat(chatMessage);
                }
                pChatText.setText("");
            }
        });

        pChatText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0){
                    checkTypingStatus("noOne");
                }else{
                    checkTypingStatus(pUid);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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
                    chatAdapter = new ChatAdapter(chatModelList,pImg,ChatActivity.this);
                    chatAdapter.notifyDataSetChanged();
                    chatRecyclerView.setAdapter(chatAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendChat(final String chatMessage) {
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        String time = String.valueOf(System.currentTimeMillis());

        final HashMap<String,Object> chat = new HashMap<>();
        chat.put("sender",myUid);
        chat.put("receiver",pUid);
        chat.put("message",chatMessage);
        chat.put("timeStamp",time);
        chat.put("isSeen",false);
        mDatabaseReference.child("chats").push().setValue(chat);



        String msg = chatMessage;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                NotificationDataModel user = snapshot.getValue(NotificationDataModel.class);
                if (notify){
                    sentNotification(pUid,user.getUser(),chatMessage);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sentNotification(final String pUid, final String user, final String chatMessage) {
        DatabaseReference allToken = FirebaseDatabase.getInstance().getReference("Token");
        Query query = allToken.orderByKey().equalTo(pUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()){
                    TokenModel tokenModel = data.getValue(TokenModel.class);
                    NotificationDataModel nData = new NotificationDataModel(myUid,user+":"+chatMessage,"New Message",pUid,R.drawable.ic_baseline_email_24);

                    SenderModel senderModel = new SenderModel(nData,tokenModel.getToken());
                    apiService.sendNotification(senderModel)
                            .enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    Toast.makeText(ChatActivity.this,""+response.message(),Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkOnlineStatus(String status){
        DatabaseReference checkRef = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String,Object> checkMap = new HashMap<>();
        checkMap.put("onlineStatus",status);
        checkRef.updateChildren(checkMap);
    }

    private void checkTypingStatus(String typing){
        DatabaseReference checkRef = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String,Object> checkMap = new HashMap<>();
        checkMap.put("typingTo",typing);
        checkRef.updateChildren(checkMap);
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        checkOnlineStatus("online");
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        String time = String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(time);
        checkTypingStatus("noOne");
        userReference.removeEventListener(seenEventListener);
    }

    @Override
    protected void onResume() {
        checkOnlineStatus("online");
        super.onResume();
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
package com.atcampus.morefriendsmorefun.Service;

import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        SharedPreferences sharedPreferences = getSharedPreferences("SP_USER",MODE_PRIVATE);
        String currentUser = sharedPreferences.getString("Current_UserID","None");

        String sent =remoteMessage.getData().get("sent");
        String user =remoteMessage.getData().get("user");
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fUser != null && sent.equals(fUser.getUid())){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                sendVersionNotification(remoteMessage);
            }else {
                sendNormalNotification(remoteMessage);
            }
        }
    }

    private void sendNormalNotification(RemoteMessage remoteMessage) {
    }

    private void sendVersionNotification(RemoteMessage remoteMessage) {
    }


}

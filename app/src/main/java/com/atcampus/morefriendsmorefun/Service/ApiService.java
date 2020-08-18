package com.atcampus.morefriendsmorefun.Service;

import com.atcampus.morefriendsmorefun.Model.SenderModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    @Headers({
            "Content-Type:Application/json",
            "Authorization-key=AAAAvb9EJ10:APA91bGEnnxyqPOY_nYkLHk8V_nE_gHieePWGoKMxaoCHcVD0qrSS0H0SaffH6NkMQCWDHw85yz17GntcicFrLbezHVDtm6swtLhPChZBzzgV8y4LWO_SM6-GQdcwRJvGmPcolJB0WOW"
    })

    @POST("fcm/send")
    Call<ResponseBody>sendNotification(@Body SenderModel body);
}

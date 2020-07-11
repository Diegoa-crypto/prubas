package com.Marche.Notificaciones;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA_Q3HaQU:APA91bFaOZm4JOKtRnxEwvqCwo_NY3NpWMdR9hS1XE3GU_M37BPEKHQg7cs-7WL1Am0BA1gNJckFmT8_89msq-hE_l6KZE7aTRX559Wv7lJ1lk8TZgluJzKlOzF3O0cS9yj5MvocJUOk"
            }
    )

    @POST("fcm/send")
    Call<MyRespuesta> sendNotification(@Body Sender body);
}

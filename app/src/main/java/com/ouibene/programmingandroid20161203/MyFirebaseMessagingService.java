package com.ouibene.programmingandroid20161203;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Youngeun-Lee on 2016. 12. 10..
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    final static String TAG = MyFirebaseMessagingService.class.getName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived" + remoteMessage.getData());

        if (remoteMessage.getData().size() > 0) {
            String messageBody = remoteMessage.getData().get("message_body");
            if (messageBody != null) {
                toast(messageBody);
            }
        }

        if (remoteMessage.getNotification() != null) {
            String messageBody = remoteMessage.getNotification().getBody();//message의 내용. final을 붙여서 변경 가능성이 없음을 알림.
            toast(messageBody);
        }
    }

    void toast (final String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    } //push 구현. notification을 사용하면 background에서는 푸시받을 수 없음
}

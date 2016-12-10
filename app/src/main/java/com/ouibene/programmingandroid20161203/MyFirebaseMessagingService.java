package com.ouibene.programmingandroid20161203;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sdsmdg.tastytoast.TastyToast;

/**
 * Created by Youngeun-Lee on 2016. 12. 10..
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            String messageBody = remoteMessage.getData().get("message_body");
            if (messageBody != null) {
                String messageType = remoteMessage.getData().get("message_type_string");
                toast(messageBody, messageType);
            }
        }

        if (remoteMessage.getNotification() != null) {
            String messageBody = remoteMessage.getNotification().getBody();//message의 내용. final을 붙여서 변경 가능성이 없음을 알림.
            toast(messageBody, "default");
        }
    }

    void toast (final String message, final String messageType) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                int messageTypeInt = TastyToast.DEFAULT;

                if (messageType == null) {
                    messageTypeInt = TastyToast.DEFAULT;
                }
                else if(messageType.equals("confusing")){
                    messageTypeInt = TastyToast.CONFUSING;
                }
                else if (messageType.equals("error")){
                    messageTypeInt = TastyToast.ERROR;
                }
                else if (messageType.equals("info")){
                    messageTypeInt = TastyToast.INFO;
                }
                else if (messageType.equals("success")){
                    messageTypeInt = TastyToast.SUCCESS;
                }
                else if (messageType.equals("warning")){
                    messageTypeInt = TastyToast.WARNING;
                }


                //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                TastyToast.makeText(getApplicationContext(), message, TastyToast.LENGTH_LONG, messageTypeInt);
            }
        });
    } //push 구현. notification을 사용하면 background에서는 푸시받을 수 없음
}

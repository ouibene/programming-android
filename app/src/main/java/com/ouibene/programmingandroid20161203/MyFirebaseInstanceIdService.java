package com.ouibene.programmingandroid20161203;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Youngeun-Lee on 2016. 12. 10..
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    final static String TAG = MyFirebaseInstanceIdService.class.getName();

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "onTokenRefresh : " + token); //원래는 server로 http 요청을 보내서 신호보내야 함
    }//push token이 바뀌면 이것이 호출됨
}

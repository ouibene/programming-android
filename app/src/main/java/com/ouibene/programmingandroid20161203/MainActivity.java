package com.ouibene.programmingandroid20161203;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;

    GoogleApiClient mGoogleApiClient;

    String mUsername;
    String mPhotoUrl;

    final static String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if ( mFirebaseUser == null ) {
            // logout 상황
            Toast.makeText(this, "로그인이 필요합니다", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            // login 상황
            mUsername = mFirebaseUser.getDisplayName();
            Toast.makeText(this, mUsername, Toast.LENGTH_LONG).show();

            if ( mFirebaseUser.getPhotoUrl() != null ) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }

        TextView usernameTextView = (TextView) findViewById(R.id.name_textview);
        ImageView photoImageView = (ImageView) findViewById(R.id.photo_imageview) ;

        usernameTextView.setText(mUsername);
        if ( mPhotoUrl != null ) {
            Glide.with(this).load(mPhotoUrl).into(photoImageView);
        }
        //else 를 넣어서 사진이 없을 때의 default image를 넣는 것이 바람직하다.

        //로그인 후 유저부분 누르면 로그아웃 됨
        usernameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this)
                        .setMessage("정말 로그아웃 하시겠습니까?")
                        .setTitle("logout!")
                        .setPositiveButton("예.(단호박)", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                mFirebaseAuth.signOut();

                                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                                LoginManager.getInstance().logOut();

                                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                                startActivity(intent);
                                finish();
                            }
                });
                dialog.show();
            }
        });


        GoogleApiClient googleApiClient = new GoogleApiClient.Builder (this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        String pushToken = FirebaseInstanceId.getInstance().getToken();
        if (pushToken != null) {
        Log.d(TAG, "pushToken : "+ pushToken);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "구글 플레이 서비스에 접속할 수 없습니다", Toast.LENGTH_SHORT).show();
    }

}
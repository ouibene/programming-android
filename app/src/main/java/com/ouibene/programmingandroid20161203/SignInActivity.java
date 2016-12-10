package com.ouibene.programmingandroid20161203;

/**
 * Created by Youngeun-Lee on 2016. 12. 3..
 */

import android.content.Intent;
import android.hardware.camera2.params.Face;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, FirebaseAuth.AuthStateListener {
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mFirebaseAuthListener;

    SignInButton mSigninGoogleButton;
    GoogleApiClient mGoogleApiClient;

    LoginButton mSigninFacebookButton;
    CallbackManager mFacebookCallbackManager;

    static final String TAG = SignInActivity.class.getName();
    static final int RC_GOOGLE_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuth.addAuthStateListener(this);

        setTitle("Login");

        /*
         * Firebase
         */
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if ( user != null ) {
                    Log.d(TAG, "sign in");

                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Log.d(TAG, "sign out");
                }

            }
        };

        /*
         *  Google Login
         */
        mSigninGoogleButton = (SignInButton) findViewById(R.id.sign_in_google_button);
        mSigninGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this) //call back을 대비. google service에 접속하지 못할 때 사용됨
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mFacebookCallbackManager = CallbackManager.Factory.create();

        mSigninFacebookButton = (LoginButton) findViewById(R.id.sign_in_facebook_button);
        mSigninFacebookButton.setReadPermissions("email", "public_profile");
        mSigninFacebookButton.registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String token = loginResult.getAccessToken().getToken();
                AuthCredential credential = FacebookAuthProvider.getCredential(token);
                mFirebaseAuth.signInWithCredential(credential);
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebookLogin:onCancel");
            }//유저가 캔슬했을 때

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebookLogin:onError", error);
            }//상태 처리가 실패했을 때
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mFirebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if ( mFirebaseAuthListener != null )
            mFirebaseAuth.removeAuthStateListener(mFirebaseAuthListener);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }//구글 서버에 접속 실패

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        //firebase auth state 변경 시 (login -> logout or logout -> login)
        FirebaseUser user = firebaseAuth.getCurrentUser ();
        if (user != null){
            //TODO: 로그인이 되었을 때
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            Log.d(TAG, "logout"); //null 이면 로그아웃상태
        }
    }

    @Override //ctrl+O
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);

        if ( requestCode == RC_GOOGLE_SIGN_IN ) { //request code가 9001로 회신이 오면 처리된다
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if ( result.isSuccess() ) { //성공여부
                String token = result.getSignInAccount().getIdToken();
                AuthCredential credential = GoogleAuthProvider.getCredential(token, null); //credential style로 정리
                mFirebaseAuth.signInWithCredential(credential);
            }
            else {
                Log.d(TAG, "Google Login Failed." + result.getStatus());
            }
        }
    }



}
package com.example.fake_book;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private Context mContext;
    private LoginButton btn_facebook_login;
    private CallbackManager mCallbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        mContext = getApplicationContext();

        mCallbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                nextActivity(newProfile);
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if (isLoggedIn) {
            nextActivity(Profile.getCurrentProfile());
        }

        btn_facebook_login = findViewById(R.id.btn_facebook_login);
        btn_facebook_login.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

            // 로그인 성공 시 호출 됩니다. Access Token 발급 성공.
            @Override
            public void onSuccess(LoginResult loginResult) {
                Profile profile = Profile.getCurrentProfile();
                nextActivity(profile);
            }

            // 로그인 창을 닫을 경우, 호출됩니다.
            @Override
            public void onCancel() {
                Log.e("Callback :: ", "onCancel");
            }

            // 로그인 실패 시에 호출됩니다.
            @Override
            public void onError(FacebookException error) {
                Log.e("Callback :: ", "onError : " + error.getMessage());
            }

            // 사용자 정보 요청
            public JSONObject requestMe(AccessToken token) {
                final JSONObject[] result = {new JSONObject()};
                Bundle parameters = new Bundle();
                GraphRequest graphRequest = GraphRequest.newMeRequest(token,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                result[0] = object;
                            }
                        });

                parameters.putString("fields", "id,name,email,gender,birthday");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
                return result[0];
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
        nextActivity(profile);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.startTracking();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        mCallbackManager.onActivityResult(requestCode, resultCode, intent);
    }

    private void nextActivity(Profile profile){
        if (profile != null){
            Intent main = new Intent(LoginActivity.this, MainActivity.class);
            main.putExtra("name", profile.getName());
            main.putExtra("id", profile.getId());
            main.putExtra("imageUrl", profile.getProfilePictureUri(200,200).toString());
            startActivity(main);
        }
    }

    // 뒤로가기 버튼을 두번 연속으로 눌러야 종료되게끔 하는 메소드
    private long time= 0;
    @Override
    public void onBackPressed(){
        if(System.currentTimeMillis()-time>=2000){
            time=System.currentTimeMillis();
            Toast.makeText(getApplicationContext(),"뒤로 버튼을 한번 더 누르면 종료합니다.",Toast.LENGTH_SHORT).show();
        }else if(System.currentTimeMillis()-time<2000){
            ActivityCompat.finishAffinity(LoginActivity.this);
        }
    }
}
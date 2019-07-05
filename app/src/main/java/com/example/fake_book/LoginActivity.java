package com.example.fake_book;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.AccessTokenManager;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;


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

        btn_facebook_login = findViewById(R.id.btn_facebook_login);
        btn_facebook_login.setReadPermissions(Arrays.asList("public_profile", "email"));
        btn_facebook_login.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

            // 로그인 성공 시 호출 됩니다. Access Token 발급 성공.
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e("Callback :: ", "onSuccess");
                JSONObject user_info = requestMe(loginResult.getAccessToken());

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("user_info", user_info.toString());
                startActivity(intent);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

}
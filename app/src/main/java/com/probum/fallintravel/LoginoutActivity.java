package com.probum.fallintravel;

import android.content.Context;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginDefine;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginoutActivity extends AppCompatActivity {

    //네이버
    private static final String TAG = "OAuthSampleActivity";
    private static String OAUTH_CLIENT_ID = "garBR3t_hzQ5qyrzHVRQ";
    private static String OAUTH_CLIENT_SECRET = "I9Wa6QmnEq";
    private static String OAUTH_CLIENT_NAME = "여행에 빠지다";
    private static OAuthLogin mOAuthLoginInstance;
    private static Context mContext;
    private OAuthLoginButton mOAuthLoginButton;
    public Map<String, String> mUserInfoMap;

    //카카오
    private SessionCallback callback;
    LoginButton loginButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //네이버
        OAuthLoginDefine.DEVELOPER_VERSION = true;
        mContext = this;
        initData();
        initView();
        if (G.isLogin == true) logOut();
        //카카오
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        // 카카오톡 로그인 버튼
        loginButton = (LoginButton)findViewById(R.id.com_kakao_login);
        loginButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if(!isConnected()){
                        Toast.makeText(LoginoutActivity.this,"인터넷 연결을 확인해주세요",Toast.LENGTH_SHORT).show();
                    }
                }

                if(isConnected()){
                    return false;
                }else{
                    return true;
                }
            }
        });

        if(Session.getCurrentSession().isOpened()){
            requestMe();
        }

        //카카오

    }

    private void initData() {
        mOAuthLoginInstance = OAuthLogin.getInstance();

        mOAuthLoginInstance.init(mContext, OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET, OAUTH_CLIENT_NAME);
		/*
		 * 2015년 8월 이전에 등록하고 앱 정보 갱신을 안한 경우 기존에 설정해준 callback intent url 을 넣어줘야 로그인하는데 문제가 안생긴다.
		 * 2015년 8월 이후에 등록했거나 그 뒤에 앱 정보 갱신을 하면서 package name 을 넣어준 경우 callback intent url 을 생략해도 된다.
		 */
        //mOAuthLoginInstance.init(mContext, OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET, OAUTH_CLIENT_NAME, OAUTH_callback_intent_url);
    }

    private void initView() {

        mOAuthLoginButton = (OAuthLoginButton) findViewById(R.id.buttonOAuthLoginImg);
        mOAuthLoginButton.setOAuthLoginHandler(mOAuthLoginHandler);

    }

    @Override
    protected void onResume() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onResume();

    }

    /**
     * startOAuthLoginActivity() 호출시 인자로 넘기거나, OAuthLoginButton 에 등록해주면 인증이 종료되는 걸 알 수 있다.
     */
    private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {
                String accessToken = mOAuthLoginInstance.getAccessToken(mContext);
                String refreshToken = mOAuthLoginInstance.getRefreshToken(mContext);
                long expiresAt = mOAuthLoginInstance.getExpiresAt(mContext);
                String tokenType = mOAuthLoginInstance.getTokenType(mContext);

                Log.i("mOAuthState 인증확인", mOAuthLoginInstance.getState(mContext).toString());
                new RefreshTokenTask().execute();
                new RequestApiTask().execute();
            } else {
                String errorCode = mOAuthLoginInstance.getLastErrorCode(mContext).getCode();
                String errorDesc = mOAuthLoginInstance.getLastErrorDesc(mContext);
            }
        }

        ;
    };


    public void logOut() {
        //////카카오////
        if(Session.getCurrentSession().isOpened()) {
            requestLogout();
        }
        ////////////////
        mOAuthLoginInstance.logout(mContext);
        new DeleteTokenTask().execute();
        G.isLogin = false;
        G.nickname = "로그인을 해주세요";
        G.profile_image = "https://raw.githubusercontent.com/Leekibum/FallinTravel1-fallintravel0808/50e8dbf96beaf25e06f735f5e071c272787bfe41/account.png";
        setResult(G.LOGOUT, getIntent());
        finish();
    }


    private class DeleteTokenTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            boolean isSuccessDeleteToken = mOAuthLoginInstance.logoutAndDeleteToken(mContext);

            if (!isSuccessDeleteToken) {
                // 서버에서 token 삭제에 실패했어도 클라이언트에 있는 token 은 삭제되어 로그아웃된 상태이다
                // 실패했어도 클라이언트 상에 token 정보가 없기 때문에 추가적으로 해줄 수 있는 것은 없음
                Log.d(TAG, "errorCode:" + mOAuthLoginInstance.getLastErrorCode(mContext));
                Log.d(TAG, "errorDesc:" + mOAuthLoginInstance.getLastErrorDesc(mContext));
            }

            return null;
        }

        protected void onPostExecute(Void v) {
        }
    }

    private class RequestApiTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
             //실행되면 doInBackground가 새로 작업하는동안 전의 작업 내용을 지움.
        }

        @Override
        protected String doInBackground(Void... params) {
            String url = "https://openapi.naver.com/v1/nid/me";//https://openapi.naver.com/v1/nid/getUserProfile.xml //구시대의 유산
            String at = mOAuthLoginInstance.getAccessToken(mContext);
            mUserInfoMap = requestNaverUserInfo(mOAuthLoginInstance.requestApi(mContext, at, url));
            return mOAuthLoginInstance.requestApi(mContext, at, url);
        }

        protected void onPostExecute(String content) {
            //doinBackground가 종료하면 값을 리턴해줌.

            if (mUserInfoMap.get("nickname") == null) {
                Toast.makeText(mContext, "로그인 실패하였습니다. 잠시후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
            } else {
                G.id=mUserInfoMap.get("id");
                G.nickname = mUserInfoMap.get("nickname");
                G.profile_image = mUserInfoMap.get("profile_image");
                G.isLogin = true;
                Toast.makeText(mContext, mUserInfoMap.get("nickname") + "님 로그인을 환영합니다.", Toast.LENGTH_SHORT).show();
                setResult(G.LOGIN, getIntent());
                finish();
            }
        }
    }


    private HashMap<String, String> requestNaverUserInfo(String response) { //xml 파싱
        final String f_array[] = new String[6];

        try {
            JSONObject json = new JSONObject(response);

            Log.i("response", response);
            json = json.getJSONObject("response");
            f_array[0] = json.getString("nickname");
            f_array[1] = json.getString("profile_image");
            f_array[2] = json.getString("age");
            f_array[3] = json.getString("gender");
            f_array[4] = json.getString("id");
            f_array[5] = json.getString("birthday");

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("error", e.toString());
        }

        HashMap<String, String> resultMap = new HashMap<>();
        resultMap.put("nickname", f_array[0]);
        resultMap.put("profile_image", f_array[1]);
        resultMap.put("age", f_array[2]);
        resultMap.put("gender", f_array[3]);
        resultMap.put("id", f_array[4]);
        resultMap.put("birthday", f_array[5]);

        return resultMap;
    }


    private class RefreshTokenTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return mOAuthLoginInstance.refreshAccessToken(mContext);
        }

        protected void onPostExecute(String res) {

        }
    }

    //카카오


    //인터넷 연결상태 확인
    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }

        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            //access token을 성공적으로 발급 받아 valid access token을 가지고 있는 상태. 일반적으로 로그인 후의 다음 activity로 이동한다.
            if(Session.getCurrentSession().isOpened()){ // 한 번더 세션을 체크해주었습니다.
                requestMe();
            }
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Logger.e(exception);
            }
        }
    }

    private void requestLogout() {
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
        });
    }

    private void requestMe() {

        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Log.e("onFailure", errorResult + "");
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.e("onSessionClosed",errorResult + "");
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
//                Log.e("onSuccess",userProfile.toString());
//                user_nickname.setText(userProfile.getNickname());
//                user_email.setText(userProfile.getEmail());
//                aQuery.id(user_img).image(userProfile.getThumbnailImagePath()); // <- 프로필 작은 이미지 , userProfile.getProfileImagePath() <- 큰 이미지
                G.id=userProfile.getEmail();
                G.nickname = userProfile.getNickname();
                G.profile_image = userProfile.getThumbnailImagePath();
                G.isLogin = true;
                if (G.isLogin==false)Toast.makeText(mContext, userProfile.getNickname() + "님 로그인을 환영합니다.", Toast.LENGTH_SHORT).show();
                setResult(G.LOGIN, getIntent());
                finish();
            }

            @Override
            public void onNotSignedUp() {
                Log.e("onNotSignedUp","onNotSignedUp");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }
}

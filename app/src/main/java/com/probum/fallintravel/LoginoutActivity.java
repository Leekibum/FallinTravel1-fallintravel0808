package com.probum.fallintravel;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginDefine;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

public class LoginoutActivity extends AppCompatActivity {

    private ISessionCallback callback;


    private static final String TAG = "OAuthSampleActivity";

    /**
     * client 정보를 넣어준다.
     */
    private static String OAUTH_CLIENT_ID = "garBR3t_hzQ5qyrzHVRQ";
    private static String OAUTH_CLIENT_SECRET = "I9Wa6QmnEq";
    private static String OAUTH_CLIENT_NAME = "여행에 빠지다";


    private static OAuthLogin mOAuthLoginInstance;
    private static Context mContext;

    /** UI 요소들 */
    private TextView mApiResultText;
    private static TextView mOauthAT;
    private static TextView mOauthRT;
    private static TextView mOauthExpires;
    private static TextView mOauthTokenType;
    private static TextView mOAuthState;

    private OAuthLoginButton mOAuthLoginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callback=new SessionCallback();
        Session.getCurrentSession().addCallback(callback);

        OAuthLoginDefine.DEVELOPER_VERSION = true;

        mContext = this;


        initData();
        initView();

        this.setTitle("OAuthLoginSample Ver." + OAuthLogin.getVersion());
        if (Build.VERSION.SDK_INT>=21){ //버전 21 이상은 위에 상태바 색 변경경
            getWindow().setStatusBarColor(0xff55ccc0);
        }
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
        mApiResultText = (TextView) findViewById(R.id.api_result_text);

//        mOauthAT = (TextView) findViewById(R.id.oauth_access_token);
//        mOauthRT = (TextView) findViewById(R.id.oauth_refresh_token);
//        mOauthExpires = (TextView) findViewById(R.id.oauth_expires);
//        mOauthTokenType = (TextView) findViewById(R.id.oauth_type);
//        mOAuthState = (TextView) findViewById(R.id.oauth_state);

        mOAuthLoginButton = (OAuthLoginButton) findViewById(R.id.buttonOAuthLoginImg);
        mOAuthLoginButton.setOAuthLoginHandler(mOAuthLoginHandler);

        updateView();
    }



    private void updateView() {
//        mOauthAT.setText(mOAuthLoginInstance.getAccessToken(mContext));
//        mOauthRT.setText(mOAuthLoginInstance.getRefreshToken(mContext));
//        mOauthExpires.setText(String.valueOf(mOAuthLoginInstance.getExpiresAt(mContext)));
//        mOauthTokenType.setText(mOAuthLoginInstance.getTokenType(mContext));
//        mOAuthState.setText(mOAuthLoginInstance.getState(mContext).toString());

    }

    @Override
    protected void onResume() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onResume();

    }

    /**
     * startOAuthLoginActivity() 호출시 인자로 넘기거나, OAuthLoginButton 에 등록해주면 인증이 종료되는 걸 알 수 있다.
     */
    static private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {
                String accessToken = mOAuthLoginInstance.getAccessToken(mContext);
                String refreshToken = mOAuthLoginInstance.getRefreshToken(mContext);
                long expiresAt = mOAuthLoginInstance.getExpiresAt(mContext);
                String tokenType = mOAuthLoginInstance.getTokenType(mContext);
//                mOauthAT.setText(accessToken);
//                mOauthRT.setText(refreshToken);
//                mOauthExpires.setText(String.valueOf(expiresAt));
//                mOauthTokenType.setText(tokenType);
//                mOAuthState.setText(mOAuthLoginInstance.getState(mContext).toString());
            } else {
                String errorCode = mOAuthLoginInstance.getLastErrorCode(mContext).getCode();
                String errorDesc = mOAuthLoginInstance.getLastErrorDesc(mContext);
                Toast.makeText(mContext, "errorCode:" + errorCode + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
            }
        };
    };

    public void onButtonClick(View v) throws Throwable {

        switch (v.getId()) {
            case R.id.buttonOAuth: { //인증하기
                mOAuthLoginInstance.startOauthLoginActivity(this, mOAuthLoginHandler);
                break;
            }
            case R.id.buttonVerifier: {
                new RequestApiTask().execute();
                break;
            }
            case R.id.buttonRefresh: {
                new RefreshTokenTask().execute();
                break;
            }
            case R.id.buttonOAuthLogout: {
                mOAuthLoginInstance.logout(mContext);
                updateView();
                break;
            }
            case R.id.buttonOAuthDeleteToken: {
                new DeleteTokenTask().execute();
                break;
            }
            default:
                break;
        }
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
            updateView();
        }
    }

    private class RequestApiTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
//            mApiResultText.setText((String) ""); //실행되면 doInBackground가 새로 작업하는동안 전의 작업 내용을 지움.
        }
        @Override
        protected String doInBackground(Void... params) {
            String url = "https://openapi.naver.com/v1/nid/getUserProfile.xml";
            String at = mOAuthLoginInstance.getAccessToken(mContext);
            return mOAuthLoginInstance.requestApi(mContext, at, url);
        }
        protected void onPostExecute(String content) {
//            mApiResultText.setText((String) content);  //doinBackground가 종료하면 값을 리턴해줌.
        }
    }

    private class RefreshTokenTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return mOAuthLoginInstance.refreshAccessToken(mContext);
        }
        protected void onPostExecute(String res) {
            updateView();
        }
    }

    //아래는 카카오 하는중 ....
    private class SessionCallback implements ISessionCallback{

        @Override
        public void onSessionOpened() {
            calltoSignupActivity();//세션 연결성공시 액티비티 호출
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception!=null){ Logger.e(exception); }
            setContentView(R.layout.activity_login); //세션 연결이 실패했을때 로그인 화면을 다시 불러움
        }
    }

    protected void calltoSignupActivity(){
        Intent intent=new Intent(this,KakaoSignupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}

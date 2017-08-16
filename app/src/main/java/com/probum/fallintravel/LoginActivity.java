package com.probum.fallintravel;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

public class LoginActivity extends AppCompatActivity {

    private ISessionCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callback=new SessionCallback();
        Session.getCurrentSession().addCallback(callback);


        if (Build.VERSION.SDK_INT>=21){ //버전 21 이상은 위에 상태바 색 변경경
            getWindow().setStatusBarColor(0xff55ccc0);
        }
    }

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

package com.probum.fallintravel;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;

/**
 * Created by alfo6-25 on 2017-08-16.
 */

public class KakaoSignupActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestMe();
    }

    protected void requestMe(){ //유저의 정보를 받아오는 함수
        UserManagement.requestMe(new MeResponseCallback() {

            @Override
            public void onFailure(ErrorResult errorResult) {
                String message="failed to get user info. msg="+errorResult;
                Logger.d(message);

                ErrorCode result= ErrorCode.valueOf(errorResult.getErrorCode());
                if (result==ErrorCode.CLIENT_ERROR_CODE){
                    finish();
                }else {
                    redirectLoginActivity();
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                redirectLoginActivity();
            }

            @Override
            public void onNotSignedUp() {
                //카카오톡 회원이 아닐시 showSingup()을 호출해야함.
            }

            @Override
            public void onSuccess(UserProfile result) {
                Logger.d("UserProfile : " +result);
                redirectMainActivity();
            }
        });
    }

    private void redirectMainActivity(){
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
    protected void redirectLoginActivity(){
        Intent intent=new Intent(this,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

}

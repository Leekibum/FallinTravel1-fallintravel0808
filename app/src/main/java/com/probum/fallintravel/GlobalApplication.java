package com.probum.fallintravel;

import android.app.Activity;
import android.app.Application;

import com.kakao.auth.KakaoSDK;

/**
 * Created by alfo6-25 on 2017-08-16.
 */

public class GlobalApplication extends Application {
    private static volatile GlobalApplication instance=null;
    private static volatile Activity currenActivity=null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        KakaoSDK.init(new KakaoSDKAdapter());
    }
    public static Activity gerCurrentActivity(){
        return currenActivity;
    }

    public static void setCurrenActivity(Activity currenActivity){
        GlobalApplication.currenActivity=currenActivity;
    }

    public static GlobalApplication getGlobalApplicationContext(){
        if (instance==null)
            throw new IllegalStateException("this applicadtion does not inherit com.kakao.GlobalApplication");
        return instance;
    }

    //어플리케이션 종료시 singLeton 어플리케이션 객체 초기화

    @Override
    public void onTerminate() {
        super.onTerminate();
        instance=null;
    }
}

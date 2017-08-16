package com.probum.fallintravel;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

import com.tsengvn.typekit.Typekit;

import java.lang.reflect.Field;

/**
 * Created by alfo6-25 on 2017-08-01.
 */

public class ApplicationBase extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        setDefaultFont(this, "DEFAULT", "ssanaiL.ttf");
//        setDefaultFont(this, "SANS_SERIF", "ssanaiL.ttf");
        setDefaultFont(this, "SERIF", "ssanaiL.ttf");
    }

    public static void setDefaultFont(Context ctx,
                                      String staticTypefaceFieldName, String fontAssetName) {
        final Typeface regular = Typeface.createFromAsset(ctx.getAssets(),
                fontAssetName);
        replaceFont(staticTypefaceFieldName, regular);
    }

    protected static void replaceFont(String staticTypefaceFieldName,
                                      final Typeface newTypeface) {
        try {
            final Field StaticField = Typeface.class
                    .getDeclaredField(staticTypefaceFieldName);
            StaticField.setAccessible(true);
            StaticField.set(null, newTypeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}


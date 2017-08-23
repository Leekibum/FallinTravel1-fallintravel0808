package com.probum.fallintravel;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.SpannableString;
import android.text.Spanned;

/**
 * Created by alfo6-25 on 2017-07-25.
 */

public class PageAdapter extends FragmentPagerAdapter {


    public Fragment[] fragments=new Fragment[3];
    AssetManager asset;
    FestivalFragment festivalFragment;
    TourFragment tourFragment;
    LocationFragment locationFragment;

    public PageAdapter(FragmentManager fm) {
        super(fm);
        festivalFragment=new FestivalFragment();
        tourFragment = new TourFragment();
        locationFragment= new LocationFragment();

        fragments[0]=festivalFragment;
        fragments[1]=tourFragment;
        fragments[2]=locationFragment;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        Typeface typeface = Typeface.createFromAsset(asset,"fonts/yungothic360.ttf");

        SpannableString span1=new SpannableString("축제");
        span1.setSpan(new CustomTypefaceSpan("", typeface),0,span1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString span2=new SpannableString("관광지");
        span2.setSpan(new CustomTypefaceSpan("", typeface),0,span2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString span3=new SpannableString("여행코스");
        span3.setSpan(new CustomTypefaceSpan("", typeface),0,span3.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString[] spannableStrings=new SpannableString[]{span1,span2,span3};



        return spannableStrings[position];

    }


    public void sendassets(AssetManager assets) {
        asset=assets;
    }

}

package com.probum.fallintravel;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by alfo6-25 on 2017-08-11.
 */

public class DetailAdapter extends PagerAdapter {

    ArrayList<String> imgs;
    LayoutInflater inflater;
    Context context;

    public DetailAdapter(ArrayList<String> imgs, LayoutInflater inflater, Context context) {
        this.imgs = imgs;
        this.inflater = inflater;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (imgs.size()==0)return 1;
        return imgs.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View page=inflater.inflate(R.layout.page,container,false);
        ImageView img=(ImageView)page.findViewById(R.id.img);
        if (imgs.size()==0)img.setImageResource(R.drawable.noimageavailable);
        else Glide.with(context).load(imgs.get(position)).into(img);

        container.addView(page);

        return page;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }
}

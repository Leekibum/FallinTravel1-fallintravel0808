package com.probum.fallintravel;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class FestivalAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Item> items;

    public FestivalAdapter(Context context, ArrayList<Item> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder=new ViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_item,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder holder1=(ViewHolder)holder;

        holder1.tvtitle.setText(items.get(position).getTitle());
        holder1.tvtitle.setTag(items.get(position).getContentid());
        holder1.tvtime.setText(items.get(position).getTime());
        holder1.tvtime.setTag(items.get(position).getContenttypeid());

        if (items.get(position).firstimage.equals("noimage")) {
            Glide.with(context).load(R.drawable.noimageavailable).into(holder1.img);//  나중에 이미지가 없어요 같은 이미지로 변경하기
        } else {
            Glide.with(context).load(items.get(position).firstimage).into(holder1.img);
        }


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvtitle,tvtime;
        ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);

            tvtitle=(TextView)itemView.findViewById(R.id.tv_title);
            tvtime=(TextView)itemView.findViewById(R.id.tv_time);
            img=(ImageView)itemView.findViewById(R.id.img);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context,DetailActivity.class);
                    intent.putExtra("contentid",tvtitle.getTag()+"");
                    intent.putExtra("contenttypeid",tvtime.getTag()+"");
                    intent.putExtra("firstimage",items.get(getPosition()).firstimage);


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((MainActivity)context,new Pair<View, String>(img,"IMG"));
                        context.startActivity(intent, options.toBundle());
                    }
                }
            });
        }
    }
}

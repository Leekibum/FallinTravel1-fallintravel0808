package com.probum.fallintravel;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;



public class ReplyAdapter extends RecyclerView.Adapter {
    ArrayList<ReplyItem> replyItems;
    Context context;

    public ReplyAdapter(ArrayList<ReplyItem> replyItems, Context context) {
        this.replyItems = replyItems;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder=new ViewHolder(LayoutInflater.from(context).inflate(R.layout.reply_item,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder holder1=(ViewHolder)holder;
        holder1.tvreply.setText(replyItems.get(position).getReply());
        if (!(replyItems.size() ==0))Glide.with(context).load(replyItems.get(position).getProfileimage()).into(holder1.circle);
        if (replyItems.get(position).getId().equals(G.id)&&!(replyItems.size()==0))holder1.imgdelete.setVisibility(View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return replyItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvreply;
        CircleImageView circle;
        ImageView imgdelete;

        public ViewHolder(View itemView) {
            super(itemView);
            tvreply=(TextView)itemView.findViewById(R.id.tv_reply);
            circle=(CircleImageView)itemView.findViewById(R.id.circle_reply);
            imgdelete=(ImageView)itemView.findViewById(R.id.img_delete);
        }
    }
}

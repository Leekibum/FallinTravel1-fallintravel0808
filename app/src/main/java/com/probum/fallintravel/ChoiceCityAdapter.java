package com.probum.fallintravel;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by alfo6-25 on 2017-08-08.
 */

public class ChoiceCityAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<ChoiceCityItem> choiceCityItems;
    ChoiceCityActivity choiceCityActivity;

    public ChoiceCityAdapter(Context context, ArrayList<ChoiceCityItem> choiceCityItems) {
        this.context = context;
        this.choiceCityItems = choiceCityItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder=new ViewHolder(LayoutInflater.from(context).inflate(R.layout.choicecity_item,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder myholder=(ViewHolder)holder;

        myholder.tvchoice2.setVisibility(View.GONE);

        myholder.tvchoice.setText(choiceCityItems.get(position).name);
        myholder.tvchoice.setTag(choiceCityItems.get(position).code);
    }

    @Override
    public int getItemCount() {
        return choiceCityItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvchoice;
        TextView tvchoice2;

        public ViewHolder(View itemView) {
            super(itemView);
            tvchoice=(TextView)itemView.findViewById(R.id.tv_choice);
            tvchoice2=(TextView)itemView.findViewById(R.id.tv_choice2);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    choiceCityActivity= (ChoiceCityActivity) context;
                    G.cityname=tvchoice.getText().toString();
                    G.citycode=tvchoice.getTag().toString();
                    choiceCityActivity.choicesigungu();
                    choiceCityActivity.tvallnotify();
                }
            });
        }
    }
}

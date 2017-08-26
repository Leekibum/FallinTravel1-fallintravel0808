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
 * Created by alfo6-25 on 2017-08-09.
 */

public class ChoicesigunguAdapter extends RecyclerView.Adapter {

    ArrayList<ChoiceCityItem> choiceCityItems2;
    Context context;
    ChoiceCityActivity choiceCityActivity;

    public ChoicesigunguAdapter(ArrayList<ChoiceCityItem> choiceCityItems2, Context context) {
        this.choiceCityItems2 = choiceCityItems2;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder=new ViewHolder(LayoutInflater.from(context).inflate(R.layout.choicecity_item,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder holder1= (ViewHolder) holder;

        holder1.tvchoice1.setVisibility(View.GONE);
        holder1.tvchoice2.setText(choiceCityItems2.get(position).name);
        holder1.tvchoice2.setTag(choiceCityItems2.get(position).code);

    }

    @Override
    public int getItemCount() {
        return choiceCityItems2.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvchoice2;
        TextView tvchoice1;

        public ViewHolder(View itemView) {
            super(itemView);
            tvchoice2=(TextView)itemView.findViewById(R.id.tv_choice2);
            tvchoice1=(TextView)itemView.findViewById(R.id.tv_choice);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    choiceCityActivity=(ChoiceCityActivity)context;
                    G.sigunguName=tvchoice2.getText().toString();
                    G.sigunguCode=tvchoice2.getTag().toString();
                    if (!G.isFirst)G.isFirst=true;
                    choiceCityActivity.finish();
                }
            });

        }
    }
}

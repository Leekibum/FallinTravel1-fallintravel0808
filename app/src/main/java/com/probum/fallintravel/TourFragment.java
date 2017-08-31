package com.probum.fallintravel;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


public class TourFragment extends Fragment {

    ArrayList<Item> items=new ArrayList<>();
    RecyclerView recyclerView;
    TourAdapter adapter;
    RequestQueue requestQueue;
    int numOfRows;
    int pageNo;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestQueue= Volley.newRequestQueue(getActivity());
        numOfRows=12;
        pageNo=1;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_tour, container, false);

        recyclerView=(RecyclerView)view.findViewById(R.id.layout_recycler);

        RecyclerView.LayoutManager manager= new GridLayoutManager(getActivity(),2,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager);

        adapter=new TourAdapter(items,getActivity(),getActivity());
        recyclerView.setAdapter(adapter);

        readtour();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItemPostion=((GridLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount= recyclerView.getAdapter().getItemCount()-1;
                if (lastVisibleItemPostion==itemTotalCount){
                    pageNo+=1;
                    readtour();
                }
            }
        });


        return view;
    }

    void readtour(){
//        String url="http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?ServiceKey="+ G.serviceKey+"&contentTypeId=12&areaCode="+G.citycode+"&sigunguCode="+G.sigunguCode+"&cat1=&cat2=&cat3=&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange="+G.arrange+"&numOfRows="+numOfRows+"&pageNo="+pageNo+"&_type=json";
        String url="http://nsdfdsf.com"; //트래픽 초과 안나게 !
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONObject obj=response.getJSONObject("response");
                    obj=obj.getJSONObject("body");
                    obj=obj.getJSONObject("items");
                    JSONArray jsonArray=obj.getJSONArray("item");


                    for (int i=0;i<jsonArray.length();i++){
                        obj=jsonArray.getJSONObject(i);
                        valueZero();

                        String title=obj.getString("title");
                        String contentid=obj.getString("contentid");
                        String firstimage="noimage";
                        String contenttypeid=obj.getString("contenttypeid");
                        if (obj.has("firstimage")) firstimage=obj.getString("firstimage");

                        items.add(new Item(title,firstimage,contentid,contenttypeid));
                        adapter.notifyDataSetChanged();
                    }


                } catch (JSONException e) { valueZero();}

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void setValue() {
        pageNo=1;
    }

    void valueZero(){
        if (items.size()==0){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                recyclerView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.noimageavailable));
            } else {
                recyclerView.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.noimageavailable));
            }
        }
        else if (items.size()>0){
            recyclerView.setBackgroundColor(Color.WHITE);
        }
    }
}

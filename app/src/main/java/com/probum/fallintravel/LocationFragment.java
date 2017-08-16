package com.probum.fallintravel;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class LocationFragment extends Fragment {

    ArrayList<Item> items=new ArrayList<>();
    RecyclerView recyclerView;
    TourAdapter adapter;
    RequestQueue requestQueue;
    int numOfRows;
    int pageNo;

//    numOfRows=12&pageNo=1


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue= Volley.newRequestQueue(getActivity());
        numOfRows=12;
        pageNo=1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_location, container, false);

        recyclerView=(RecyclerView)view.findViewById(R.id.layout_recycler);

        RecyclerView.LayoutManager manager= new GridLayoutManager(getActivity(),2,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager);

        adapter=new TourAdapter(items,getActivity());
        recyclerView.setAdapter(adapter);

        readLocation();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItemPostion=((GridLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount= recyclerView.getAdapter().getItemCount()-1;
                if (lastVisibleItemPostion==itemTotalCount){
                    pageNo+=1;
                    readLocation();
                }
            }
        });

        return view;
    }

    void readLocation(){
        String url="http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?ServiceKey="+ G.serviceKey+"&contentTypeId=&areaCode="+G.citycode+"&sigunguCode="+G.sigunguCode+"&cat1=&cat2=&cat3=&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=C&numOfRows="+numOfRows+"&pageNo="+pageNo+"&_type=json";
        Log.i("UrlUrlURl",url);
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

                        String title=obj.getString("title");
                        String contentid=obj.getString("contentid");
                        String firstimage="noimage";
                        String contenttypeid=obj.getString("contenttypeid");
                        if (obj.has("firstimage")) firstimage=obj.getString("firstimage");
                        items.add(new Item(title,firstimage,contentid,contenttypeid));
                        adapter.notifyDataSetChanged();
                    }


                } catch (JSONException e) { }

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

}

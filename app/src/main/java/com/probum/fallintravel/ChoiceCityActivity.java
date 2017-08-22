package com.probum.fallintravel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
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

public class ChoiceCityActivity extends AppCompatActivity {

    RecyclerView recyclercity,recyclerdistrict;
    ArrayList<ChoiceCityItem> choiceCityItems=new ArrayList<>();
    ArrayList<ChoiceCityItem> choiceCityItems2=new ArrayList<>();
    ChoiceCityAdapter adapter;
    ChoicesigunguAdapter adapter2;
    RecyclerView.LayoutManager layoutManager;
    RequestQueue requestQueue;
    Context context;
    TextView tvall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        context=this;
        requestQueue= Volley.newRequestQueue(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_city);
        tvall=(TextView)findViewById(R.id.tv_all);
        recyclercity=(RecyclerView)findViewById(R.id.recycler_city);
        recyclerdistrict=(RecyclerView)findViewById(R.id.recycler_district);

        tvall.setText(G.cityname+"   전체");

        adapter=new ChoiceCityAdapter(this,choiceCityItems);
        layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclercity.setLayoutManager(layoutManager);
        recyclercity.setAdapter(adapter);

        adapter2=new ChoicesigunguAdapter(choiceCityItems2,this);
//        layoutManager=new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerdistrict.setLayoutManager(layoutManager);
        recyclerdistrict.setAdapter(adapter2);



        if (Build.VERSION.SDK_INT>=21){ //버전 21 이상은 위에 상태바 색 변경경
            getWindow().setStatusBarColor(0xff55ccc0);
        }

        choiceCity();


    }//onCreate

    public void clickAll(View v){
        Intent intent=getIntent();
        setResult(G.SELECT_LOCATION,intent);
//        Toast.makeText(context, "전체 클릭", Toast.LENGTH_SHORT).show();
        G.sigunguName="전체";
        G.sigunguCode="";
        finish();
    }

    @Override
    protected void onDestroy() {
        saveData();
        super.onDestroy();
    }

    @Override
    protected void onPause() {

        super.onPause();
    }


    void  saveData(){
        //data.xml이라는 문서에 저장
        SharedPreferences pref=getSharedPreferences("data",MODE_PRIVATE);//xml파일
        SharedPreferences.Editor editor =pref.edit();
        editor.putString("Cityname",G.cityname);
        editor.putString("CityCode",G.citycode);
        editor.commit();
    }


    void choiceCity(){

        String url="http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaCode?ServiceKey="+G.serviceKey+"&numOfRows=30&pageNo=1&MobileOS=ETC&MobileApp=TestApp&_type=json";
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONObject object=response.getJSONObject("response");
                    object=object.getJSONObject("body");
                    object=object.getJSONObject("items");
                    JSONArray jsonArray=object.getJSONArray("item");

                    for (int i=0;i<jsonArray.length();i++){
                        object=jsonArray.getJSONObject(i);

                        String name=object.getString("name");
                        String code=object.getString("code");

                        choiceCityItems.add(new ChoiceCityItem(name,code));
                        adapter.notifyDataSetChanged();
                    }


                } catch (JSONException e) { }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(jsonObjectRequest);
    }



    void choicesigungu(){
        String url="http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaCode?ServiceKey="+G.serviceKey+"&areaCode="+G.citycode+"&numOfRows=20&pageNo=1&MobileOS=ETC&MobileApp=AppTesting&_type=json";

        choiceCityItems2.clear();
        adapter2.notifyDataSetChanged();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject object = response.getJSONObject("response");
                        object = object.getJSONObject("body");
                        object = object.getJSONObject("items");

                        JSONArray jsonArray = object.getJSONArray("item");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            object = jsonArray.getJSONObject(i);

                            String name = object.getString("name");
                            String code = object.getString("code");

                            choiceCityItems2.add(new ChoiceCityItem(name, code));
                            adapter2.notifyDataSetChanged();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            requestQueue.add(jsonObjectRequest);



        Intent intent=getIntent();
        setResult(G.SELECT_LOCATION,intent);
    }


    public void tvallnotify() {
        tvall.setText(G.cityname+"   전체");

    }
}

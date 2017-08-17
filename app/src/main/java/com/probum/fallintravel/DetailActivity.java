package com.probum.fallintravel;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.rd.PageIndicatorView;
import com.rd.draw.data.Indicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    ArrayList<String> imgs=new ArrayList<>();
    DetailAdapter adapter;
    String contentid;
    String contenttypeid;
    int typecode;
    RequestQueue requestQueue;
    PageIndicatorView pageIndicatorView;
    String homepage;
    FloatingActionButton fab;

    TextView tvtitle,tvoverview,tvaddr;
    ViewPager viewPager;
    TextView intro1tv1,intro2tv1,intro3tv1,intro4tv1,intro5tv1,intro6tv1,intro7tv1,intro8tv1,intro9tv1;
    TextView intro1tv2,intro2tv2,intro3tv2,intro4tv2,intro5tv2,intro6tv2,intro7tv2,intro8tv2,intro9tv2;
    LinearLayout intro1,intro2,intro3,intro4,intro5,intro6,intro7,intro8,intro9;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        requestQueue= Volley.newRequestQueue(this);
        tvtitle=(TextView)findViewById(R.id.tv_title);
        tvoverview=(TextView)findViewById(R.id.tv_overview);
        viewPager=(ViewPager)findViewById(R.id.viewPager);
        tvaddr=(TextView)findViewById(R.id.tv_addr);
        pageIndicatorView = (PageIndicatorView)findViewById(R.id.indicator);
        fab=(FloatingActionButton)findViewById(R.id.fab);

        intro1tv1=(TextView)findViewById(R.id.intro1_tv1);
        intro2tv1=(TextView)findViewById(R.id.intro2_tv1);
        intro3tv1=(TextView)findViewById(R.id.intro3_tv1);
        intro4tv1=(TextView)findViewById(R.id.intro4_tv1);
        intro5tv1=(TextView)findViewById(R.id.intro5_tv1);
        intro6tv1=(TextView)findViewById(R.id.intro6_tv1);
        intro7tv1=(TextView)findViewById(R.id.intro7_tv1);
        intro8tv1=(TextView)findViewById(R.id.intro8_tv1);
        intro9tv1=(TextView)findViewById(R.id.intro8_tv1);

        intro1tv2=(TextView)findViewById(R.id.intro1_tv2);
        intro2tv2=(TextView)findViewById(R.id.intro2_tv2);
        intro3tv2=(TextView)findViewById(R.id.intro3_tv2);
        intro4tv2=(TextView)findViewById(R.id.intro4_tv2);
        intro5tv2=(TextView)findViewById(R.id.intro5_tv2);
        intro6tv2=(TextView)findViewById(R.id.intro6_tv2);
        intro7tv2=(TextView)findViewById(R.id.intro7_tv2);
        intro8tv2=(TextView)findViewById(R.id.intro8_tv2);
        intro9tv2=(TextView)findViewById(R.id.intro8_tv2);

        intro1=(LinearLayout)findViewById(R.id.intro1);
        intro2=(LinearLayout)findViewById(R.id.intro2);
        intro3=(LinearLayout)findViewById(R.id.intro3);
        intro4=(LinearLayout)findViewById(R.id.intro4);
        intro5=(LinearLayout)findViewById(R.id.intro5);
        intro6=(LinearLayout)findViewById(R.id.intro6);
        intro7=(LinearLayout)findViewById(R.id.intro7);
        intro8=(LinearLayout)findViewById(R.id.intro8);
        intro9=(LinearLayout)findViewById(R.id.intro9);

        Glide.with(this).load(R.mipmap.closemy).into(fab);

        Intent intent=getIntent();
        contentid=intent.getStringExtra("contentid");
        contenttypeid=intent.getStringExtra("contenttypeid");

        intro9.setVisibility(View.GONE);

        readCommon();
        readIntro();
        readInfo();
        readImage();

        adapter=new DetailAdapter(imgs,getLayoutInflater(),this);


    }



    public void clickFab(View v){
        finish();
    }
    public void clickHttp(View v){
//        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(homepage)));
    }


    void readCommon(){
        String url="http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailCommon?ServiceKey="+G.serviceKey+"&contentTypeId=&contentId="+contentid+"&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&defaultYN=Y&firstImageYN=Y&areacodeYN=Y&catcodeYN=Y&addrinfoYN=Y&mapinfoYN=Y&overviewYN=Y&transGuideYN=Y&_type=json";

        Log.i("url",url);

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject object=response.getJSONObject("response");
                    object=object.getJSONObject("body");
                    object=object.getJSONObject("items");
                    object=object.getJSONObject("item");

                    tvtitle.setText(object.getString("title"));
                    tvaddr.setText("주소 : "+object.getString("addr1")+" "+object.getString("addr2"));
                    tvoverview.setText(object.getString("overview"));



//                    if (object.has("homepage")) {
//                        String home = object.getString("homepage");
//                        int last = home.indexOf(">");
//                        String home2=home.substring(last,home.length());
//                        int r = home2.lastIndexOf(".");
//
//
//
//                        homepage = home.substring(last + 1, r + 3);
//                        Toast.makeText(DetailActivity.this, ""+last + "    ,"+r, Toast.LENGTH_SHORT).show();
//                        intro8tv2.setText("   " + homepage);
//                    }


                    if (object.has("firstimage"))imgs.add(object.getString("firstimage"));
                    if (object.has("firstimage2"))imgs.add(object.getString("firstimage2"));

                    pageIndicatorView.setCount(imgs.size());

                    viewPager.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",error.toString());
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
    void readIntro(){
        String url="http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailIntro?ServiceKey="+G.serviceKey+"&contentTypeId="+contenttypeid+"&contentId="+contentid+"&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&introYN=Y&_type=json";

        Log.i("url intro",url);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject object=response.getJSONObject("response");
                    object=object.getJSONObject("body");
                    object=object.getJSONObject("items");
                    object=object.getJSONObject("item");

                    if (contenttypeid.equals(G.festival)){
                        intro1tv2.setText(object.getString("sponsor1"));
                        intro2tv2.setText(object.getString("sponsor1tel"));
                        intro3tv2.setText(object.getString("eventstartdate")+"  ~  "+object.getString("eventenddate"));
                        intro4tv2.setText(object.getString("playtime"));
                        intro5tv2.setText(object.getString("eventplace"));
                       if (object.has("agelimit"))intro6tv2.setText(object.getString("agelimit"));else intro6.setVisibility(View.GONE);
                       if (object.has("usetimefestival"))intro7tv2.setText(object.getString("usetimefestival")); else intro7.setVisibility(View.GONE);
                    }

                    if (contenttypeid.equals(G.TOUR) || contenttypeid.equals("14")){
                        if (object.has("expagerange")){intro1tv1.setText("체험 가능 연령 : "); intro1tv2.setText(object.getString("expagerange"));} else intro1.setVisibility(View.GONE);
                        if (object.has("expguide") && !object.getString("expguide").equals("")){intro2tv1.setText("체험 안내 : "); intro1tv2.setText(object.getString("expguide"));}else intro2.setVisibility(View.GONE);
                        if (object.has("usetime")){intro3tv1.setText("이용 시간 : "); intro3tv2.setText(object.getString("usetime"));}else intro3.setVisibility(View.GONE);
                        if (object.has("restdate")){intro4tv1.setText("쉬는날 : "); intro4tv2.setText(object.getString("restdate"));}else intro4.setVisibility(View.GONE);
                        if (object.has("infocenter")){intro5tv1.setText("문의 및 안내 : "); intro5tv2.setText(object.getString("infocenter"));}else intro5.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",error.toString());
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
    void readInfo(){
        String url="http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailInfo?ServiceKey="+G.serviceKey+"&contentTypeId="+contenttypeid+"&contentId="+contentid+"&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&listYN=Y&_type=json";
        Log.i("url Info  Info",url);

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject object=response.getJSONObject("response");
                    object=object.getJSONObject("body");
                    object=object.getJSONObject("items");

                    try {
                        JSONObject objectitem=object.getJSONObject("item");
                        if (contenttypeid.equals(G.festival)){
                            intro8tv2.setText(objectitem.getString("infotext"));

                        }


                    }catch (Exception e){
                        JSONArray array=object.getJSONArray("item");
                        for (int i=0;i<array.length();i++){
                            object=array.getJSONObject(i);
                            if (object.getString("infoname").equals("행사내용")){intro8tv2.setText(object.getString("infotext"));}
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",error.toString());
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    void readImage(){
        String url="http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailImage?ServiceKey="+G.serviceKey+"&contentTypeId=&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&contentId="+contentid+"&imageYN=Y&_type=json";
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

                        if (obj.has("originimgurl"))imgs.add(obj.getString("originimgurl"));

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
}

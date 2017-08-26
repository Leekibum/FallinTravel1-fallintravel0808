package com.probum.fallintravel;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
    ArrayList<String> subname=new ArrayList<>();
    ArrayList<String> subdetailoverview=new ArrayList<>();
    TextView[] tv1s=new TextView[9];
    TextView[] tv2s=new TextView[9];
    DetailAdapter adapter;
    String contentid;
    String contenttypeid;
    int subnum;
    RequestQueue requestQueue;
    PageIndicatorView pageIndicatorView;
    NestedScrollView nested;
    ImageView imgfavorite,imgclose;
    TextView tvtitle,tvoverview,tvaddr,tvovername;
    ViewPager viewPager;
    TextView intro1tv1,intro2tv1,intro3tv1,intro4tv1,intro5tv1,intro6tv1,intro7tv1,intro8tv1,intro9tv1;
    TextView intro1tv2,intro2tv2,intro3tv2,intro4tv2,intro5tv2,intro6tv2,intro7tv2,intro8tv2,intro9tv2;
    LinearLayout intro1,intro2,intro3,intro4,intro5,intro6,intro7,intro8,intro9,firstlinear;
    ImageView imgtitle;
    String KorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        requestQueue= Volley.newRequestQueue(this);

        tv1s= new TextView[]{intro1tv1,intro2tv1,intro3tv1,intro4tv1,intro5tv1,intro6tv1,intro7tv1,intro8tv1,intro9tv1};
        tv2s= new TextView[]{intro1tv2,intro2tv2,intro3tv2,intro4tv2,intro5tv2,intro6tv2,intro7tv2,intro8tv2,intro9tv2};
        findIds();
        KorService="KorService";


        Intent intent=getIntent();
        contentid=intent.getStringExtra("contentid");
        contenttypeid=intent.getStringExtra("contenttypeid");
        String firstimage=intent.getStringExtra("firstimage");
        imgs.add(firstimage);

        readCommon();
        readIntro();
        readInfo();
        readImage();


        adapter=new DetailAdapter(imgs,getLayoutInflater(),this);
        viewPager.setAdapter(adapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            viewPager.setTransitionName("IMG");
        }
        subname.add("개요");

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (contenttypeid.equals(G.course)){
                    if (position==0){
                        tvovername.setText(subname.get(position));tvaddr.setText("넘기시면 추천코스가 나옵니다.");
                        tvoverview.setText(subdetailoverview.get(position));
                    }

                    subnum=position;

                   if (position>=1&&position<subname.size()) {
                       tvaddr.setText("");
                       tvovername.setText("제" + (position) + "코스 : " + subname.get(position));
                       tvoverview.setText(subdetailoverview.get(position));
                   }


                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i=0;i<tv1s.length;i++){
                    tvtitle.invalidate();
                    tvaddr.invalidate();
                    tvoverview.invalidate();
                    tvovername.invalidate();
                    tv1s[i].invalidate();
                    tv2s[i].invalidate();
                }
            }
        },400);
    }//onCreate

    void clickFavorite(View v){

    }

    public void clickFab(View v){
        onBackPressed();
    }

    public void clickHttp(View v){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(tv2s[8].getText().toString())));
    }


    void readCommon(){
        String url="http://api.visitkorea.or.kr/openapi/service/rest/"+KorService+"/detailCommon?ServiceKey="+G.serviceKey+"&contentTypeId="+contenttypeid+"&contentId="+contentid+"&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&defaultYN=Y&firstImageYN=Y&areacodeYN=Y&catcodeYN=Y&addrinfoYN=Y&mapinfoYN=Y&overviewYN=Y&transGuideYN=Y&_type=json";

        Log.i("urlCommon : ",url);

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject object=response.getJSONObject("response");
                    object=object.getJSONObject("body");
                    object=object.getJSONObject("items");
                    object=object.getJSONObject("item");

                    subdetailoverview.add(changeText(object.getString("overview")));

                    if (object.has("overview"))tvoverview.setText(changeText(object.getString("overview")));

                    if (object.has("addr1") &&!contenttypeid.equals(G.course) &&object.has("addr2")){tvaddr.setText("주소  "+object.getString("addr1")+" "+object.getString("addr2"));}
                    else if (object.has("addr1") &&!contenttypeid.equals(G.course))tvaddr.setText("주소  "+object.getString("addr1"));
                    else tvaddr.setVisibility(View.GONE);
                    if (object.has("telname"))tv2s[0].setText(object.getString("telname"));else intro1.setVisibility(View.GONE);
                    if (object.has("tel"))tv2s[1].setText(object.getString("tel"));else intro2.setVisibility(View.GONE);

                    if (object.has("title"))tvtitle.setText(object.getString("title"));
                    if (object.has("homepage"))tv2s[8].setText(changeHomepage(object.getString("homepage")));else intro9.setVisibility(View.GONE);
                    if (!object.has("homepage"))intro9.setVisibility(View.GONE);
//                    if (object.has("firstimage"))imgs.add(object.getString("firstimage"));
                    if (object.has("firstimage2")&&!contenttypeid.equals(G.course))imgs.add(object.getString("firstimage2"));

                    adapter.notifyDataSetChanged();
                    pageIndicatorView.setCount(imgs.size());

                    Log.i("tvtitle : ",tvtitle.getText().toString());


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("jsonerror",e.toString());
                    Toast.makeText(DetailActivity.this, "catch!!", Toast.LENGTH_SHORT).show();
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
        String url="http://api.visitkorea.or.kr/openapi/service/rest/"+KorService+"/detailIntro?ServiceKey="+G.serviceKey+"&contentTypeId="+contenttypeid+"&contentId="+contentid+"&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&introYN=Y&_type=json";

        Log.i("url intro : ",url);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject object=response.getJSONObject("response");
                    object=object.getJSONObject("body");
                    object=object.getJSONObject("items");
                    object=object.getJSONObject("item");

                    if (contenttypeid.equals(G.festival)){
                        if (object.has("sponsor1") && intro1.getVisibility() ==View.GONE){tv2s[0].setText(object.getString("sponsor1")); intro1.setVisibility(View.VISIBLE);}
                        if (object.has("sponsor1tel")&&intro2.getVisibility()==View.GONE){tv2s[1].setText(object.getString("sponsor1tel")); intro2.setVisibility(View.VISIBLE);}
                        tv2s[2].setText(changeDate(object.getString("eventstartdate"))+"  ~  "+changeDate(object.getString("eventenddate")));
                        if (object.has("playtime"))tv2s[3].setText(object.getString("playtime"));else intro4.setVisibility(View.GONE);
                        if (object.has("playtime") && object.getString("playtime").equals(""))intro4.setVisibility(View.GONE);
                        if (object.has("eventplace"))tv2s[4].setText(object.getString("eventplace"));else intro5.setVisibility(View.GONE);
                       if (object.has("agelimit"))tv2s[5].setText(object.getString("agelimit"));else intro6.setVisibility(View.GONE);
                       if (object.has("usetimefestival"))tv2s[6].setText(changeText(object.getString("usetimefestival"))); else intro7.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    }

                    if (contenttypeid.equals(G.TOUR) || contenttypeid.equals(G.cultural)){
                        if (object.has("expagerange")){tv1s[2].setText("체험 가능 연령  "); tv2s[2].setText(object.getString("expagerange"));} else intro3.setVisibility(View.GONE);
                        if (object.has("expguide") && object.getString("expguide").length()>10){tv1s[3].setText("체험 안내  "); tv2s[3].setText(changeText(object.getString("expguide")));}else intro4.setVisibility(View.GONE);
                        if (object.has("usetime")){tv1s[4].setText("이용 시간 "); tv2s[4].setText(changeText(object.getString("usetime")));}else intro5.setVisibility(View.GONE);
                        if (object.has("restdate")){tv1s[5].setText("쉬는날 "); tv2s[5].setText(changeText(object.getString("restdate")));}else intro6.setVisibility(View.GONE);
                        if (object.has("infocenter")){tv1s[6].setText("문의 및 안내 "); tv2s[6].setText(changeText(object.getString("infocenter")));}else intro7.setVisibility(View.GONE);
                        intro8.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
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
        String url="http://api.visitkorea.or.kr/openapi/service/rest/"+KorService+"/detailInfo?ServiceKey="+G.serviceKey+"&contentTypeId="+contenttypeid+"&contentId="+contentid+"&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&listYN=Y&_type=json";
        Log.i("url detailInfo : ",url);

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject object=response.getJSONObject("response");
                    object=object.getJSONObject("body");
                    object=object.getJSONObject("items");

                    try {
                        JSONObject objectitem=object.getJSONObject("item");

                        if (objectitem.has("infoname") && objectitem.getString("infoname").equals("행사내용")){
                            tv1s[7].setText(changeText(objectitem.getString("infoname")));
                            tv2s[7].setText(changeText(objectitem.getString("infotext")));
                        }


                    }catch (Exception e){
                        JSONArray array=object.getJSONArray("item");
                        for (int i=0;i<array.length()+1;i++){
                            object=array.getJSONObject(i);

//                            if (object.has("infoname") &&object.getString("infoname").equals("행사내용")){tv2s[7].setText(changeText(object.getString("infotext")));}
//                            else if (object.has("infoname") )

                            if (contenttypeid.equals(G.course)) {
                                if (object.has("subname")) subname.add(object.getString("subname"));
                                tvaddr.setVisibility(View.VISIBLE);
                                tvaddr.setText("넘기면 추천코스가 나옵니다.");
                                if (object.has("subdetailoverview"))    subdetailoverview.add(changeText(object.getString("subdetailoverview")));
                                if (object.has("subdetailimg"))         imgs.add(object.getString("subdetailimg"));
                                else imgs.add("noimage");
                                intro1.setVisibility(View.GONE);
                                intro2.setVisibility(View.GONE);
                                intro3.setVisibility(View.GONE);
                                intro4.setVisibility(View.GONE);
                                intro5.setVisibility(View.GONE);
                                intro6.setVisibility(View.GONE);
                                intro7.setVisibility(View.GONE);
                                intro8.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                            }

                            pageIndicatorView.setCount(imgs.size());

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
//        http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailInfo?ServiceKey=인증키&contentTypeId=25&contentId=1942795&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&listYN=Y
        String url="http://api.visitkorea.or.kr/openapi/service/rest/"+KorService+"/detailImage?ServiceKey="+G.serviceKey+"&contentTypeId="+contenttypeid+"&contentId="+contentid+"&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&imageYN=Y&_type=json";
        Log.i("url Image",url);

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

                        pageIndicatorView.setCount(imgs.size());

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

    String changeText(String text){

        text= text.replace("<br />","");
        text=text.replace("&nbsp;"," ");
        text= text.replace("<br>","");
        text=text.replace("<br/>","");
        text=text.replace("<strong>","");
        text=text.replace("</strong>","");
        text=text.replace("&gt;","");
//        text=text.replace(text.substring(text.indexOf("<a"),text.indexOf("</a>")),"");
        if (text.contains("<a")) {
            text = text.replace(text.substring(text.lastIndexOf("<a"),text.lastIndexOf("</a>")+4),"");
        }
        return text;
    }

    String changeHomepage(String homepage){

        int a=homepage.indexOf("http");
        homepage=homepage.substring(a);
        int b=homepage.indexOf("\"");
        if (b!=-1)homepage=homepage.substring(0,b);

        return homepage;
    }

    String changeDate(String date){

        String YY=date.substring(2,4);
        String MM=date.substring(4,6);
        String DD=date.substring(6,8);
        date=YY+"년 "+MM+"월 "+DD+"일";
        return date;
    }

    void findIds(){

        tvtitle=(TextView)findViewById(R.id.tv_title);
        tvoverview=(TextView)findViewById(R.id.tv_overview);
        viewPager=(ViewPager)findViewById(R.id.viewPager);
        tvaddr=(TextView)findViewById(R.id.tv_addr);
        pageIndicatorView = (PageIndicatorView)findViewById(R.id.indicator);
        nested=(NestedScrollView)findViewById(R.id.nested);
        tvovername=(TextView)findViewById(R.id.tv_overviewname);
        imgclose=(ImageView)findViewById(R.id.img_close);
        imgfavorite=(ImageView)findViewById(R.id.img_favorite);
        firstlinear=(LinearLayout)findViewById(R.id.detail_firstlinear);
        imgtitle=(ImageView)findViewById(R.id.img_title);

        tv1s[0]=(TextView)findViewById(R.id.intro1_tv1);
        tv1s[1]=(TextView)findViewById(R.id.intro2_tv1);
        tv1s[2]=(TextView)findViewById(R.id.intro3_tv1);
        tv1s[3]=(TextView)findViewById(R.id.intro4_tv1);
        tv1s[4]=(TextView)findViewById(R.id.intro5_tv1);
        tv1s[5]=(TextView)findViewById(R.id.intro6_tv1);
        tv1s[6]=(TextView)findViewById(R.id.intro7_tv1);
        tv1s[7]=(TextView)findViewById(R.id.intro8_tv1);
        tv1s[8]=(TextView)findViewById(R.id.intro9_tv1);

        tv2s[0]=(TextView)findViewById(R.id.intro1_tv2);
        tv2s[1]=(TextView)findViewById(R.id.intro2_tv2);
        tv2s[2]=(TextView)findViewById(R.id.intro3_tv2);
        tv2s[3]=(TextView)findViewById(R.id.intro4_tv2);
        tv2s[4]=(TextView)findViewById(R.id.intro5_tv2);
        tv2s[5]=(TextView)findViewById(R.id.intro6_tv2);
        tv2s[6]=(TextView)findViewById(R.id.intro7_tv2);
        tv2s[7]=(TextView)findViewById(R.id.intro8_tv2);
        tv2s[8]=(TextView)findViewById(R.id.intro9_tv2);

        intro1=(LinearLayout)findViewById(R.id.intro1);
        intro2=(LinearLayout)findViewById(R.id.intro2);
        intro3=(LinearLayout)findViewById(R.id.intro3);
        intro4=(LinearLayout)findViewById(R.id.intro4);
        intro5=(LinearLayout)findViewById(R.id.intro5);
        intro6=(LinearLayout)findViewById(R.id.intro6);
        intro7=(LinearLayout)findViewById(R.id.intro7);
        intro8=(LinearLayout)findViewById(R.id.intro8);
        intro9=(LinearLayout)findViewById(R.id.intro9);

        Glide.with(this).load(R.drawable.intitlesub).into(imgtitle);
        Glide.with(this).load(R.mipmap.closefinal).into(imgclose);
        Glide.with(this).load(R.drawable.ic_favorite_border_black_24dp).into(imgfavorite);
    }

    public void clickTel(View v){
        String tel="tel:"+tv2s[1].getText().toString();
        startActivity(new Intent("android.intent.action.DIAL",Uri.parse(tel)));
    }
}

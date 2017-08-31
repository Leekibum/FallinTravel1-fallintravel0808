package com.probum.fallintravel;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.rd.PageIndicatorView;
import com.rd.draw.data.Indicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailActivity extends AppCompatActivity {

    ArrayList<String> imgs=new ArrayList<>();
    ArrayList<String> subname=new ArrayList<>();
    ArrayList<String> subdetailoverview=new ArrayList<>();
    ArrayList<ReplyItem> replyItems=new ArrayList<>();

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
    TextView intro1tv1,intro2tv1,intro3tv1,intro4tv1,intro5tv1,intro6tv1,intro7tv1,intro8tv1,intro9tv1,intro10tv1,introhometv1;
    TextView intro1tv2,intro2tv2,intro3tv2,intro4tv2,intro5tv2,intro6tv2,intro7tv2,intro8tv2,intro9tv2,intro10tv2,introhometv2;
    LinearLayout intro0,intro1,intro2,intro3,intro4,intro5,intro6,intro7,intro8,intro9,firstlinear,introhome;
    ImageView imgtitle;
    String KorService;
    TextView[] tv1s=new TextView[]{intro1tv1,intro2tv1,intro3tv1,intro4tv1,intro5tv1,intro6tv1,intro7tv1,intro8tv1,intro9tv1,intro10tv1};
    TextView[] tv2s=new TextView[]{intro1tv2,intro2tv2,intro3tv2,intro4tv2,intro5tv2,intro6tv2,intro7tv2,intro8tv2,intro9tv2,intro10tv2};
    LinearLayout[] linears=new LinearLayout[]{};
    EditText editreply;
    RecyclerView recyclerreply;
    ReplyAdapter replyAdapter;
    String typeid,content,id,profileimage,reply;
    InputMethodManager imm;

    String insertUrl="http://probum.dothome.co.kr/fallintravel/saveReply.php";
    String loadUrl="http://probum.dothome.co.kr/fallintravel/loadReply.php";
    String deleteUrl="http://probum.dothome.co.kr/fallintravel/deleteReply.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        requestQueue= Volley.newRequestQueue(this);

        findIds();
        linears=new LinearLayout[]{intro0,intro1,intro2,intro3,intro4,intro5,intro6,intro7,intro8,intro9};
        KorService="KorService";


        Intent intent=getIntent();
        contentid=intent.getStringExtra("contentid");
        contenttypeid=intent.getStringExtra("contenttypeid");
        String firstimage=intent.getStringExtra("firstimage");

        if (!G.isLogin)editreply.setFocusableInTouchMode(false);
        editreply.setOnTouchListener(onTouchListener);
        if (G.isLogin)editreply.setHint("댓글을 입력하세요");else if (!G.isLogin)editreply.setHint("댓글을 입력하려면 로그인을 해주세요.");

        readCommon();
        readIntro();
        readInfo();
        readImage();

        adapter=new DetailAdapter(imgs,getLayoutInflater(),this);
        viewPager.setAdapter(adapter);

        recyclerreply.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true));
        replyAdapter=new ReplyAdapter(replyItems,this);
        recyclerreply.setAdapter(replyAdapter);

        //이미지 애니메이션
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            viewPager.setTransitionName("IMG");
        }
        imgs.add(firstimage);
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
        loadReply();


        delaytime(450);
        delaytime(1000);
    }//onCreate

    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (G.isLogin){editreply.setFocusableInTouchMode(true);editreply.setHint("댓글을 입력하세요");}
        else if (!G.isLogin)editreply.setHint("댓글을 입력하려면 로그인을 해주세요.");
    }

    void dialog(){
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(this);
        alert_confirm.setMessage("로그인이 필요합니다. 로그인을 하시겠습니까 ?").setCancelable(false).setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(DetailActivity.this,LoginoutActivity.class));
                        // 'YES'
                    }
                }).setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 'No'
                        return;
                    }
                });
        AlertDialog alert = alert_confirm.create();
        alert.show();

    }

    public View.OnTouchListener onTouchListener=new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action=event.getAction();

            switch (action){
                case MotionEvent.ACTION_DOWN:
                    if (!G.isLogin){dialog();}
                    else if (G.isLogin && isReply()){
                            editreply.setFocusableInTouchMode(false);
                        Toast.makeText(DetailActivity.this, "이미 댓글을 입력하셨습니다.", Toast.LENGTH_SHORT).show();
                    }
                    break;

            }
            //false== 이 리스너 이후에도 다른 리스너들이 작동하도록
            return false;
        }
    };

    void delaytime(int time){
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
                    replyAdapter.notifyDataSetChanged();
                }
            }
        },time);
    }

    void clickFavorite(View v){

    }

    public void clickFab(View v){
        onBackPressed();
    }

    public void clickHttp(View v){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(introhometv2.getText().toString())));
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
                    if (object.has("telname"))tv2s[0].setText(object.getString("telname"));else intro0.setVisibility(View.GONE);
                    if (object.has("tel"))tv2s[1].setText(changeTel(object.getString("tel")));else intro1.setVisibility(View.GONE);

                    if (object.has("title"))tvtitle.setText(object.getString("title"));
                    if (object.has("homepage"))introhometv2.setText(changeHomepage(object.getString("homepage")));else introhome.setVisibility(View.GONE);
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
//                        if (object.has("sponsor1") && intro1.getVisibility() ==View.GONE){tv2s[0].setText(object.getString("sponsor1")); intro1.setVisibility(View.VISIBLE);}
//                        if (object.has("sponsor1tel")&&intro2.getVisibility()==View.GONE){tv2s[1].setText(object.getString("sponsor1tel")); intro2.setVisibility(View.VISIBLE);}
                        tv2s[2].setText(changeDate(object.getString("eventstartdate"))+"  ~  "+changeDate(object.getString("eventenddate")));
                        if (object.has("playtime"))tv2s[3].setText(object.getString("playtime"));else intro3.setVisibility(View.GONE);
                        if (object.has("playtime") && object.getString("playtime").equals(""))intro3.setVisibility(View.GONE);
                        if (object.has("eventplace"))tv2s[4].setText(object.getString("eventplace"));else intro4.setVisibility(View.GONE);
                        if (object.has("agelimit"))tv2s[5].setText(object.getString("agelimit"));else intro5.setVisibility(View.GONE);
                        if (object.has("usetimefestival"))tv2s[6].setText(changeText(object.getString("usetimefestival"))); else intro6.setVisibility(View.GONE);
                        intro8.setVisibility(View.GONE);
                        intro9.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }

                    if (contenttypeid.equals(G.TOUR) || contenttypeid.equals(G.cultural)){
                        if (object.has("expagerange")){tv1s[2].setText("체험 가능 연령  "); tv2s[2].setText(object.getString("expagerange"));} else intro2.setVisibility(View.GONE);
                        if (object.has("expguide") && object.getString("expguide").length()>10){tv1s[3].setText("체험 안내  "); tv2s[3].setText(changeText(object.getString("expguide")));}else intro3.setVisibility(View.GONE);
                        if (object.has("usetime")){tv1s[4].setText("이용 시간 "); tv2s[4].setText(changeText(object.getString("usetime")));}else intro4.setVisibility(View.GONE);
                        if (object.has("restdate")){tv1s[5].setText("쉬는날 "); tv2s[5].setText(changeText(object.getString("restdate")));}else intro5.setVisibility(View.GONE);
                        if (object.has("infocenter")){tv1s[6].setText("문의 및 안내 "); tv2s[6].setText(changeText(object.getString("infocenter")));}else intro6.setVisibility(View.GONE);
                        intro7.setVisibility(View.GONE);
                        intro8.setVisibility(View.GONE);
                        intro9.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }

                    if (contenttypeid.equals(G.lports)){
                        if (object.has("scaleleports")){tv1s[2].setText("규모");tv2s[2].setText(object.getString("scaleleports"));} else intro2.setVisibility(View.GONE);
                        if (object.has("usetimeleports")){tv1s[3].setText("이용시간");tv2s[3].setText(changeText(object.getString("usetimeleports")));}else intro3.setVisibility(View.GONE);
                        if (object.has("openperiod")){tv1s[4].setText("개장기간");tv2s[4].setText(object.getString("openperiod"));}else intro4.setVisibility(View.GONE);
                        if (object.has("restdateleports")){tv1s[5].setText("쉬는날");tv2s[5].setText(object.getString("restdateleports"));}else intro5.setVisibility(View.GONE);
                        if (object.has("usefeeleports")){tv1s[6].setText("입장료");tv2s[6].setText(object.getString("usefeeleports"));}else intro6.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                        intro7.setVisibility(View.GONE);
                        intro8.setVisibility(View.GONE);
                        intro9.setVisibility(View.GONE);
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

                            if (contenttypeid.equals(G.festival)) {
                                if (object.has("infoname") && object.getString("infoname").equals("행사내용")) {
                                    tv2s[7].setText(changeText(object.getString("infotext")));
                                } else if (object.has("infoname") && object.getString("infoname").equals("행사소개")) {
                                    tv1s[7].setText("행사소개");
                                    tv2s[7].setText(changeText(object.getString("infotext")));
                                } else intro7.setVisibility(View.GONE);
                            }

                            if (contenttypeid.equals(G.lports)){
                                if (i>2)return;
                                if (object.has("fldgubun"))linears[7+i].setVisibility(View.VISIBLE);
                                if (object.has("infoname")) {tv1s[7+i].setText(changeText(object.getString("infoname")));}
                                if (object.has("infotext")) tv2s[7+i].setText(changeText(object.getString("infotext")));
                            }

                            if (contenttypeid.equals(G.course)) {
                                if (object.has("subname")) subname.add(object.getString("subname"));
                                tvaddr.setVisibility(View.VISIBLE);
                                tvaddr.setText("넘기면 추천코스가 나옵니다.");
                                if (object.has("subdetailoverview"))    subdetailoverview.add(changeText(object.getString("subdetailoverview")));
                                if (object.has("subdetailimg"))         imgs.add(object.getString("subdetailimg"));
                                else imgs.add("noimage");
                                intro0.setVisibility(View.GONE);
                                intro1.setVisibility(View.GONE);
                                intro2.setVisibility(View.GONE);
                                intro3.setVisibility(View.GONE);
                                intro4.setVisibility(View.GONE);
                                intro5.setVisibility(View.GONE);
                                intro6.setVisibility(View.GONE);
                                intro7.setVisibility(View.GONE);
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
        editreply=(EditText)findViewById(R.id.edit_reply);
        recyclerreply=(RecyclerView)findViewById(R.id.recycler_reply);
        imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);


        tv1s[0]=(TextView)findViewById(R.id.intro1_tv1);
        tv1s[1]=(TextView)findViewById(R.id.intro2_tv1);
        tv1s[2]=(TextView)findViewById(R.id.intro3_tv1);
        tv1s[3]=(TextView)findViewById(R.id.intro4_tv1);
        tv1s[4]=(TextView)findViewById(R.id.intro5_tv1);
        tv1s[5]=(TextView)findViewById(R.id.intro6_tv1);
        tv1s[6]=(TextView)findViewById(R.id.intro7_tv1);
        tv1s[7]=(TextView)findViewById(R.id.intro8_tv1);
        tv1s[8]=(TextView)findViewById(R.id.intro9_tv1);
        tv1s[9]=(TextView)findViewById(R.id.intro10_tv1);
        introhometv1=(TextView)findViewById(R.id.introhome_tv1);

        tv2s[0]=(TextView)findViewById(R.id.intro1_tv2);
        tv2s[1]=(TextView)findViewById(R.id.intro2_tv2);
        tv2s[2]=(TextView)findViewById(R.id.intro3_tv2);
        tv2s[3]=(TextView)findViewById(R.id.intro4_tv2);
        tv2s[4]=(TextView)findViewById(R.id.intro5_tv2);
        tv2s[5]=(TextView)findViewById(R.id.intro6_tv2);
        tv2s[6]=(TextView)findViewById(R.id.intro7_tv2);
        tv2s[7]=(TextView)findViewById(R.id.intro8_tv2);
        tv2s[8]=(TextView)findViewById(R.id.intro9_tv2);
        tv2s[9]=(TextView)findViewById(R.id.intro10_tv2);
        introhometv2=(TextView)findViewById(R.id.introhome_tv2);

        intro0=(LinearLayout)findViewById(R.id.intro1);
        intro1=(LinearLayout)findViewById(R.id.intro2);
        intro2=(LinearLayout)findViewById(R.id.intro3);
        intro3=(LinearLayout)findViewById(R.id.intro4);
        intro4=(LinearLayout)findViewById(R.id.intro5);
        intro5=(LinearLayout)findViewById(R.id.intro6);
        intro6=(LinearLayout)findViewById(R.id.intro7);
        intro7=(LinearLayout)findViewById(R.id.intro8);
        intro8=(LinearLayout)findViewById(R.id.intro9);
        intro9=(LinearLayout)findViewById(R.id.intro10);
        introhome=(LinearLayout)findViewById(R.id.introhome);

        Glide.with(this).load(R.drawable.intitlesub).into(imgtitle);
        Glide.with(this).load(R.mipmap.closefinal).into(imgclose);
        Glide.with(this).load(R.drawable.ic_favorite_border_black_24dp).into(imgfavorite);
    }

    void loadReply(){
        new Thread(){
            @Override
            public void run() {

                String typeid="";
                typeid=selectContenttypeid(typeid);

                try {
                    typeid= URLEncoder.encode(typeid,"utf-8");
                    URL url=new URL(loadUrl);
                    HttpURLConnection conn=(HttpURLConnection)url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);

                    String data= "contenttypeid="+typeid;

                    OutputStream os= conn.getOutputStream();
                    os.write(data.getBytes());
                    os.flush();
                    os.close();

                    InputStream is=conn.getInputStream();
                    InputStreamReader isr=new InputStreamReader(is);
                    BufferedReader reader=new BufferedReader(isr);

                    StringBuffer buffer=new StringBuffer();
                    String line=reader.readLine();

                    while (line!=null){
                        buffer.append(line);
                        line=reader.readLine();
                    }

                    String str=buffer.toString();

                    String[] rows= str.split(";");

                    //읽어온 DB의 데이터와 내가 화면에 이미 보여주고 있는 아이템의 개수가 같다면 이미 다 불러들인적이 있는 상황
//                    if (rows.length== replyItems.size()) return;

                    replyItems.clear();
                    for (String row: rows){
                        String[] datas=row.split("&");

                        Log.e("sdgsdg","zzname");
                        Log.e("sdgsdg",datas.length+"");

                        if (datas.length<4) continue;

                        String content=datas[0];
                        String id=datas[1];
                        String profileimage=datas[2];
                        String reply=datas[3];
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                replyAdapter.notifyDataSetChanged();
                                if (contentid.equals("content"))  editreply.setFocusable(false);
                            }
                        });
                        if (contentid.equals(content)){
                            editreply.setFocusableInTouchMode(false);
                            replyItems.add(new ReplyItem(content,id,profileimage,reply));
                        }

                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        replyAdapter.notifyDataSetChanged();
        delaytime(400);
    }

    String selectContenttypeid(String typeid){
        if (contenttypeid.equals("12")) typeid="tour";
        if (contenttypeid.equals("14")) typeid="cultural";
        if (contenttypeid.equals("15")) typeid="festival";
        if (contenttypeid.equals("25")) typeid="course";
        if (contenttypeid.equals("28")) typeid="lports";
        if (contenttypeid.equals("32")) typeid="shopping";
        if (contenttypeid.equals("38")) typeid="eatery";
        if (contenttypeid.equals("39")) typeid="arrange";

        return typeid;
    }

    public void clickSaveReply(View v){
        if (isReply()) {
            Toast.makeText(this, "이미 댓글을 입력하셨습니다.", Toast.LENGTH_SHORT).show();
        }
        else if (editreply.length()<4) Toast.makeText(this, "4글자 이상 입력해주세요", Toast.LENGTH_SHORT).show();
        else if (editreply.length()>=4 &&isReply()==false){
            typeid=selectContenttypeid(typeid);
            content=contentid;
            id=G.id;
            profileimage=G.profile_image;
            reply=editreply.getText().toString();
            editreply.setText("");
            imm.hideSoftInputFromWindow(editreply.getWindowToken(),0);

            new Thread(){
            @Override
            public void run() {


                try {
                    typeid= URLEncoder.encode(typeid,"utf-8");
                    content= URLEncoder.encode(content,"utf-8");
                    id= URLEncoder.encode(id,"utf-8");
                    profileimage= URLEncoder.encode(profileimage,"utf-8");
                    reply= URLEncoder.encode(reply,"utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                try {
                    URL url= new URL(insertUrl);

                    HttpURLConnection conn= (HttpURLConnection)url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);

                    String data= "contenttypeid="+typeid+"&content="+content+"&id="+id+"&profileimage="+profileimage+"&reply="+reply;

                    OutputStream os= conn.getOutputStream();
                    os.write(data.getBytes());
                    os.flush();
                    os.close();

                    //서버로부터 오는 echo값 읽어오기
                    InputStream is=conn.getInputStream();
                    InputStreamReader isr=new InputStreamReader(is);
                    BufferedReader reader=new BufferedReader(isr);

                    final StringBuffer buffer=new StringBuffer();
                    String line=reader.readLine();

                    while (line!=null){
                        buffer.append(line);
                        line=reader.readLine();
                    }


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            editreply.setFocusable(false);
                            editreply.setFocusableInTouchMode(false);
                            Toast.makeText(DetailActivity.this, "댓글을 달았습니다.", Toast.LENGTH_SHORT).show();
                            editreply.setText("");
                            loadReply();
                        }
                    });


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            }.start();
        }
    }

    public void clickReplyDelte(View v) {
        replyDelete();
    }

    void replyDelete(){

        typeid = selectContenttypeid(typeid);
        content = contentid;
        id = G.id;
        Log.i("zxcvasdf", typeid + "," + content + "," + id);

        StringRequest stringRequest= new StringRequest(Request.Method.POST, deleteUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Log.i("aaaa",response);
                if (response.equals("success")){
                    loadReply();
                    editreply.setFocusable(true);
                    editreply.setFocusableInTouchMode(true);
                    Toast.makeText(DetailActivity.this, "댓글을 지웠습니다.", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params= new HashMap<>();
                params.put("contenttypeid", typeid);
                params.put("content",content);
                params.put("id",id);

                return params;
            }

        };

        requestQueue.add(stringRequest);

    }

    public void clickTel(View v){
        String tel="tel:"+tv2s[1].getText().toString();
        startActivity(new Intent("android.intent.action.DIAL",Uri.parse(tel)));
    }

    boolean isReply(){
        for (int i=0;i<replyItems.size();i++){
            if (replyItems.get(i).getId().equals(G.id)) return true;
        }


        return false;
    }

    void saveData(){
        SharedPreferences pref=getSharedPreferences("data",MODE_PRIVATE);//xml파일
        SharedPreferences.Editor editor =pref.edit();
        editor.putString("cityname",G.cityname);
        editor.putString("citycode",G.citycode);
        editor.putString("sigunguName",G.sigunguName);
        editor.putString("sigunguCode",G.sigunguCode);
        editor.putBoolean("isLogin",G.isLogin);
        editor.putBoolean("isFirst",G.isFirst);
        editor.putString("nickname",G.nickname);
        editor.putString("profile_image",G.profile_image);
        editor.putString("arrange",G.arrange);
        editor.putString("id",G.id);
        editor.commit();

    }

    String changeText(String text){

        text= text.replace("<br />","");
        text=text.replace("&nbsp;"," ");
        text= text.replace("<br>","");
        text=text.replace("<br/>","");
        text=text.replace("<strong>","");
        text=text.replace("</strong>","");
        text=text.replace("&gt;","");
        text=text.replace("&lt;","  ");
//        text=text.replace(text.substring(text.indexOf("<a"),text.indexOf("</a>")),"");
        if (text.contains("<a")) {
            text = text.replace(text.substring(text.lastIndexOf("<a"),text.lastIndexOf("</a>")+4),"");
        }
        return text;
    }

    String changeHomepage(String homepage){

        int a=homepage.indexOf("http");
        if (a!=-1)homepage=homepage.substring(a);
        else if (a==-1)homepage="http://"+homepage;
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

    String changeTel(String tel){

        if (tel.contains(",")){
            tel=tel.substring(0,tel.indexOf(","));
        }

        return tel;
    }
}

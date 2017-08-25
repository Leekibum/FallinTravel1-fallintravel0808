package com.probum.fallintravel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdditionActivity extends AppCompatActivity {

    ArrayList<Item> items=new ArrayList<>();
    RecyclerView recyclerView;
    RequestQueue requestQueue;
    TourAdapter adapter;
    int pageNo;
    String url;
    Intent intent;
    SearchView searchView;
    Context context;
    String keyword;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    Spinner spin;
    TextView cityname,title,navname,navlogin;
    String citycode="",sigunguCode="";
    NavigationView navi;
    boolean isnavislide=false;
    CircleImageView imgcircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addition);
        context=this;
        pageNo=1;
        intent=getIntent();
        findIds();
        requestQueue= Volley.newRequestQueue(this);
        cityname.setText("전국 ");

        changenaviitem();
        changenaveLogin();

        RecyclerView.LayoutManager manager= new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager);
        adapter=new TourAdapter(items,this,this);
        recyclerView.setAdapter(adapter);

        setSupportActionBar(toolbar);
        ActionBar actionBar= getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        drawerToggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.action_settings,R.string.action_settings);

        spinnercustom();

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position==0) G.arrange="A";
                else if (position==1)G.arrange="B";
                else if (position==2)G.arrange="C";
                else if (position==4)G.arrange="D";
                recyclerItemChange();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //네비게이션 드로어어
       drawerToggle.syncState();
        drawerLayout.addDrawerListener(drawerToggle);
        readurl();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItemPostion=((GridLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount= recyclerView.getAdapter().getItemCount()-1;
                if (lastVisibleItemPostion==itemTotalCount){
                    pageNo+=1;
                    readurl();
                }
            }
        });
    }//onCreate

    void selecturl(){
        String type=intent.getStringExtra("what");
        String KorService="KorService";
        if (type.equals("searchKeyword")){
            url="http://api.visitkorea.or.kr/openapi/service/rest/"+KorService+"/"+type+"?ServiceKey="+G.serviceKey+"&keyword="+intent.getStringExtra("keyword")+"&areaCode="+citycode+"&sigunguCode="+sigunguCode+"&cat1=&cat2=&cat3=&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange="+G.arrange+"&numOfRows=12&pageNo="+pageNo+"&_type=json";
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case G.SELECT_LOCATION:
                citycode=G.citycode;
                sigunguCode=G.sigunguCode;
                cityname.setText(G.cityname + " " +G.sigunguName);
                saveData();
                recyclerItemChange();
                break;
            case G.LOGIN:
                changenaviitem();
                changenaveLogin();
                break;
            case G.LOGOUT:
                changenaviitem();
                changenaveLogin();
                break;

        }
    }

    private void recyclerItemChange() {
        items.clear();
        pageNo=1;
        readurl();
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
        editor.commit();

    }


    public void clickChoiceCity(View v){
        startActivityForResult(new Intent(this,ChoiceCityActivity.class),22);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        //searchview
        final MenuItem item=menu.findItem(R.id.menu_search);
        searchView=(SearchView)item.getActionView();
        searchView.setIconifiedByDefault(true);

        MenuItem heart=menu.findItem(R.id.menu_heart);
        heart.setVisible(false);
        MenuItem close=menu.findItem(R.id.menu_close);
        close.setVisible(true);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length()<2) Toast.makeText(context, "두글자이상 키워드를 입력해주세요!", Toast.LENGTH_SHORT).show();
                else searchgood(query);
                searchView.setIconified(true);
                item.collapseActionView();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id){
            case R.id.menu_close:
                finish();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    void searchgood(String query){
        Intent intent=new Intent(this,AdditionActivity.class);
        try {
            keyword= URLEncoder.encode(query,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url="http://api.visitkorea.or.kr/openapi/service/rest/KorService/searchKeyword?ServiceKey="+G.serviceKey+"&keyword="+keyword+"&areaCode=&sigunguCode=&cat1=&cat2=&cat3=&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=A&numOfRows=12&pageNo=";
        intent.putExtra("url",url);
        intent.putExtra("what","searchKeyword");
        intent.putExtra("query",query);
        intent.putExtra("keyword",keyword);
        startActivity(intent);
        finish();
    }

    void findIds(){
        ImageView imgtitle=(ImageView)findViewById(R.id.img_title);
        recyclerView=(RecyclerView)findViewById(R.id.layout_recycler);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        drawerLayout=(DrawerLayout)findViewById(R.id.layout_drawer);
        spin=(Spinner)findViewById(R.id.spin);
        cityname=(TextView)findViewById(R.id.cityname);
        title=(TextView)findViewById(R.id.tv_title);
        navi=(NavigationView)findViewById(R.id.navi);
        navname=(TextView)navi.getHeaderView(0).findViewById(R.id.tv_name);
        navlogin= (TextView) navi.getHeaderView(0).findViewById(R.id.tv_login);
        imgcircle=(CircleImageView)navi.getHeaderView(0).findViewById(R.id.img_circle);

        Glide.with(this).load(R.drawable.intitle).into(imgtitle);
        if (intent.getStringExtra("what").equals("searchKeyword")){
            imgtitle.setVisibility(View.INVISIBLE);
            title.setVisibility(View.VISIBLE);
            Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/210manB.ttf");
            title.setTypeface(typeface);
            title.setText(intent.getStringExtra("query"));

        }
    }

    void readurl(){
        selecturl();
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject object=response.getJSONObject("response");
                    object=object.getJSONObject("body");
                    object=object.getJSONObject("items");
                    JSONArray array=object.getJSONArray("item");
                    for (int i=0;i<array.length();i++){
                        valueZero();
                        object=array.getJSONObject(i);
                        String title=object.getString("title");
                        String contentid=object.getString("contentid");
                        String contenttypeid=object.getString("contenttypeid");
                        String firstimage="noimage";
                        if (object.has("firstimage"))firstimage=object.getString("firstimage");

                        items.add(new Item(title,firstimage,contentid,contenttypeid));
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    valueZero();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);

    }

    void valueZero(){
        if (items.size()==0){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                recyclerView.setBackground(ContextCompat.getDrawable(this, R.drawable.noimageavailable));
            } else {
                recyclerView.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.noimageavailable));
            }
        }
        else if (items.size()>0){
            recyclerView.setBackgroundColor(Color.WHITE);
        }
    }

    void spinnercustom(){

        ArrayAdapter arrayAdapter=ArrayAdapter.createFromResource(this,R.array.datas,R.layout.spinner_item);
        spin.setAdapter(arrayAdapter);
        int num = 0;
        if (G.arrange.equals("A"))num=0; else if (G.arrange.equals("B"))num=1; else if (G.arrange.equals("C")) num=2; else if (G.arrange.equals("D")) num=3;
        spin.setSelection(num);
    }

    void changenaviitem(){
        if (G.isLogin)navname.setText(G.nickname+" 님");else navname.setText(G.nickname);
        Glide.with(this).load(G.profile_image).into(imgcircle);
    }

    void changenaveLogin(){
        if (G.isLogin==false) navlogin.setText(" 로그인 하기 ");
        if (G.isLogin==true) navlogin.setText(" 로그아웃 ");
    }

    public void clickFab(View v){
        finish();
    }

    public void clickLogin(View v){
        if (G.isLogin==false )startActivityForResult(new Intent(this,LoginoutActivity.class),G.LOGIN);
        if (G.isLogin==true)startActivityForResult(new Intent(this,LoginoutActivity.class),G.LOGIN);
    }

    public void clickNavItem(View v){
        Intent intent1=new Intent(this,MainActivity.class);
        switch (v.getId()){
            case R.id.linear_festival:
                intent1.putExtra("clickAddition",0);
                setResult(88,intent1);
                finish();
                break;

            case R.id.linear_tour:
                intent1.putExtra("clickAddition",1);
                setResult(88,intent1);
                finish();
                break;

            case R.id.linear_course:
                intent1.putExtra("clickAddition",2);
                setResult(88,intent1);
                finish();
                break;
        }
    }
}

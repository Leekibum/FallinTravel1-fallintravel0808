package com.probum.fallintravel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
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
import android.widget.ImageView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addition);
        context=this;
        pageNo=1;
        intent=getIntent();
        findIds();
        requestQueue= Volley.newRequestQueue(this);

        RecyclerView.LayoutManager manager= new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager);
        adapter=new TourAdapter(items,this,this);
        recyclerView.setAdapter(adapter);

        setSupportActionBar(toolbar);
        ActionBar actionBar= getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        drawerToggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.action_settings,R.string.action_settings);

        //네비게이션 드로어어
//       drawerToggle.syncState();
//        drawerLayout.addDrawerListener(drawerToggle);

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
        startActivity(intent);
        finish();
    }

    void findIds(){
        ImageView imgtitle=(ImageView)findViewById(R.id.img_title);
        recyclerView=(RecyclerView)findViewById(R.id.layout_recycler);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        drawerLayout=(DrawerLayout)findViewById(R.id.layout_drawer);

        Glide.with(this).load(R.drawable.intitle).into(imgtitle);
    }

    void readurl(){
        url=intent.getStringExtra("url")+pageNo+"&_type=json";
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject object=response.getJSONObject("response");
                    object=object.getJSONObject("body");
                    object=object.getJSONObject("items");
                    JSONArray array=object.getJSONArray("item");
                    for (int i=0;i<array.length();i++){
                        changeBackground();
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
                    changeBackground();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);

    }

    void changeBackground(){
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

    public void clickFab(View v){
        finish();
    }
}

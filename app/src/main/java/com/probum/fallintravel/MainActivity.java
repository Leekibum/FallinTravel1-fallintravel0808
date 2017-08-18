package com.probum.fallintravel;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.security.MessageDigest;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.kakao.util.helper.Utility.getPackageInfo;

public class MainActivity extends AppCompatActivity {

    ActionBarDrawerToggle drawerToggle;
    NavigationView navi;
    TabLayout tabLayout;
    ViewPager viewPager;
    PageAdapter pageAdapter;
    FragmentManager fragmentManager;
    SearchView searchView;
    Typeface typeface;
    TextView cityname,navlogin,navname;
    DrawerLayout drawerLayout;
    boolean isnavislide=false;
    BackPressCloseHandler backPressCloseHandler;
    CircleImageView imgcircle;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;

        //// 키해시 구하기 !
        getAppKeyHash();

        loadData();


        navi=(NavigationView)findViewById(R.id.navi);
        tabLayout=(TabLayout)findViewById(R.id.layout_tab);
        viewPager=(ViewPager)findViewById(R.id.pager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        cityname=(TextView)findViewById(R.id.cityname);
        drawerLayout=(DrawerLayout)findViewById(R.id.layout_drawer);
        backPressCloseHandler=new BackPressCloseHandler(this);
        setSupportActionBar(toolbar);

        navname=(TextView)navi.getHeaderView(0).findViewById(R.id.tv_name);
        navlogin= (TextView) navi.getHeaderView(0).findViewById(R.id.tv_login);
        imgcircle=(CircleImageView)navi.getHeaderView(0).findViewById(R.id.img_circle);

        changenaviitem(G.nickname,G.profile_image);
        changenaveLogin();


        typeface = Typeface.createFromAsset(getAssets(),"ssanaiL.ttf");

        SpannableString spannableString=new SpannableString("여행에 빠지다");
        spannableString.setSpan(new CustomTypefaceSpan("", typeface),0,spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ActionBar actionBar= getSupportActionBar();
        actionBar.setTitle(spannableString);

        drawerToggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.action_settings,R.string.action_settings);

        drawerToggle.syncState();
        drawerLayout.addDrawerListener(drawerToggle);

        fragmentManager=getSupportFragmentManager();
        pageAdapter =new PageAdapter(fragmentManager);

        pageAdapter.sendassets(getAssets());

        viewPager.setAdapter(pageAdapter);

        tabLayout.setupWithViewPager(viewPager,true);

        if (Build.VERSION.SDK_INT>=21){ //버전 21 이상은 위에 상태바 색 변경경
           getWindow().setStatusBarColor(0xff55ccc0);
        }
        cityname.setText(G.cityname + "   " +G.sigunguName);

        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                isnavislide=true;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                isnavislide=false;
            }
        });
    }//onCreate


    void saveData(){
        SharedPreferences pref=getSharedPreferences("data",MODE_PRIVATE);//xml파일
        SharedPreferences.Editor editor =pref.edit();
        editor.putString("cityname",G.cityname);
        editor.putString("citycode",G.citycode);
        editor.putString("sigunguName",G.sigunguName);
        editor.putString("sigunguCode",G.sigunguCode);
        editor.putBoolean("isLogin",G.isLogin);
        editor.putString("nickname",G.nickname);
        editor.putString("profile_image",G.profile_image);
        editor.commit();

    }

    void loadData(){
        SharedPreferences pref=getSharedPreferences("data",MODE_PRIVATE);//xml파일

        G.cityname=pref.getString("cityname","서울");
        G.citycode=pref.getString("citycode","1");
        G.sigunguName=pref.getString("sigunguName","시군구 선택");
        G.sigunguCode=pref.getString("sigunguCode","1");
        G.isLogin=pref.getBoolean("isLogin",false);
        G.nickname=pref.getString("nickname","로그인을 해주세요");
        G.profile_image=pref.getString("profile_image","https://raw.githubusercontent.com/Leekibum/FallinTravel1-fallintravel0808/50e8dbf96beaf25e06f735f5e071c272787bfe41/account.png");
    }

    void changenaviitem(String nickname,String profile_image){
        navname.setText(nickname);
        Glide.with(this).load(profile_image).into(imgcircle);
    }
    void changenaveLogin(){
        if (G.isLogin==false) navlogin.setText("로그인 ");
        if (G.isLogin==true) navlogin.setText("로그아웃");
    }

    @Override
    protected void onDestroy() {
        saveData();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        saveData();
        super.onPause();
    }

    public void clickLogin(View v){
        if (G.isLogin==false)startActivityForResult(new Intent(this,LoginoutActivity.class),G.LOGINOUT);
        else if (G.isLogin==true){
            sendBroadcast(new Intent("logOut!"));
            changenaviitem(G.nickname,G.profile_image);
            changenaveLogin();
        }
    }

    public void clickBack(View v){
        drawerLayout.closeDrawer(navi);
    }

    @Override
    public void onBackPressed() {
        if (isnavislide==true) drawerLayout.closeDrawer(navi);
        else backPressCloseHandler.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        final MenuItem item=menu.findItem(R.id.menu_search);

        searchView=(SearchView)item.getActionView();
        searchView.setInputType(InputType.TYPE_CLASS_TEXT);
        searchView.setIconifiedByDefault(true);




        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();
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

        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void clickChoiceCity(View v){

        Intent intent=new Intent(this,ChoiceCityActivity.class);
        startActivityForResult(intent,22);

//        Toast.makeText(this, "지역 선택", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode){
            case G.SELECT_LOCATION:
                cityname.setText(G.cityname + "   " +G.sigunguName);
                saveData();

                pageAdapter.festivalFragment.items.clear();
                if (pageAdapter.festivalFragment.requestQueue!=null){pageAdapter.festivalFragment.setValue();pageAdapter.festivalFragment.readfestival();}

                pageAdapter.tourFragment.items.clear();
                if (pageAdapter.tourFragment.requestQueue!=null){pageAdapter.tourFragment.setValue(); pageAdapter.tourFragment.readtour();}

                pageAdapter.locationFragment.items.clear();
                if (pageAdapter.locationFragment.requestQueue!=null){pageAdapter.locationFragment.setValue(); pageAdapter.locationFragment.readLocation();}

                break;

            case G.LOGINOUT:
                    changenaviitem(G.nickname,G.profile_image);
                    changenaveLogin();
                break;
        }

          }

    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.d("Hash key", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }


}

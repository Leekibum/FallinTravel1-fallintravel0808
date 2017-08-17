package com.probum.fallintravel;


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

import java.security.MessageDigest;

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
    TextView cityname,navlogin;
    DrawerLayout drawerLayout;
    boolean isnavislide=false;
    BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //// 키해시 구하기 !
        getAppKeyHash();

        SharedPreferences pref=getSharedPreferences("data",MODE_PRIVATE);//xml파일

        G.cityname=pref.getString("cityname","서울");
        G.citycode=pref.getString("citycode","1");
        G.sigunguName=pref.getString("sigunguName","시군구 선택");
        G.sigunguCode=pref.getString("sigunguCode","1");

        navi=(NavigationView)findViewById(R.id.navi);
        tabLayout=(TabLayout)findViewById(R.id.layout_tab);
        viewPager=(ViewPager)findViewById(R.id.pager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        cityname=(TextView)findViewById(R.id.cityname);
        drawerLayout=(DrawerLayout)findViewById(R.id.layout_drawer);
        backPressCloseHandler=new BackPressCloseHandler(this);
        setSupportActionBar(toolbar);

        navlogin= (TextView) navi.getHeaderView(0).findViewById(R.id.tv_login);



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

    void changeNavlogin(String text){
        navlogin.setText(text);
    }

    void saveData(){
        SharedPreferences pref=getSharedPreferences("data",MODE_PRIVATE);//xml파일
        SharedPreferences.Editor editor =pref.edit();
        editor.putString("cityname",G.cityname);
        editor.putString("citycode",G.citycode);
        editor.putString("sigunguName",G.sigunguName);
        editor.putString("sigunguCode",G.sigunguCode);
        editor.commit();

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
        startActivityForResult(new Intent(this,LoginoutActivity.class),G.LOGINOUT);
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

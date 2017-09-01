package com.probum.fallintravel;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
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
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.hdodenhof.circleimageview.CircleImageView;


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
    Toolbar toolbar;
    Spinner spin;
    ImageView intitle;
    String keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;

        loadData();
        findIds();

        backPressCloseHandler=new BackPressCloseHandler(this);
        setSupportActionBar(toolbar);

        changenaviitem();
//        HashHash();

//        typeface = Typeface.createFromAsset(getAssets(),"ssanaiL.ttf");
//        SpannableString spannableString=new SpannableString("여행에 빠지다");
//        spannableString.setSpan(new CustomTypefaceSpan("", typeface),0,spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ActionBar actionBar= getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        drawerToggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.action_settings,R.string.action_settings);
        drawerToggle.syncState();
        drawerLayout.addDrawerListener(drawerToggle);

        fragmentManager=getSupportFragmentManager();
        pageAdapter =new PageAdapter(fragmentManager);

        pageAdapter.sendassets(getAssets());

        viewPager.setAdapter(pageAdapter);

        tabLayout.setupWithViewPager(viewPager,true);

        //tab layout set position
//        TabLayout.Tab tab=tabLayout.getTabAt(2);
//        tab.select();

        if (G.isFirst)cityname.setText(G.cityname + " " +G.sigunguName);
        else if (!G.isFirst)cityname.setText("지역 선택");

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

    void spinnercustom(){
        //스피너 색 자바로 바꾸기 .버전 16이상이므로 나는 테마를 줘서
//        Drawable spinDrawable= spin.getBackground().getConstantState().newDrawable();
//
//        spinDrawable.setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_ATOP);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            spin.setBackground(spinDrawable);
//        }
        ArrayAdapter arrayAdapter=ArrayAdapter.createFromResource(this,R.array.datas,R.layout.spinner_item);
        spin.setAdapter(arrayAdapter);
        int num = 0;
        if (G.arrange.equals("A"))num=0; else if (G.arrange.equals("B"))num=1; else if (G.arrange.equals("C")) num=2; else if (G.arrange.equals("D")) num=3;
        spin.setSelection(num);
    }

    void findIds(){
        navi=(NavigationView)findViewById(R.id.navi);
        tabLayout=(TabLayout)findViewById(R.id.layout_tab);
        viewPager=(ViewPager)findViewById(R.id.pager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        cityname=(TextView)findViewById(R.id.cityname);
        drawerLayout=(DrawerLayout)findViewById(R.id.layout_drawer);
        navname=(TextView)navi.getHeaderView(0).findViewById(R.id.tv_name);
        navlogin= (TextView) navi.getHeaderView(0).findViewById(R.id.tv_login);
        imgcircle=(CircleImageView)navi.getHeaderView(0).findViewById(R.id.img_circle);
        spin=(Spinner)findViewById(R.id.spin);
        intitle=(ImageView)findViewById(R.id.img_title);

        Glide.with(this).load(R.drawable.intitle).into(intitle);
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

    void loadData(){
        SharedPreferences pref=getSharedPreferences("data",MODE_PRIVATE);//xml파일

        G.cityname=pref.getString("cityname","서울");
        G.citycode=pref.getString("citycode","1");
        G.sigunguName=pref.getString("sigunguName","시군구 선택");
        G.sigunguCode=pref.getString("sigunguCode","1");
        G.isLogin=pref.getBoolean("isLogin",false);
        G.nickname=pref.getString("nickname"," 로그인을 해주세요 ");
        G.arrange=pref.getString("arrange","D");
        G.isFirst=pref.getBoolean("isFirst",false);
        G.id=pref.getString("id","");
        G.profile_image=pref.getString("profile_image","https://raw.githubusercontent.com/Leekibum/FallinTravel1-fallintravel0808/50e8dbf96beaf25e06f735f5e071c272787bfe41/account.png");
    }

    void changenaviitem(){
        if (G.isLogin)navname.setText(G.nickname+" 님");else navname.setText(G.nickname);
        Glide.with(this).load(G.profile_image).into(imgcircle);
        if (G.isLogin==false) navlogin.setText(" 로그인 하기 ");
        if (G.isLogin==true) navlogin.setText(" 로그아웃 ");
    }



    @Override
    protected void onDestroy() {
        saveData();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        if (G.isFirst)cityname.setText(G.cityname + " " +G.sigunguName);
        else if (!G.isFirst)cityname.setText("지역 선택");
        int num = 0;
        if (G.arrange.equals("A"))num=0; else if (G.arrange.equals("B"))num=1; else if (G.arrange.equals("C")) num=2; else if (G.arrange.equals("D")) num=3;
        spin.setSelection(num);
        super.onStart();
        changenaviitem();
        changeNaviPosition(G.clickAddition);

    }

    @Override
    protected void onPause() {
        saveData();
        super.onPause();
    }

    void changeNaviPosition(int num){
        TabLayout.Tab tab=tabLayout.getTabAt(num);
        tab.select();
    }

    public void clickNavItem(View v){
        Intent intent1=new Intent(this,AdditionActivity.class);
        switch (v.getId()){

            case R.id.linear_festival:
                changeNaviPosition(0);
                drawerLayout.closeDrawer(navi);
                break;

            case R.id.linear_tour:
                changeNaviPosition(1);
                drawerLayout.closeDrawer(navi);
                break;

            case R.id.linear_course:
                changeNaviPosition(2);
                drawerLayout.closeDrawer(navi);
                break;

            case R.id.linear_lports:
                intent1.putExtra("what",G.lports);
                startActivity(intent1);
                drawerLayout.closeDrawer(navi);
                break;

            case R.id.linear_stay:
                intent1.putExtra("what",G.stay);
                startActivity(intent1);
                drawerLayout.closeDrawer(navi);
                break;

            case R.id.linear_shopping:
                intent1.putExtra("what",G.shopping);
                startActivity(intent1);
                drawerLayout.closeDrawer(navi);
                break;

            case R.id.linear_eatery:
                intent1.putExtra("what",G.eatery);
                startActivity(intent1);
                drawerLayout.closeDrawer(navi);
                break;
        }
    }

    public void clickLogin(View v){
        if (G.isLogin==false )startActivityForResult(new Intent(this,LoginoutActivity.class),G.LOGIN);
        if (G.isLogin==true)startActivityForResult(new Intent(this,LoginoutActivity.class),G.LOGIN);
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
        //searchview
        final MenuItem item=menu.findItem(R.id.menu_search);
        searchView=(SearchView)item.getActionView();
        searchView.setIconifiedByDefault(true);



        MenuItem heart=menu.findItem(R.id.menu_heart);
        heart.setEnabled(false);


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

    void searchgood(String query){
        Intent intent=new Intent(this,AdditionActivity.class);
        try {
           keyword= URLEncoder.encode(query,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        String url="http://api.visitkorea.or.kr/openapi/service/rest/KorService/searchKeyword?ServiceKey="+G.serviceKey+"&keyword="+keyword+"&areaCode=&sigunguCode=&cat1=&cat2=&cat3=&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=A&numOfRows=12&pageNo=";
        intent.putExtra("what","searchKeyword");
        intent.putExtra("query",query);
        intent.putExtra("keyword",keyword);
        startActivityForResult(intent,88);
    }

    public void clickChoiceCity(View v){
        startActivityForResult(new Intent(this,ChoiceCityActivity.class),22);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode){
            case G.SELECT_LOCATION:
                cityname.setText(G.cityname + " " +G.sigunguName);
                saveData();
                recyclerItemChange();

                break;

            case G.LOGIN:
                    changenaviitem();
                break;
            case G.LOGOUT:
                changenaviitem();
                break;

        }

          }


    void recyclerItemChange(){
        pageAdapter.festivalFragment.items.clear();
        if (pageAdapter.festivalFragment.requestQueue!=null){pageAdapter.festivalFragment.setValue();pageAdapter.festivalFragment.readfestival();}

        pageAdapter.tourFragment.items.clear();
        if (pageAdapter.tourFragment.requestQueue!=null){pageAdapter.tourFragment.setValue(); pageAdapter.tourFragment.readtour();}

        pageAdapter.locationFragment.items.clear();
        if (pageAdapter.locationFragment.requestQueue!=null){pageAdapter.locationFragment.setValue(); pageAdapter.locationFragment.readLocation();}

    }

    public void HashHash() {

        try {
            PackageInfo Info = getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : Info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("키", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {


        }
    }
}



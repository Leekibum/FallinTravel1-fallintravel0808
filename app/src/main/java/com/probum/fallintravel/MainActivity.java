package com.probum.fallintravel;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
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
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ActionBarDrawerToggle drawerToggle;
    NavigationView navi;
    TabLayout tabLayout;
    ViewPager viewPager;
    PageAdapter pageAdapter;
    FragmentManager fragmentManager;
    SearchView searchView;
    Typeface typeface;
    TextView cityname;
    DrawerLayout drawerLayout;
    boolean isnavislide=false;
    BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navi=(NavigationView)findViewById(R.id.navi);
        tabLayout=(TabLayout)findViewById(R.id.layout_tab);
        viewPager=(ViewPager)findViewById(R.id.pager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        cityname=(TextView)findViewById(R.id.cityname);
        drawerLayout=(DrawerLayout)findViewById(R.id.layout_drawer);
        backPressCloseHandler=new BackPressCloseHandler(this);
        setSupportActionBar(toolbar);

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

    public void clickLogin(View v){
        startActivity(new Intent(this,LoginActivity.class));
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
        cityname.setText(G.cityname + "   " +G.sigunguName);

        pageAdapter.festivalFragment.items.clear();
        if (pageAdapter.festivalFragment.requestQueue!=null){pageAdapter.festivalFragment.setValue();pageAdapter.festivalFragment.readfestival();}

        pageAdapter.tourFragment.items.clear();
        if (pageAdapter.tourFragment.requestQueue!=null){pageAdapter.tourFragment.setValue(); pageAdapter.tourFragment.readtour();}

        pageAdapter.locationFragment.items.clear();
       if (pageAdapter.locationFragment.requestQueue!=null){pageAdapter.locationFragment.setValue(); pageAdapter.locationFragment.readLocation();}
    }



}

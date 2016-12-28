package com.example.ngel.crono;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ángel on 27/12/2016.
 */

public class MainActivity extends AppCompatActivity {

    //DECLARACIÓN DE LOS RECURSOS DEL .xml
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.onBackPressed();

        //DEFINICIÓN DE Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        //DEFINICIÓN DE ViewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager); //se definen el nº de pestañas (nombre_pestaña + fragment)

        //DEFINICIÓN DE TabLayout
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager); //se asigna el ViewPager al TabLayout

    }

    //Define el nº de pestañas
    public void setupViewPager(ViewPager upViewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ResultCronoFragment(), "Mi resultado"); //Pestaña 1
        adapter.addFragment(new RankingCronoFragment(), "Resultado General"); //Pestaña 2
        viewPager.setAdapter(adapter); //se le asigna el adapter al viewPager
    }

    @Override
    public void onBackPressed() {} //para que no pueda regresar al activity anterior

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        //Esta clase actua como manejador de las pestañas => sabiendo cual se selecciona crea un determiando fragment u otro
        private final List<Fragment> mFragmentList = new ArrayList<>(); //almacena los fragments
        private final List<String> mFragmentTitleList = new ArrayList<>();  //almacena el nombre de la pestaña

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
        //Inserta en cada respectivo arraylist el fragment y el nombre de su pestaña
        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}

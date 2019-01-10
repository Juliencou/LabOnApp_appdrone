package com.example.julien.appdrone;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;


public class MainActivity extends AppCompatActivity implements  MyProfileFragment.OnFragmentInteractionListener,
                                                                MySettingsFragment.OnFragmentInteractionListener,
                                                                MyHistoryFragment.OnFragmentInteractionListener {

    private final String TAG = this.getClass().getSimpleName();


    private MyProfileFragment myProfileFragment;
    private MyHistoryFragment myHistoryFragment;
    private MySettingsFragment mySettingsFragment;
    private SectionsStatePagerAdapter mSectionStatePagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mSectionStatePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());

        // Do this in case of detaching of Fragments
        myProfileFragment = new MyProfileFragment();
        myHistoryFragment = new MyHistoryFragment();
        mySettingsFragment = new MySettingsFragment();

        ViewPager mViewPager = findViewById(R.id.mainViewPager);
        setUpViewPager(mViewPager);

    }


    private void setUpViewPager(ViewPager mViewPager) {
        mSectionStatePagerAdapter.addFragment(myProfileFragment, getString(R.string.tab_title_my_profile));
        mSectionStatePagerAdapter.addFragment(myHistoryFragment, getString(R.string.tab_title_my_history));
        mSectionStatePagerAdapter.addFragment(mySettingsFragment, getString(R.string.tab_title_my_settings));
        mViewPager.setAdapter(mSectionStatePagerAdapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
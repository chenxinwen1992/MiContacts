package org.chenxinwen.micontacts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.chenxinwen.micontacts.fragment.CallFragment;
import org.chenxinwen.micontacts.fragment.ContactsFragment;
import org.chenxinwen.micontacts.fragment.YellowPageFragment;
import org.chenxinwen.micontacts.view.MiTab;

/**
 * Created by chenxinwen on 16/8/9.09:49.
 * Email:191205292@qq.com
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private MiTab mTab;
    private ViewPager mViewPager;
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mTab = (MiTab) findViewById(R.id.mTab);
        mViewPager = (ViewPager) findViewById(R.id.mViewPager);
        mFab = (FloatingActionButton) findViewById(R.id.mFab);

        mFab.setOnClickListener(this);


        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mViewPager.setOffscreenPageLimit(2);
        mTab.setViewPager(mViewPager);

        mViewPager.setCurrentItem(1);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mFab:

                break;
        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private String[] titles = {getString(R.string.call),
                getString(R.string.contacts),
                getString(R.string.yellow_page)};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return CallFragment.newInstance();
            } else if (position == 1) {
                return ContactsFragment.newInstance();
            } else {
                return YellowPageFragment.newInstance();
            }
        }

    }
}

package com.ibm.rtc.rtc.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ibm.rtc.rtc.R;
import com.ibm.rtc.rtc.model.Workitem;
import com.ibm.rtc.rtc.ui.base.TitleProvider;
import com.ibm.rtc.rtc.ui.fragment.WorkitemCommentsFragment;
import com.ibm.rtc.rtc.ui.fragment.WorkitemDetailFragment;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v-wajie on 1/6/2016.
 */
public class WorkitemActivity extends AppCompatActivity {
    private static final String TAG = "WorkitemActivity";
    public static final String WORKITME_INFO = "WORKITME_INFO";

    private Toolbar mToolbar;
    private ArrayList<Fragment> mFragments;
    private ViewPager mViewPager;

    private Workitem mWorkitem;

    public static Intent createLauncherIntent(Context context, Workitem workitem) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(WORKITME_INFO, workitem);

        Intent intent = new Intent(context, WorkitemActivity.class);
        intent.putExtras(bundle);
        return intent;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workitem);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        if (mToolbar != null) {
            mToolbar.setTitle(R.string.app_name);
            setSupportActionBar(mToolbar);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //从Intent中获取将要展示的Workitem.
        if (getIntent().getExtras() != null) {
            mWorkitem = getIntent().getExtras().getParcelable(WORKITME_INFO);
            if (mWorkitem != null) {
                //设置标题
                mToolbar.setTitle(mWorkitem.getTitle());

                //获取ViewPager
                TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
                mViewPager = (ViewPager) findViewById(R.id.content);

                //添加fragment
                addFragments();

                NavigationAdapter adapter = new NavigationAdapter(getSupportFragmentManager(), mFragments);
                mViewPager.setAdapter(adapter);
                tabLayout.setupWithViewPager(mViewPager);

                showTabsIcons(tabLayout);
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    private void addFragments() {
        mFragments = new ArrayList<>();
        mFragments.add(WorkitemDetailFragment.newIntance(mWorkitem));
        mFragments.add(WorkitemCommentsFragment.newIntance(mWorkitem));
    }

    private void showTabsIcons(TabLayout tabLayout) {
        for (int i = 0; i < mFragments.size(); ++i) {
            Fragment fragment = mFragments.get(i);
            if (fragment instanceof TitleProvider) {
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                if (tab != null) {
                    IIcon iIcon = ((TitleProvider) fragment).getTitleIcon();
                    if (iIcon != null) {
                        Drawable icon = new IconicsDrawable(this, iIcon)
                                .sizeDp(14).colorRes(R.color.white);
                        tab.setIcon(icon);
                    }
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return false;
    }

    private class NavigationAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public NavigationAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments != null ? fragments.size() : 0;
        }
    }
}

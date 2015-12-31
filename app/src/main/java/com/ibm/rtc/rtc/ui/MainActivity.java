package com.ibm.rtc.rtc.ui;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ibm.rtc.rtc.R;
import com.ibm.rtc.rtc.account.Account;
import com.ibm.rtc.rtc.account.AccountManager;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.List;

/**
 * Created by v-wajie on 2015/12/8.
 */
public class MainActivity extends AppCompatActivity implements AccountHeader.OnAccountHeaderListener {

    private Toolbar mToolbar;
    private Drawer mDrawer;
    private View mContentView;
    private WorkitemsListFragment mWorkitemsListFragment;
    private Fragment mLastUsedFragment;

    public static void startActivity(Activity context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //在创建的时候判断是否已经有登录的账号，如果没有就启动LoginActivity
        AccountManager accountManager = AccountManager.getInstance(this);
        List<Account> accounts = accountManager.getAccounts();

        if (accounts == null || accounts.isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.generic_toolbar);
        mContentView = findViewById(R.id.content);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mDrawer == null) {
            createDrawer();
        }
    }

    private void createDrawer() {

        AccountHeader accountHeader = builderHeader();
        //create the Drawer
        DrawerBuilder drawerBuilder = new DrawerBuilder().withActivity(this)
                .withToolbar(getmToolbar())
                .withAccountHeader(accountHeader);
        drawerBuilder.addDrawerItems(new PrimaryDrawerItem().withName(R.string.drawer_menu_workitems)
                        .withIcon(R.mipmap.ic_launcher)
                        .withIdentifier(R.id.drawer_workitems), new PrimaryDrawerItem().withName(R.string.drawer_menu_projects)
                        .withIcon(R.mipmap.ic_launcher)
                        .withIdentifier(R.id.drawer_projects), new DividerDrawerItem(), new SecondaryDrawerItem()
                        .withName(R.string.drawer_menu_settings)
                        .withIdentifier(R.id.drawer_settings));

        drawerBuilder.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                if (drawerItem != null) {
                    int identifier = drawerItem.getIdentifier();
                    switch (identifier) {
                        case R.id.drawer_workitems:
                            OnWorkitemsSelected();
                            break;
                        case R.id.drawer_projects:
                            OnProjectsSelected();
                            break;
                        case R.id.drawer_settings:
                            OnSettingsSelected();
                            break;
                    }
                }

                return false;
            }
        });

        mDrawer = drawerBuilder.build();
        mDrawer.setSelection(R.id.drawer_workitems);
    }

    private AccountHeader builderHeader() {

        AccountHeaderBuilder headerBuilder = new AccountHeaderBuilder().withActivity(this)
                //TODO set the header Background color
                .withHeaderBackground(R.color.cardview_dark_background)
                .withOnAccountHeaderListener(this);

        ProfileDrawerItem profileDrawerItem = new ProfileDrawerItem().withName("Jack")
                .withEmail("Jackwangcs@outlook.com")
                .withIcon(R.mipmap.ic_launcher)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                        MainActivity.this.startActivity(intent);
                        return true;
                    }
                });

        headerBuilder.addProfiles(profileDrawerItem);
        return headerBuilder.build();
    }

    public Toolbar getmToolbar() {
        return mToolbar;
    }

    @Override
    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
        return false;
    }

    public boolean OnWorkitemsSelected() {
        if (mWorkitemsListFragment == null) {
            mWorkitemsListFragment = new WorkitemsListFragment();
        }
        setFragment(mWorkitemsListFragment, false);
        return true;
    }

    public boolean OnProjectsSelected() {
        Snackbar.make(mContentView, "Projects clicked", Snackbar.LENGTH_SHORT).show();
        return true;
    }

    public boolean OnSettingsSelected() {
        Snackbar.make(mContentView, "Settings clicked", Snackbar.LENGTH_SHORT).show();
        return true;
    }

    private void setFragment(Fragment fragment, boolean addToBackStack) {
        try {
            if (fragment != null && getSupportFragmentManager() != null) {
                this.mLastUsedFragment = fragment;
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if (transaction != null) {
                    transaction.replace(R.id.content, fragment);
                    if (addToBackStack) {
                        transaction.addToBackStack(null);
                    }
                    transaction.commit();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

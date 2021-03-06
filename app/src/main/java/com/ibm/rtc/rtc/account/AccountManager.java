package com.ibm.rtc.rtc.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by v-wajie on 2015/12/11.
 *
 */
public class AccountManager {

    private static AccountManager mInstance;
    private static Context mCtx;
    private SharedPreferences mSharedPreferences;
    private static final String ACCOUNT_PREFS_TAG = "Acount";
    private static final String ACCOUNT_LIST_TAG = "Acounts";

    private List<Account> mAccountList;

    private AccountManager(Context context) {
        mCtx = context;
        mSharedPreferences = context.getApplicationContext().getSharedPreferences(ACCOUNT_PREFS_TAG,
                Context.MODE_PRIVATE);
    }

    public static AccountManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AccountManager(context);
        }
        return mInstance;
    }

    public @Nullable ArrayList<Account> getAccounts() {
        ArrayList<Account> accountList = null;
        String jsonText = mSharedPreferences.getString(ACCOUNT_LIST_TAG, null);
        if (jsonText != null) {
            Gson gson = new Gson();
            accountList = new ArrayList<>();
            accountList.addAll(Arrays.asList(gson.fromJson(jsonText, Account[].class)));
        }
        return accountList;
    }

    public @Nullable Account getAccountByUsername(String username) {
        ArrayList<Account> accounts = getAccounts();
        if (accounts == null) {
            return null;
        }
        for (Account account : accounts) {
            if (account.getUsername().equals(username)) {
                return account;
            }
        }

        return null;
    }

    public void saveAccount(Account account) {
        List<Account> accountList = getAccounts();
        if (accountList == null)
            accountList = new ArrayList<Account>();
        accountList.add(account);

        saveAccountList(accountList);
    }

    public void removeAccount(String username) throws Exception {
        List<Account> accountList = getAccounts();
        if (accountList == null || accountList.isEmpty()) {
            throw new Exception("Account dose not exist!");
        }

        ArrayList<Account> accounts = new ArrayList<>(accountList);
        Iterator<Account> iterator = accounts.iterator();
        while (iterator.hasNext()) {
            Account account = iterator.next();
            if (account.getUsername().equals(username)) {
                iterator.remove();
            }
        }

        saveAccountList(accounts);
    }


    private void saveAccountList(List<Account> list) {
        Gson gson = new Gson();
        String jsonText = gson.toJson(list);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ACCOUNT_LIST_TAG, jsonText);
        editor.apply();
    }
}

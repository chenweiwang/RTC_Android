package com.ibm.rtc.rtc.ui.base;

import android.support.annotation.StringRes;

import com.mikepenz.iconics.typeface.IIcon;

/**
 * Created by v-wajie on 1/6/2016.
 */
public interface TitleProvider {
    @StringRes
    int getTitle();

    IIcon getTitleIcon();
}

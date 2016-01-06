package com.ibm.rtc.rtc.ui.base;

import android.support.v4.app.Fragment;

import com.ibm.rtc.rtc.model.Workitem;

/**
 * Created by v-wajie on 1/6/2016.
 */
public abstract class WorkitembaseFragment extends Fragment {

    protected static final String WORKITEM_INFO = "WORKITEM_INFO";
    private Workitem mWorkitem;

    private void loadArguments() {
        if (getArguments() != null) {
            mWorkitem = getArguments().getParcelable(WORKITEM_INFO);
        }
    }

    protected Workitem getWorkitem() {
        if (mWorkitem == null) {
            loadArguments();
        }
        return mWorkitem;
    }
}

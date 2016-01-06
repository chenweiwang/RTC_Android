package com.ibm.rtc.rtc.ui.fragment;

import android.os.Bundle;

import com.ibm.rtc.rtc.R;
import com.ibm.rtc.rtc.model.Workitem;
import com.ibm.rtc.rtc.ui.base.TitleProvider;
import com.ibm.rtc.rtc.ui.base.WorkitembaseFragment;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;

/**
 * Created by v-wajie on 1/6/2016.
 */
public class WorkitemDetailFragment extends WorkitembaseFragment implements TitleProvider {
    private static final String TAG = "WorkitemDetailFragment";

    public static WorkitemDetailFragment newIntance(Workitem workitem) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(WORKITEM_INFO, workitem);

        WorkitemDetailFragment detailFragment = new WorkitemDetailFragment();
        detailFragment.setArguments(bundle);
        return detailFragment;
    }



    @Override
    public int getTitle() {
        return R.string.workitem_detail_title;
    }

    @Override
    public IIcon getTitleIcon() {
        return Octicons.Icon.oct_info;
    }
}

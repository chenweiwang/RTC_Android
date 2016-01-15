package com.ibm.rtc.rtc.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.ibm.rtc.rtc.R;
import com.ibm.rtc.rtc.adapter.WorkitemAdapter;
import com.ibm.rtc.rtc.core.UrlManager;
import com.ibm.rtc.rtc.core.VolleyQueue;
import com.ibm.rtc.rtc.core.WorkitemSorter;
import com.ibm.rtc.rtc.core.WorkitemsRequest;
import com.ibm.rtc.rtc.model.Project;
import com.ibm.rtc.rtc.model.Workitem;
import com.ibm.rtc.rtc.ui.base.FilterChoice;
import com.ibm.rtc.rtc.ui.base.LoadingListFragment;
import com.ibm.rtc.rtc.ui.base.SortChoice;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jack on 2015/12/23.
 */
public class WorkitemsListFragment extends LoadingListFragment<WorkitemAdapter> {
    private static final String TAG = "WorkitemsListFragment";
    private static final String PROJECT_INFO = "PROJECT_INFO";
    private static final String WORKITEM_CONFIG = "WORKITEM_CONFIG";
    private static final String SORT = "SORT";

    private RequestQueue mRequestQueue;
    private Project mProject;
    private List<Workitem> list; // 当前project下的workitem列表
    private int sortType = -1; // 当前界面下workitem的排序方式，默认根据id升序排列
    private ArrayList<Integer> mFilterIds;
    private ArrayList<String> mFilterNames;
    private final int DEFAULT_STATUS_CODE = 500;

    public static WorkitemsListFragment newInstance(Project project) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(PROJECT_INFO, project);
        WorkitemsListFragment workitemsListFragment = new WorkitemsListFragment();
        workitemsListFragment.setArguments(bundle);
        return workitemsListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        loadArgumentsForProject();
        mRequestQueue = VolleyQueue.getInstance(getActivity()).getRequestQueue();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.workitem_list_fragment, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (getActivity() != null) {
            MenuItem itemFilter = menu.findItem(R.id.workitem_filter);
            if (itemFilter != null) {
                itemFilter.setIcon(new IconicsDrawable(getActivity(), GoogleMaterial.Icon.gmd_filter_list)
                    .colorRes(R.color.white).actionBar());
            }
            MenuItem itemSort = menu.findItem(R.id.workitem_sort);
            if (itemSort != null) {
                itemSort.setIcon(new IconicsDrawable(getActivity(), GoogleMaterial.Icon.gmd_sort)
                    .colorRes(R.color.white).actionBar());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.workitem_filter:
                onMenuItemFilterSelected();
                break;
            case R.id.workitem_sort:
                onMenuItemSortSelected();
                break;
        }

        return true;
    }

    private void onMenuItemFilterSelected() {
        FilterChoice[] choices = FilterChoice.values();
        //don't show the Unhandled option
        String[] names = new String[choices.length - 1];
        for (int i = 0; i < choices.length; ++i) {
            if (choices[i] != FilterChoice.Unhandled)
                names[i] = choices[i].name();
        }

        Integer[] ids = null;
        if (mFilterIds != null) {
            ids = mFilterIds.toArray(new Integer[mFilterIds.size()]);
        }

        new MaterialDialog.Builder(getActivity()).items(names)
                .itemsCallbackMultiChoice(ids, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        WorkitemsListFragment.this.mFilterIds = new ArrayList<>(Arrays.asList(which));
                        List<String> filters = new ArrayList<String>();
                        for (int i = 0; i < text.length; ++i) {
                            filters.add(String.valueOf(text[i]));
                        }

                        WorkitemsListFragment.this.mFilterNames = new ArrayList<String>(filters);
                        saveFilter();
                        executeRequest();
                        return false;
                    }
                })
                .positiveText(getString(R.string.dialog_ok_button))
                .neutralText(getString(R.string.dialog_clear_filter_button)).callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onNeutral(MaterialDialog dialog) {
                super.onNeutral(dialog);
                WorkitemsListFragment.this.mFilterIds = null;
                WorkitemsListFragment.this.mFilterNames = null;
                clearSavedFilter();
                executeRequest();
            }
        }).show();
    }

    private void onMenuItemSortSelected() {
        SortChoice[] choices = SortChoice.values();
        //don't show the Unhandled option
        String[] names = new String[choices.length - 1];
        for (int i = 0; i < choices.length; ++i) {
            if (choices[i] != SortChoice.Unhandled)
                names[i] = choices[i].name();
        }

        Integer selectedId;
        if (sortType == -1) {
            SharedPreferences sharedPreferences= getActivity().getSharedPreferences(WORKITEM_CONFIG, Context.MODE_PRIVATE);
            selectedId = sharedPreferences.getInt("WORKITEM_SORTER",0);
        } else {
            selectedId = sortType;
        }
        new MaterialDialog.Builder(getActivity()).items(names)
                .itemsCallbackSingleChoice(selectedId, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        // 改变页面workitem的排序方式，并将该方式缓存起来
                        sortType = which;
                        setUpList(list, sortType);
                        return true;
                    }
                }).show();
    }

    private void saveFilter() {
        if (mFilterIds != null && mFilterNames != null) {
            SharedPreferences sharedPreferences = getActivity()
                    .getSharedPreferences(WORKITEM_CONFIG, Context.MODE_PRIVATE);

            Gson gson = new Gson();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("WORKITEM_FILTER", gson.toJson(mFilterNames));
            editor.putString("WORKITEM_FILTER_IDS", gson.toJson(mFilterIds));
            editor.apply();
        }
    }

    private void clearSavedFilter() {
        SharedPreferences shared = getActivity()
                .getSharedPreferences(WORKITEM_CONFIG, Context.MODE_PRIVATE);

        SharedPreferences.Editor edit = shared.edit();
        edit.remove("WORKITEM_FILTER");
        edit.remove("WORKITEM_FILTER_IDS");
        edit.remove("WORKITEM_SORTER");
        edit.apply();
    }

    private void saveSorter() {
        SharedPreferences sharedPreferences = getActivity()
                .getSharedPreferences(WORKITEM_CONFIG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (sortType != -1) {
            editor.putInt("WORKITEM_SORTER", sortType);
        } else {
            editor.putInt("WORKITEM_SORTER", 0);
        }
        editor.apply();
    }


    private void loadArgumentsForProject() {
        if (getArguments() != null) {
            mProject = getArguments().getParcelable(PROJECT_INFO);
        } else {
            throw new IllegalStateException("Project must not be null");
        }
    }

    public void setUpList(List<Workitem> workitems, int which) {
        WorkitemAdapter adapter = new WorkitemAdapter(getActivity(),
                LayoutInflater.from(getActivity()));
        adapter.setRecyclerAdapterContentListener(this);


        //默认以id升序
        WorkitemSorter.sort(workitems, which);
        //将排序后的workitems添加到适配器中
        adapter.addAll(workitems);

        setAdapter(adapter);
    }

    @Override
    protected void executeRequest() {
        super.executeRequest();

        UrlManager urlManager = UrlManager.getInstance(getActivity());
        String workitemsUrl = urlManager.getRootUrl() + "workitems?uuid=" + mProject.getUuid();
        WorkitemsRequest workitemsRequest = new WorkitemsRequest(workitemsUrl,
            new Response.Listener<List<Workitem>>() {
                @Override
                public void onResponse(List<Workitem> workitems) {
                    if (workitems != null && !workitems.isEmpty()) {
                        list = workitems;
                        hideEmpty();
                        if (refreshing || getAdapter() == null) {
                            if (sortType == -1) {
                                SharedPreferences sharedPreferences= getActivity().getSharedPreferences(WORKITEM_CONFIG, Context.MODE_PRIVATE);
                                setUpList(workitems, sharedPreferences.getInt("WORKITEM_SORTER",0));
                            } else {
                                setUpList(workitems, sortType);
                            }
                        }
                    } else {
                        setEmpty();
                    }
                    stopRefresh();
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.d(TAG, "Fetch workitems error: " + volleyError.getMessage());
                    stopRefresh();

                    //TODO 设置合适的错误类型信息
                    setEmpty(true, volleyError.networkResponse == null ?
                        DEFAULT_STATUS_CODE : volleyError.networkResponse.statusCode);

                    if (getView() != null)
                        Snackbar.make(getView(), getText(R.string.workitem_list_refresh_error), Snackbar.LENGTH_SHORT).show();
                }
            });
        workitemsRequest.setTag(TAG);
        mRequestQueue.add(workitemsRequest);
    }

    @Override
    protected int getNoDataText() {
        return R.string.no_workitems;
    }

    @Override
    public void onStop() {
        saveSorter();
        super.onStop();
        mRequestQueue.cancelAll(TAG);
    }
}

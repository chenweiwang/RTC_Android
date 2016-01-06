package com.ibm.rtc.rtc.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ibm.rtc.rtc.R;
import com.ibm.rtc.rtc.adapter.RecyclerArrayAdapter;

import java.util.List;

import tr.xip.errorview.ErrorView;

/**
 * Created by Jack on 2015/12/17.
 */
public abstract class LoadingListFragment<Adapter extends RecyclerArrayAdapter> extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, RecyclerArrayAdapter.RecyclerAdapterContentListener,
        ErrorView.RetryListener {

    protected RecyclerView recyclerView;
    protected boolean fromRetry = false;
    protected boolean refreshing;
    private SwipeRefreshLayout swipe;
    private ErrorView errorView;
    private Adapter adapter;
    private View loadingView;
    private boolean fromPaginated;
    private Integer page;
    private List<ItemDecoration> listDecorators;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        //在onCreateView中设置Layout
        return inflater.inflate(R.layout.fragment_list, null, false);
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //在onViewCreated中获取布局中的视图实例。
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        loadingView = view.findViewById(R.id.loading_view);

        //RecyclerView设置布局管理器，设置滑动动画，以及装饰
        if (recyclerView != null) {
            recyclerView.setLayoutManager(getLayoutManager());
            recyclerView.setItemAnimator(getItemAnimator());
            if (getItemDecoration() != null) {
                recyclerView.addItemDecoration(getItemDecoration());
            }
        }

        errorView = (ErrorView) view.findViewById(R.id.error_view);
        swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe);

        //设置swipe的加载进度的颜色以及listener
        if (swipe != null) {
            swipe.setColorSchemeColors(getActivity().getResources().getColor(R.color.primary_light));
            swipe.setOnRefreshListener(this);
        }

        //为了加载Adapter并创建列表
        if (getAdapter() != null) {
            getAdapter().clear();
            setAdapter(null);
        }
        executeRequest();
    }

    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    protected RecyclerView.ItemAnimator getItemAnimator() {
        return new DefaultItemAnimator();
    }

    protected RecyclerView.ItemDecoration getItemDecoration() {
        return new DividerItemDecoration(getActivity(), DividerItemDecoration.LIST_VERTICAL);
    }

    public Adapter getAdapter() {
        return adapter;
    }

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
        if (this.adapter != null) {
            try {
                adapter.setRecyclerAdapterContentListener(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (recyclerView != null) {
            recyclerView.setAdapter(adapter);
        }
    }

    protected void startRefresh() {
        hideEmpty();
        if (swipe != null && (fromRetry || fromPaginated)) {
            fromRetry = false;
            swipe.post(new Runnable() {
                @Override
                public void run() {
                    swipe.setRefreshing(true);
                }
            });
        }

        if (!fromPaginated && loadingView != null &&
                (getAdapter() == null || getAdapter().getItemCount() == 0)) {
            loadingView.setVisibility(View.VISIBLE);
        }
    }

    protected void executeRequest() {
        startRefresh();
    }

    public void hideEmpty() {
        if (getActivity() != null) {
            if (errorView != null) {
                errorView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onRefresh() {
        refreshing = true;
        executeRequest();
    }

    public void setRefreshing() {
        this.refreshing = true;
    }

    @Override
    public void loadMoreItems() {
        if (page != null) {
            executePaginatedRequest(page);
            page = null;
        }
    }

    @Override
    public void onRetry() {
        hideEmpty();
        fromRetry = true;
        executeRequest();
    }

    protected void executePaginatedRequest(int page) {
        fromPaginated = true;
        startRefresh();
    }

    protected void stopRefresh() {
        if (swipe != null) {
            fromRetry = false;
            swipe.post(new Runnable() {
                @Override
                public void run() {
                    swipe.setRefreshing(false);
                }
            });
        }

        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
    }

    public void setEmpty() {
        stopRefresh();
        if (getActivity() != null) {
            if (errorView != null) {
                errorView.setVisibility(View.VISIBLE);
                errorView.setTitle(getNoDataText());
                errorView.showRetryButton(true);
                errorView.setOnRetryListener(this);
            }
        }

        if (recyclerView != null) {
            recyclerView.setLayoutManager(getLayoutManager());
            recyclerView.setItemAnimator(getItemAnimator());
            if (getItemDecoration() != null) {
                recyclerView.addItemDecoration(getItemDecoration());
            }
        }
    }

    public void setEmpty(boolean withError, int statusCode) {
        if (getActivity() != null) {
            if (errorView != null) {
                errorView.setVisibility(View.VISIBLE);
                errorView.setError(statusCode);
                errorView.showRetryButton(withError);
                if (withError) {
                    errorView.setOnRetryListener(this);
                }
            }
        }
    }


    protected abstract int getNoDataText();

}


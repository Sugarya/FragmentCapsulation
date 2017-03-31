package com.sugary.fragmentcapsulation.base;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import com.sugary.fragmentcapsulation.R;

import butterknife.BindView;

/**
 * Created by Ethan on 2017/03/30
 *
 * 1.Toolbar设置 ,左侧返回键监听  2.屏幕顶部状态栏适配
 */
public abstract class BasicFragmentWithToolbar extends BasicFragment {

    private static final String TAG = "BasicFragmentWithToolbar";

    @BindView(R.id.toolbar_include)
    Toolbar mToolbar;

    @BindView(R.id.tv_include_header_title)
    TextView mTvHeaderTitle;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        initToolbar();
        setupToolbar();
        adaptStatusBar();
        return view;
    }

    /**
     * Android 4.4时，设置状态栏透明，来适配标题栏样式
     */
    private void adaptStatusBar() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            if (mToolbar != null) {
                WindowManager.LayoutParams localLayoutParams = getAttachActivity().getWindow().getAttributes();
                localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);

                int toolbarHeight = (int)getResources().getDimension(R.dimen.tool_bar_height_adapt);
                ViewGroup.LayoutParams layoutParams = mToolbar.getLayoutParams();
                layoutParams.height = toolbarHeight;

                int toolbarPaddingTop = (int) getResources().getDimension(R.dimen.tool_bar_padding_top);
                mToolbar.setPadding(0, toolbarPaddingTop, 0, 0);
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            configureToolbar();
        }
    }

    /**
     * 初始化设置toolbar
     */
    private void initToolbar() {
        mToolbar.setTitle("");
        mToolbar.setSubtitle("");
        getAttachActivity().setSupportActionBar(mToolbar);
    }

    @Override
    protected void configureToolbar() {
        super.configureToolbar();
        int stackEntryCount = getChildFragmentManager().getBackStackEntryCount();
        if (stackEntryCount > 0) {
            FragmentManager.BackStackEntry stackEntry = getChildFragmentManager().getBackStackEntryAt(stackEntryCount - 1);
            //Get the name that was supplied to FragmentTransaction.addToBackStack(String) when creating this entry
            String fragmentTab = stackEntry.getName();
            BasicFragmentWithToolbar fragmentByTag = (BasicFragmentWithToolbar) getChildFragmentManager().findFragmentByTag(fragmentTab);
            if (fragmentByTag != null) {
                fragmentByTag.setupToolbar();
            }
        } else {
            setupToolbar();
        }
    }

    /**
     * 设置Toolbar的显示内容
     */
    protected void setupToolbar() {
        if(mToolbar != null) {
            mToolbar.setNavigationIcon(R.drawable.icon_header_left);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getAttachActivity().onBackPressed();
                }
            });
        }
    }

    /**
     * 隐藏toolbar
     */
    public void hideToolbar() {
        if(mToolbar != null) {
            mToolbar.setVisibility(View.GONE);
        }
    }

    public void setToolbarVisibilty(int visibility) {
        if(mToolbar != null) {
            mToolbar.setVisibility(visibility);
        }
    }


    public void setHeaderTitle(String titleContent) {
        if(mTvHeaderTitle != null) {
            mTvHeaderTitle.setText(titleContent);
        }
    }

    public void setRawTitle(String title){
        if(mToolbar != null) {
            mToolbar.setTitle(title);
        }
    }

    public void setHeaderTitle(@StringRes int titleResID) {
        if(mTvHeaderTitle != null) {
            mTvHeaderTitle.setText(titleResID);
        }
    }

    public void setNavigationIcon(@DrawableRes int resID) {
        if(mToolbar != null) {
            mToolbar.setNavigationIcon(resID);
        }
    }

    public void setupToolbar(@DrawableRes int leftResID, String titleContent) {
        if(mToolbar != null){
            mToolbar.setNavigationIcon(leftResID);
        }
        setHeaderTitle(titleContent);
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }


    protected abstract
    @LayoutRes
    int getLayoutResID();


}

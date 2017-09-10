package com.sugary.fragmentcapsulation.base;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sugary.fragmentcapsulation.R;
import com.sugary.fragmentcapsulation.utils.KeyboardUtils;
import com.sugary.fragmentcapsulation.utils.LOG;

import butterknife.BindView;
import butterknife.OnClick;
import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;

/**
 * Created by Ethan on 17/9/05.
 * 1.顶部搜索栏功能
 */
public abstract class BasicSearchBarFragment extends BasicToolbarFragment {

    private static final String TAG = BasicSearchBarFragment.class.getSimpleName();

    @BindView(R.id.rframe_include)
    RevealFrameLayout mRevealFrameLayout;

    @BindView(R.id.rl_include_search_bar)
    RelativeLayout mSearchBar;

    @BindView(R.id.ed_header_search)
    EditText mEdSearch;

    @BindView(R.id.img_header_icon)
    ImageView mImgSearchBack;


    private String mSearchWord = "";
    private int mLastSearchContentLength = 0;
    /**
     * 能否搜索标识
     */
    private boolean mShowSearchToolbar = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        adaptSearchBar();
        initSearchView();
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
           reactionToSearchBack();
        }
    }

    @OnClick(R.id.container_header_back_icon)
    public void onHeaderBackClick(View view) {
        reactionToSearchBack();
    }

    @Override
    protected boolean onBackPressed() {
        super.onBackPressed();
        if ((reactionToSearchBack())) {
            return true;
        }

        return false;
    }

    /**
     * Android 4.4时，设置状态栏透明，来适配搜索栏
     */
    private void adaptSearchBar() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            int marginTopValue = (int) getResources().getDimension(R.dimen.tool_bar_padding_top);
            RelativeLayout.MarginLayoutParams layoutParams = (RelativeLayout.MarginLayoutParams) mSearchBar.getLayoutParams();
            layoutParams.topMargin = marginTopValue;
        }
    }

    private void initSearchView() {
        mEdSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                mSearchWord = v.getText().toString();
                LOG.d(TAG, "onEditorAction searchWord=" + mSearchWord);
                fetchSearchData(mSearchWord);

                KeyboardUtils.hideSoftInput(v, getContext());
                reactionToCover(false);
                return true;
            }
        });

        mEdSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int currentLength = s.toString().length();
                if (mLastSearchContentLength >= 1 && currentLength == 0 && mShowSearchToolbar) {
                    mSearchWord = "";
                    fetchSearchData(mSearchWord);
                }
                mLastSearchContentLength = currentLength;
            }
        });
    }

    /**
     * 点击搜索
     */
    protected void reactionToClickSearchAction() {
        mShowSearchToolbar = true;

        View childView = mRevealFrameLayout.getChildAt(0);
        childView.setVisibility(View.VISIBLE);
        childView.bringToFront();

        int centerX = childView.getRight();
        int centerY = childView.getBottom() / 2;
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(childView, centerX, centerY, 0, childView.getWidth());
        circularReveal.setDuration(300).setInterpolator(new LinearInterpolator());
        circularReveal.start();

        circularReveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                reactionToCover(true);

                mEdSearch.requestFocus();
                KeyboardUtils.showSoftInput(mEdSearch, getContext());
            }
        });
    }


    /**
     * 搜索栏的回退逻辑
     *
     * @return
     */
    private boolean reactionToSearchBack() {
        if (mShowSearchToolbar) {
            mShowSearchToolbar = false;
            KeyboardUtils.hideSoftInput(mEdSearch, getContext());

            View childView = mRevealFrameLayout.getChildAt(0);
            childView.bringToFront();

            int centerX = childView.getLeft();
            int centerY = childView.getBottom() / 2;
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(childView, centerX, centerY, 0, childView.getWidth());
            circularReveal.setDuration(300).setInterpolator(new DecelerateInterpolator());

            circularReveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    reactionToCover(false);
                }
            });
            circularReveal.start();

            if (mEdSearch != null) {
                mEdSearch.setText("");
                mSearchWord = "";
                fetchSearchData(mSearchWord);
            }

            return true;
        }
        return false;
    }

    /**
     * 带搜索关键词 请求获取数据
     */
    protected abstract void fetchSearchData(String searchWord);

    /**
     * 搜索覆盖物逻辑
     *
     * @param visibility
     */
    protected abstract void reactionToCover(boolean visibility);

}

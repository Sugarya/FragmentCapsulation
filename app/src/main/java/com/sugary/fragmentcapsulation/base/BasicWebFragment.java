package com.sugary.fragmentcapsulation.base;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sugary.fragmentcapsulation.R;
import com.sugary.fragmentcapsulation.utils.HomeConstants;
import com.sugary.fragmentcapsulation.utils.LOG;

import butterknife.BindView;



/**
 * Created by Ethan 2017/08/01
 * WebView 基类
 */
public class BasicWebFragment extends BasicFragment {

    private static final String TAG = "BasicWebFragment";

    @BindView(R.id.swipe_fragment_goods_detail)
    SwipeRefreshLayout mSwipeRefresh;

    @BindView(R.id.web_fragment_web_detail)
    WebView mWebView;

    private String mUrl = "";

    public static BasicWebFragment newInstance(String url) {
        BasicWebFragment fragment = new BasicWebFragment();
        Bundle args = new Bundle();
        args.putString(HomeConstants.ARGUMENT_KEY_BASIC_WEB_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void parseArguments(Bundle arguments) {
        super.parseArguments(arguments);
        mUrl = arguments.getString(HomeConstants.ARGUMENT_KEY_BASIC_WEB_URL, "");
        LOG.d(TAG, "url = " + mUrl);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        initSwipeRefreshLayout();
        initWebView();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl(mUrl);
            }
        });
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefresh.setColorSchemeColors(getResources().getColor(R.color.orange), getResources().getColor(R.color.yellow));
    }

    private void initWebView() {
        setupWebView(mWebView.getSettings());

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (mSwipeRefresh != null) {
                    mSwipeRefresh.setRefreshing(true);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                LOG.d(TAG, "newProgress=" + newProgress);
                if (newProgress >= 80 && mSwipeRefresh != null) {
                    mSwipeRefresh.setRefreshing(false);
                    mSwipeRefresh.setEnabled(false);
                }
            }
        });
    }

    private void setupWebView(WebSettings settings) {
        if (settings == null) {
            return;
        }

        settings.setAppCacheEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportZoom(true);
        settings.setDisplayZoomControls(true);
    }



    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_basic_web;
    }


}

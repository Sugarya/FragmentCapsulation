package com.sugary.fragmentcapsulation.base;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentTransactionBugFixHack;
import android.support.v7.app.AppCompatActivity;
import com.sugary.fragmentcapsulation.R;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.sugary.fragmentcapsulation.base.BasicActivity.LaunchMode.STANDARD;


/**
 * Created by Ethan on 2017/03/30.
 * 1.控件绑定  2. 提供替换fragment方法 show/replace,支持单例模式启动  3.管理fragment回退
 */
public abstract class BasicActivity extends AppCompatActivity {


    private static final String TAG = "BasicActivity";

    /**
     * Fragment回退管理的队列
     */
    private Deque<String> mFragmentBackDeque = new ArrayDeque<>();
    /**
     * 当前显示的Fragment
     */
    private Fragment mCurrentFragment;

    /**
     * 调用popBackStack系列方法，可通过设置此变量实现通信，为onSupportBackPressed()入参
     */
    private Bundle mSupportBackStackArguments;
    private Handler mHandler;
    private Unbinder mUnBinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResID());
        mUnBinder = ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAppAccountValid();
    }

    /**
     * 判断App账号有效性
     */
    private void checkAppAccountValid() {

    }

    @Override
    protected void onDestroy() {
        if(mUnBinder != null) {
            mUnBinder.unbind();
        }
        super.onDestroy();
    }


    public void replaceOneFragment(String fragmentTab) {
        replaceOneFragment(0, fragmentTab, null, true);
    }

    public void replaceOneFragment(String fragmentTab, Bundle bundle) {
        replaceOneFragment(0, fragmentTab, bundle, true);
    }

    public void replaceOneFragment(@IdRes int resId, String fragmentTab) {
        replaceOneFragment(resId, fragmentTab, null, true);
    }

    public void replaceOneFragment(@IdRes int resId, String fragmentTab, Bundle bundle) {
        replaceOneFragment(resId, fragmentTab, bundle, true);
    }

    public void replaceOneFragment(String fragmentTab, boolean isAddToBack) {
        replaceOneFragment(0, fragmentTab, null, isAddToBack);
    }

    public void replaceOneFragment(@IdRes int resId, String fragmentTab, boolean isAddToBack) {
        replaceOneFragment(resId, fragmentTab, null, isAddToBack);
    }

    public void replaceOneFragment(String fragmentTab, Bundle bundle, boolean isAddToBack) {
        replaceOneFragment(0, fragmentTab, bundle, isAddToBack);
    }

    /**
     * 替换当前Fragment里的某个FrameLayout布局
     * @param resId 被替换的布局ID
     * @param fragmentTab 新的Fragment名
     * @param arguments 传入新的Fragment的Bundle
     * @param isAddToBack 是否加入回退栈
     */
    private void replaceOneFragment(@IdRes int resId, String fragmentTab, Bundle arguments, boolean isAddToBack) {
        int childrenFragmentContainerResID = ((BasicFragment) mCurrentFragment).getChildrenFragmentContainerResID();
        int layoutId = resId <= 0 ? childrenFragmentContainerResID : resId;

        if (layoutId == -1) {
            throw new IllegalStateException("You should overwrite getChildrenFragmentContainerResID from BasicFragment");
        }

        FragmentManager manager = mCurrentFragment.getChildFragmentManager();
        if (manager != null) {
            FragmentTransaction transaction = manager.beginTransaction();

            transaction
                    .setCustomAnimations(R.anim.right_enter, R.anim.left_exit, R.anim.left_enter, R.anim.right_exit)
                    .replace(layoutId, fragmentProvider(fragmentTab, arguments), fragmentTab);
            if (isAddToBack) {
                transaction.addToBackStack(fragmentTab);
            }

            transaction.commitAllowingStateLoss();
        }
    }


    /**
     * 显示特定Tag的Fragment,如果是第一次显示,则新建并添加该Fragment
     *
     * @param fragmentTab
     */
    public void showOneFragment(String fragmentTab) {
        showOneFragment(fragmentTab, null, true, STANDARD,true);
    }

    /**
     * 显示特定Tag的Fragment,如果是第一次显示,则新建并添加该Fragment
     *
     * @param fragmentTab
     * @param isAddToStack 第一次显示时，是否加入回退栈
     */
    public void showOneFragment(String fragmentTab, boolean isAddToStack) {
        showOneFragment(fragmentTab, null, isAddToStack, STANDARD,true);
    }

    /**
     * 显示特定Tag的Fragment,如果是第一次显示,则新建并添加该Fragment
     *
     * @param fragmentTab
     * @param isAddToStack
     * @param launchMode
     * @param transitionAnimationEnable
     */
    public void showOneFragment(String fragmentTab, boolean isAddToStack,LaunchMode launchMode, boolean transitionAnimationEnable) {
        showOneFragment(fragmentTab, null, isAddToStack, launchMode,transitionAnimationEnable);
    }


    /**
     * 显示特定Tag的Fragment,如果是第一次显示,则新建并添加该Fragment
     *
     * @param fragmentTab
     * @param arguments
     */
    public void showOneFragment(String fragmentTab, Bundle arguments) {
        showOneFragment(fragmentTab, arguments, true, STANDARD,true);
    }

    public void showOneFragment(String fragmentTab, Bundle arguments, LaunchMode launchMode) {
        showOneFragment(fragmentTab, arguments, true, launchMode,true);
    }

    /**
     * 显示特定Tag的Fragment,如果是第一次显示,则新建并添加该Fragment
     *
     * @param fragmentTab    Fragment标签名
     * @param arguments      传入Fragment的参数Bundle
     * @param isAddBackStack 是否加入FragmentManager回退栈
     * @param launchMode     启动模式 分为： STANDARD，SINGLE，SINGLE_ENHANCEMENT
     */
    private void showOneFragment(String fragmentTab, Bundle arguments, boolean isAddBackStack, LaunchMode launchMode, boolean transitionAnimationEnable) {
        FragmentManager manager = getSupportFragmentManager();
        if (manager == null) {
            return;
        }

        Fragment fragmentByTag = manager.findFragmentByTag(fragmentTab);

        if (fragmentByTag != null && launchMode == LaunchMode.SINGLE_ENHANCEMENT) {
            popMultipleBackStack(fragmentTab, arguments);
            return;
        }

        FragmentTransaction transaction = manager.beginTransaction();

        //设置过渡动画
        if(transitionAnimationEnable) {
            transaction.setCustomAnimations(R.anim.right_enter, R.anim.left_exit, 0, 0);
        }

        //隐藏当前所有fragment
        List<Fragment> fragments = manager.getFragments();
        if (fragments != null && fragments.size() > 0) {
            for (Fragment f : fragments) {
                if (f != null) {
                    transaction.hide(f);
                }
            }
        }
        //第一次添加该Fragment
        if (fragmentByTag == null) {
            mCurrentFragment = fragmentProvider(fragmentTab, arguments);
            if(launchMode != LaunchMode.DEFAULT) {
                mFragmentBackDeque.push(fragmentTab);
            }
            transaction.add(getFragmentContainerResID(), mCurrentFragment, fragmentTab);
            if (isAddBackStack) {
                transaction.addToBackStack(fragmentTab);
            }
            transaction.commitAllowingStateLoss();
            return;
        }

        if (!(fragmentByTag instanceof BasicFragment)) {
            throw new ClassCastException("fragment must extends BasicFragment");
        }

        //更新Arguments，按后退键时Fragment里的后退方法里使用
        if (arguments != null) {
            setSupportBackStackArguments(arguments);
        }

        //根据启动模式类型，采取不同的方式维护后退栈
        switch (launchMode) {
            case STANDARD:
                mFragmentBackDeque.push(fragmentTab);
                break;
            case SINGLE:
                synchronizeFragmentBackDequeWhenSingleLaunchMode(fragmentTab);
                break;
        }

        BasicFragment basicFragment = (BasicFragment) fragmentByTag;
        mCurrentFragment = fragmentByTag;
        basicFragment.setSupportArguments(arguments);
        transaction.show(fragmentByTag);
        transaction.commitAllowingStateLoss();
    }


    /**
     * 获取要替换的布局ID
     *
     * @return fragment替换的布局ID
     */
    protected int getFragmentContainerResID() {
        return -1;
    }

    /**
     * 提供fragment
     * @param fragmentTab Fragment标签
     * @param arguments 传入Fragment的参数
     * @return
     */
    protected BasicFragment fragmentProvider(String fragmentTab, Bundle arguments) {
        return null;
    }

    /**
     * 返回键显示特定Tag的Fragment
     */
    private void showOneFragmentOnBackPressed() {
        mFragmentBackDeque.pop();
        String fragmentTab = mFragmentBackDeque.peek();

        FragmentManager manager = getSupportFragmentManager();
        if (manager != null) {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.left_enter, R.anim.right_exit, 0, 0);

            List<Fragment> fragments = manager.getFragments();
            for (Fragment f : fragments) {
                if (f != null) {
                    transaction.hide(f);
                }
            }
            Fragment fragmentByTag = manager.findFragmentByTag(fragmentTab);
            if (fragmentByTag != null) {
                mCurrentFragment = fragmentByTag;
                transaction.show(fragmentByTag);
            }

            transaction.commitAllowingStateLoss();
        }
    }

    /**
     * 一次弹出多个Fragment
     *
     * @param tag
     * @param popFlag 0 弹出不包括tag所指的Fragment；1 表示弹出包括当前tag的fragment
     * @param bundle  回退栈 传输参数
     */
    public void popMultipleBackStack(String tag, int popFlag, Bundle bundle) {
        if (bundle != null) {
            setSupportBackStackArguments(bundle);
        }
        //维护后退栈内容，保持同步
        if (mFragmentBackDeque.contains(tag)) {
            String peekElement = mFragmentBackDeque.peek();
            while (!tag.equals(peekElement)) {
                if (mFragmentBackDeque.isEmpty()) {
                    break;
                }
                mFragmentBackDeque.pop();
                peekElement = mFragmentBackDeque.peek();
            }

            if (popFlag == 1) {
                if (!mFragmentBackDeque.isEmpty()) {
                    mFragmentBackDeque.pop();
                }
            }
        }

        FragmentManager manager = getSupportFragmentManager();
        manager.popBackStackImmediate(tag, popFlag);
    }

    /**
     * 一次弹出多个Fragment
     *
     * @param tag
     * @param bundle 回退栈 传输参数
     */
    private void popMultipleBackStack(String tag, Bundle bundle) {
        if (bundle != null) {
            setSupportBackStackArguments(bundle);
        }
        synchronizeFragmentBackDequeWhenSingleLaunchMode(tag);

        FragmentManager manager = getSupportFragmentManager();
        manager.popBackStackImmediate(tag, 0);

        reorderAvailIndicesToFixBug();
    }

    /**
     * 修复Fragment出栈后，栈内顺序不正确的bug
     */
    private void reorderAvailIndicesToFixBug(){
        if(mHandler == null) {
            mHandler = new Handler(getMainLooper());
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                FragmentTransactionBugFixHack.reorderIndices(getSupportFragmentManager());
            }
        });
    }

    /**
     * 单例模式下，管理自维护的Fragment后退栈
     *
     * @param tag
     */
    private void synchronizeFragmentBackDequeWhenSingleLaunchMode(String tag) {
        if (mFragmentBackDeque.contains(tag)) {
            String peekElement = mFragmentBackDeque.peek();
            while (!tag.equals(peekElement)) {
                if (mFragmentBackDeque.isEmpty()) {
                    break;
                }
                mFragmentBackDeque.pop();
                peekElement = mFragmentBackDeque.peek();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mFragmentBackDeque == null || mCurrentFragment == null) {
            return;
        }

        //检查当前Fragment的ChildFragmentManager回退栈是否需要回退
        int childStackEntryCount = mCurrentFragment.getChildFragmentManager().getBackStackEntryCount();
        if (childStackEntryCount > 0) {
            mCurrentFragment.getChildFragmentManager().popBackStackImmediate();
            return;
        }

        //检查当前Fragment的自维护的回退栈是否需要回退
        if (mFragmentBackDeque.size() >= 2) {
            showOneFragmentOnBackPressed();
            return;
        }

        if(mCurrentFragment instanceof BasicFragment){
            BasicFragment basicFragment = (BasicFragment)mCurrentFragment;
            if(basicFragment.onBackPressed()){
                return;
            }
        }

        finish();
    }

    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }

    public Deque<String> getFragmentBackDeque() {
        return mFragmentBackDeque;
    }

    public Bundle getSupportBackStackArguments() {
        if (mSupportBackStackArguments == null) {
            mSupportBackStackArguments = new Bundle();
        }
        return mSupportBackStackArguments;
    }

    public void setSupportBackStackArguments(Bundle supportBackStackArguments) {
        this.mSupportBackStackArguments = supportBackStackArguments;
    }


    protected abstract
    @LayoutRes
    int getLayoutResID();


    /**
     * fragment 启动模式
     */
    public enum LaunchMode {

        /**
         * 默认 不记录回退记录
         */
        DEFAULT,
        /**
         * 标准模式
         */
        STANDARD,
        /**
         * 单例模式，其他Fragment从自维护的mFragmentBackDeque栈里退出
         */
        SINGLE,
        /**
         * 强化版单例模式，其他Fragment从FragmentManager栈和自维护的mFragmentBackDeque栈里退出
         */
        SINGLE_ENHANCEMENT,
    }
}

package com.sugary.fragmentcapsulation.rxlife;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;


/**
 * Created by Ethan 2017/09/09
 * Rx life of fragment
 */
public class RxSupportFragment extends Fragment {

    private BehaviorSubject<LifecycleEvent> mBehaviorSubject = BehaviorSubject.create();

    public RxSupportFragment() {
        // Required empty public constructor
    }

    /**
     * 绑定结束事件
     * @param endEvent 结束事件
     * @param <T>
     * @return
     */
    protected <T> Observable.Transformer<T, T> bindLifecycleEvent(final LifecycleEvent endEvent) {
        final Observable<LifecycleEvent> eventObservable = mBehaviorSubject.takeFirst(new Func1<LifecycleEvent, Boolean>() {
            @Override
            public Boolean call(LifecycleEvent event) {
                return event.equals(endEvent);
            }
        });
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> tObservable) {
                return tObservable.takeUntil(eventObservable);
            }
        };
    }









    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBehaviorSubject.onNext(LifecycleEvent.CREATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mBehaviorSubject.onNext(LifecycleEvent.CREATE_VIEW);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBehaviorSubject.onNext(LifecycleEvent.VIEW_CREATED);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            mBehaviorSubject.onNext(LifecycleEvent.HIDDEN);
        }else{
            mBehaviorSubject.onNext(LifecycleEvent.SHOW);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehaviorSubject.onNext(LifecycleEvent.START);
    }

    @Override
    public void onResume() {
        super.onResume();
        mBehaviorSubject.onNext(LifecycleEvent.RESUME);
    }

    @Override
    public void onPause() {
        super.onPause();
        mBehaviorSubject.onNext(LifecycleEvent.PAUSE);
    }

    @Override
    public void onStop() {
        super.onStop();
        mBehaviorSubject.onNext(LifecycleEvent.STOP);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBehaviorSubject.onNext(LifecycleEvent.DESTROY_VIEW);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBehaviorSubject.onNext(LifecycleEvent.DESTROY);
    }
}

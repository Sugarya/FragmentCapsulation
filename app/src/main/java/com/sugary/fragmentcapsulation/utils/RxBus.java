package com.sugary.fragmentcapsulation.utils;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Modify by Ethan on 17/01/25.
 * 线程安全的RxBus
 */
public class RxBus {
    private static volatile RxBus instance;
    private final Subject<Object, Object> _bus;

    /**
     * 用SerializedSubject包装PublishSubject，序列化
     */
    private RxBus() {
        //private final PublishSubject<Object> _bus = PublishSubject.create();

        // If multiple threads are going to emit events to this
        // then it must be made thread-safe like this instead
        _bus = new SerializedSubject<>(PublishSubject.create());
    }

    /**
     * RxBus实例(单例)
     * @return
     */
    public static RxBus getInstance() {
        if (null == instance) {
            synchronized (RxBus.class) {
                if (null == instance) {
                    instance = new RxBus();
                }
            }
        }
        return instance;
    }

    public void send(Object object) {
        try{
            _bus.onNext(object);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean hasObservers() {
        return _bus.hasObservers();
    }

    private <T> Observable<T> toObservable(final Class<T> type) {
        return _bus.ofType(type);
    }

    public <T> Subscription toSubscription(final Class<T> type, Observer<T> observer) {
        return toObservable(type).subscribe(observer);
    }

    public <T> Subscription toSubscription(final Class<T> type, Action1<T> action1) {
        return toObservable(type).subscribe(action1);
    }

    public <T> Subscription toSubscription(final Class<T> type, Action1<T> action1, Action1<Throwable> errorAction1) {
        return toObservable(type).subscribe(action1,errorAction1);
    }

    public <T> Subscription toSubscription(final Class<T> type, Action1<T> action1, Action1<Throwable> errorAction1, Action0 onCompleted) {
        return toObservable(type).subscribe(action1, errorAction1, onCompleted);
    }

}

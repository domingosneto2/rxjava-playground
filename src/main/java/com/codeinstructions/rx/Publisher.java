package com.codeinstructions.rx;

import com.codeinstructions.log.Log;
import rx.Observable;
import rx.Subscriber;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Domingos on 12/11/2014.
 */
public class Publisher<T> {
    private Observable<T> observable;

    final AtomicLong requested = new AtomicLong(0);

    private Subscriber<? super T> subscriber;

    private boolean unsubscribed = false;

    public Publisher() {
        observable = Observable.create(s -> {
            s.setProducer(n -> {
                Log.log("Requested " + n);
                synchronized (requested) {
                    long newN = requested.addAndGet(n);
                    Log.log("New n: " + newN);
                    requested.notifyAll();
                }
                unsubscribed = s.isUnsubscribed();
            });
            subscriber = s;
        });
    }

    public boolean publish(T value){
        synchronized (requested) {
            while (requested.get() == 0) {
                Log.log("Zero requested.  Waiting...");
                if (unsubscribed) {
                    return false;
                }
                try {
                    requested.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            Log.log("Publishing " + value);
            subscriber.onNext(value);
            requested.decrementAndGet();
            return !unsubscribed;
        }
    }

    public Observable<T> getObservable() {
        return observable;
    }

    public void finish() {
        subscriber.onCompleted();
    }
}

package com.codeinstructions.rx;

import rx.Observable;
import rx.Subscriber;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

public class Publisher<T> {
    private Observable<T> observable;

    final AtomicLong requested = new AtomicLong(0);

    private Subscriber<? super T> subscriber;

    private volatile boolean unsubscribed = false;

    public Publisher() {
        observable = Observable.create(s -> {
            s.setProducer(n -> {
                println("Requested " + n);
                synchronized (requested) {
                    long oldN = requested.get();
                    long newN = Math.max(oldN, n);
                    if (newN != oldN) {
                        requested.set(newN);
                    }
                    println("New n: " + newN);
                    if (oldN == 0) {
                        // requested was previously zero.  The publishing
                        // thread might be blocked.  Lets update the values
                        // of the shared variables and wake the thread.
                        unsubscribed = s.isUnsubscribed();
                        if (unsubscribed) {
                            requested.set(0);
                        }
                        requested.notify();
                    }
                }
            });
            subscriber = s;
        });
    }

    public boolean publish(T value){
        synchronized (requested) {
            while (requested.get() == 0) {
                println("Zero requested.  Waiting...");
                if (unsubscribed) {
                    return false;
                }
                try {
                    requested.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            println("Publishing " + value);
            subscriber.onNext(value);
            requested.decrementAndGet();
            return !unsubscribed;
        }
    }

    public void onCompleted() {
        subscriber.onCompleted();
    }

    public void onError(Throwable t) {
        subscriber.onError(t);
    }

    public Observable<T> getObservable() {
        return observable;
    }

    private static void println(String str) {
        System.out.println(label() + str);
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");

    private static String time() {
        return sdf.format(new Date(System.currentTimeMillis()));
    }

    private static String label() {
        return time() + " [" + Thread.currentThread().getName() + "]: ";
    }
}


package com.codeinstructions.rx;

import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This example has an Observable implementation that handles recursive
 * calls properly.
 */
public class Test11 {
    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(1);
        testingSource(0, 2_000)
                .subscribeOn(Schedulers.io())
                .buffer(100)
                .flatMap(l -> Observable.from(l)
                                .observeOn(Schedulers.computation())
                                .doOnNext(Test11::consume)
                )
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {
                        latch.countDown();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        latch.countDown();
                    }

                    @Override
                    public void onNext(Integer integer) {
                    }
                });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static Observable<Integer> testingSource(int min, int max) {
        return Observable.create(subscriber -> {
            AtomicInteger ai = new AtomicInteger(min);
            AtomicLong requested = new AtomicLong(0);
            subscriber.setProducer(n -> {
                println("Requested " + n);
                long newR = requested.addAndGet(n);
                if (newR != n) {
                    // Avoid recursive call.
                    return;
                }
                for (int i = 0; i < n; i++) {
                    if (subscriber.isUnsubscribed()) {
                        break;
                    }
                    if (ai.get() == max) {
                        subscriber.onCompleted();
                        break;
                    }
                    println("Emitting " + ai.get());
                    subscriber.onNext(ai.getAndAdd(1));
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                requested.set(0);
            });
        });
    }

    private static void consume(int i) {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        println(i);
    }

    private static void println(int i) {
        System.out.println(label() + i);
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

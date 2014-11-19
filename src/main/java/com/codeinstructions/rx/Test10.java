
package com.codeinstructions.rx;

import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This is an example on how we may get a StackOverflowError if we
 * don't handle recursive calls in the producer's request() method.
 * We removed the call to buffer() on purpose to cause the StackOverflowError.
 */
public class Test10 {
    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(1);
        testingSource(0, 2_000)
                .subscribeOn(Schedulers.io())
                .flatMap(v -> Observable.just(v)
                                .observeOn(Schedulers.computation())
                                .doOnNext(i -> consume(i))
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
            subscriber.setProducer(n -> {
                println("Requested " + n);
                if (subscriber.isUnsubscribed()) {
                    return;
                }
                println("Emitting " + ai.get());
                subscriber.onNext(ai.getAndAdd(1));
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!subscriber.isUnsubscribed() && ai.get() == max) {
                    subscriber.onCompleted();
                }
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

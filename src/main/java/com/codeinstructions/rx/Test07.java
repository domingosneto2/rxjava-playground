
package com.codeinstructions.rx;

import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class Test07 {
    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(1);
        testingSource(0, 10000)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
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
                        consume(integer);
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
            for (int i = min; i < max; i++) {
                if (subscriber.isUnsubscribed()) {
                    break;
                }

                println("Emitting " + i);
                subscriber.onNext(i);
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (!subscriber.isUnsubscribed()) {
                subscriber.onCompleted();
            }
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

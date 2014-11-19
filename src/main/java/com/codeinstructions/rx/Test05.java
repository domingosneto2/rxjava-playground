package com.codeinstructions.rx;

import rx.Observable;
import rx.schedulers.Schedulers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * Here we extend the previous example by using a CountDownLatch to prevent the main
 * thread from terminating before the subscription completes.
 */
public class Test05 {
    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(1);
        testingSource(0, 100)
                .subscribeOn(Schedulers.io())
                .doOnCompleted(latch::countDown)
                .subscribe(Test05::println);

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
            }
            if (!subscriber.isUnsubscribed()) {
                subscriber.onCompleted();
            }
        });
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

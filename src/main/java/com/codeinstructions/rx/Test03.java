package com.codeinstructions.rx;

import rx.Observable;
import rx.Subscriber;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Test03 {
    public static void main(String[] args) {
        testingSource(0, 100).subscribe(Test03::println);
    }

    private static Observable<Integer> testingSource(int min, int max) {
        return Observable.create(subscriber -> {
            for (int i = min; i < max; i++) {
                if (subscriber.isUnsubscribed()) {
                    break;
                }
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

    private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");

    private static String time() {
        return sdf.format(new Date(System.currentTimeMillis()));
    }

    private static String label() {
        return time() + " [" + Thread.currentThread().getName() + "]: ";
    }
}

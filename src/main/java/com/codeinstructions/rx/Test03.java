package com.codeinstructions.rx;

import rx.Observable;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Now we've swapped the Observable implementation with our own.
 *
 * The Observable.create() method takes as a parameter a function that
 * receives a Subscriber.  This observable implementation doesn't handle
 * backpressure. This, however, is not a problem yet since we are
 * subscribing synchronously in the same thread where the observable runs.
 */
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


package com.codeinstructions.rx;

import rx.Observable;
import rx.schedulers.Schedulers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * Now we run the Observable on the io Scheduler and the subscriber
 * in the computation scheduler.  Now we have asynchronous execution,
 * that is, the observable's call to onNext() doesn't have to wait for
 * the subscriber's callback tot run.
 *
 * As we will see later, this may cause problems with backpressure.
 * If the observable emits items faster than the subscriber can process
 * them, an exception will be thrown.
 */
public class Test06 {
    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(1);
        testingSource(0, 100)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .doOnCompleted(latch::countDown)
                .subscribe(Test06::println);

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

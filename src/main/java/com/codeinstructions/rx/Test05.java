package com.codeinstructions.rx;

import com.codeinstructions.log.Log;
import rx.Observable;
import rx.schedulers.Schedulers;

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
                .subscribe(Log::log);

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
                Log.log("Emitting " + i);
                subscriber.onNext(i);
            }
            if (!subscriber.isUnsubscribed()) {
                subscriber.onCompleted();
            }
        });
    }
}

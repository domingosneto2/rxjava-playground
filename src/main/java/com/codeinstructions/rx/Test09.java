
package com.codeinstructions.rx;

import com.codeinstructions.log.Log;
import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Here we use flatMap() to split the subscription among multiple threads.
 * It is important to use buffer() so that each unit of work contains
 * many grouped items, otherwise performance will suffer.
 */
public class Test09 {
    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(1);
        testingSource(0, 2_000)
                .subscribeOn(Schedulers.io())
                .buffer(100)
                .flatMap(l -> Observable.from(l)
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
                        // No real work.  The actual work is being
                        // performend on doOnNext() inside the flatMap() call.
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
                Log.log("Requested " + n);
                for (int i = 0; i < n; i++) {
                    if (subscriber.isUnsubscribed()) {
                        return;
                    }
                    if (ai.get() == max) {
                        subscriber.onCompleted();
                        return;
                    }
                    Log.log("Emitting " + ai.get());
                    subscriber.onNext(ai.getAndAdd(1));
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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
        Log.log(i);
    }
}

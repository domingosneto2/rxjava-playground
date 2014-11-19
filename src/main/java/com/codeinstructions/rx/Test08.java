
package com.codeinstructions.rx;

import com.codeinstructions.log.Log;
import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In this example we replace the Observable code with an implementation
 * that handles backpressure.  Instead of running the generation loop in
 * the OnSubscribe.call() method, we pass a Producer to the subscriber.
 * The Producer has a request(long n) method, so that the subscriber
 * can manage the rate at which the Observable generates items.
 *
 * This implementation doesn't follow the specification though: as we
 * will see in a future example, it is possible that the call to onNext()
 * in the producer ends up calling request() again.  That may result in
 * a StackOverflowError if not handled properly.
 */
public class Test08 {
    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(1);
        testingSource(0, 2_000)
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
            AtomicInteger ai = new AtomicInteger(0);
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

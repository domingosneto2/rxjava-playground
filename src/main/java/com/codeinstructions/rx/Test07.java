
package com.codeinstructions.rx;

import com.codeinstructions.log.Log;
import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;

import java.util.concurrent.CountDownLatch;

/**
 * Here we simulate an observable that emits items twice as fast as
 * the subscriber can process them.  If you run this sample, you will
 * see that after a while a MissingBackpressureException is thrown.
 * This happens because the Observable and the observer run on
 * different threads.
 *
 * We also changed the subscribe() call and now pass a full blown
 * Observer object, where we implement the onNext(), onError() and
 * onComplete() methods.
 */
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

                Log.log("Emitting " + i);
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
        Log.log(i);
    }
}

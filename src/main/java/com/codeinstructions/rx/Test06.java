
package com.codeinstructions.rx;

import com.codeinstructions.log.Log;
import rx.Observable;
import rx.schedulers.Schedulers;

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

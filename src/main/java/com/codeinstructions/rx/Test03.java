package com.codeinstructions.rx;

import com.codeinstructions.log.Log;
import rx.Observable;

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
        testingSource(0, 100).subscribe(Log::log);
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

package com.codeinstructions.rx;

import com.codeinstructions.log.Log;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Here we specify the Scheduler where the OnSubscribe.call() method will run
 * (this is the method passed to Observable.create()).  The effect is tot have
 * the Observer running in the specified thread.
 *
 * If you run this example, you will notice that the Observer emits the firs few
 * numbers and then the program terminates.  This is because the Observer and
 * the Subscription run on a computation thread, and the main thread terminates
 * right after the subscription thread kicks off.
 */
public class Test04{
    public static void main(String[] args) {
        testingSource(0, 100)
                .subscribeOn(Schedulers.computation())
                .subscribe(Log::log);
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

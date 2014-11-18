package com.codeinstructions.rx.old;

import com.codeinstructions.threading.ThreadUtil;
import rx.Observable;
import rx.Producer;
import rx.schedulers.Schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This example shows a simple observer / subscriber pair.
 *
 * In this example, even though we give 5 threads to the observer pool
 */
public class Test09 {
    public static void main(String[] args) {
        ExecutorService observerPool = Executors.newFixedThreadPool(5);
        ExecutorService subscriberPool = Executors.newFixedThreadPool(1);
        testingSource()
                .subscribeOn(Schedulers.from(subscriberPool))
                .observeOn(Schedulers.from(observerPool))
                .take(100)
                .doOnCompleted(() -> {
                    observerPool.shutdown();
                    subscriberPool.shutdown();
                })
                .subscribe(i -> {
                    log("Processing " + i);
                    randomSleep(20, 20);
                    log("Processed " + i);
                });
    }

    private static Observable<Integer> testingSource() {
        return Observable.create(s -> {
            s.setProducer(new Producer() {
                int v = 0;

                @Override
                public void request(long n) {
                    log("Requested " + n);
                    randomSleep(5, 5);
                    if (!s.isUnsubscribed()) {
                        log("Emitting " + v);
                        s.onNext(v++);
                    }
                    v++;
                }
            });
        });
    }

    private static void randomSleep(int base, int factor) {
        ThreadUtil.sleep(base + (int)(factor * Math.random()));
    }

    private static void log(String message) {
        System.out.println(label() + message);
    }

    private static String label() {
        return System.currentTimeMillis() + " - " + ThreadUtil.getThreadName() + ": ";
    }
}

package com.codeinstructions.rx.old;

import com.codeinstructions.threading.ThreadUtil;
import rx.Observable;
import rx.Producer;
import rx.schedulers.Schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Domingos on 11/11/2014.
 */
public class Test01 {
    public static void main(String[] args) {
        ExecutorService subscriberPool = Executors.newFixedThreadPool(5);
        ExecutorService observerPool = Executors.newFixedThreadPool(5);
        testingSource()
                .take(10)
                .subscribeOn(Schedulers.from(subscriberPool))
                .observeOn(Schedulers.from(observerPool))
                .doOnCompleted(() -> {
                    subscriberPool.shutdown();
                    observerPool.shutdown();
                })
                .forEach(i -> {
                    log("Processing " + i);
                    randomSleep(2000, 2000);
                    log("Processed " + i);
                });
    }

    private static Observable<Integer> testingSource() {
//        ExecutorService subscriberPool = Executors.newFixedThreadPool(5);
        return Observable.create(s -> {
            s.setProducer(new Producer() {
                int v = 0;

                @Override
                public void request(long n) {
                    log("Requested " + n);
                    for (int i = 0; i < n; i++) {
                        log("Generating " + v);
                        randomSleep(500, 500);
                        log("Generated " + v);
                        log("Emitting " + v);
                        if (!s.isUnsubscribed()) {
                            s.onNext(v);
                        }
                        log("Emitted " + v);
                        v++;
                    }
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

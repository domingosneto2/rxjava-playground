package com.codeinstructions.rx.old;

import com.codeinstructions.threading.ThreadUtil;
import rx.Observable;
import rx.Producer;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This example shows how a StackOverflow can happen if you are not careful about
 * avoiding recursive calls on the Producer.request() method.
 */
public class Test10 {
    public static void main(String[] args) {
        ExecutorService observerPool = Executors.newFixedThreadPool(5);
        Scheduler scheduler = Schedulers.from(observerPool);
        ExecutorService subscriberPool = Executors.newFixedThreadPool(1);
        testingSource()
                .subscribeOn(Schedulers.from(subscriberPool))
                //.take(1000)
                //.observeOn(scheduler)
                .doOnCompleted(() -> {
                    observerPool.shutdown();
                    subscriberPool.shutdown();
                })
                //.buffer(10)
                .flatMap(i -> Observable.just(i)
                        .observeOn(scheduler)
                        .doOnNext(i2 -> {
                            log("Processing " + i2);
                            randomSleep(20, 20);
                            log("Processed " + i2);
                        }))
                .subscribe();
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

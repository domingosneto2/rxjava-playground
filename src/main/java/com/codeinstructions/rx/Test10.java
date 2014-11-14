package com.codeinstructions.rx;

import com.codeinstructions.threading.ThreadUtil;
import rx.Observable;
import rx.Producer;
import rx.schedulers.Schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Domingos on 11/11/2014.
 */
public class Test10 {
    public static void main(String[] args) {
        ExecutorService observerPool = Executors.newFixedThreadPool(5);
        ExecutorService subscriberPool = Executors.newFixedThreadPool(1);
        testingSource()
                .subscribeOn(Schedulers.from(subscriberPool))
                .take(10)
                .doOnCompleted(() -> {
                    observerPool.shutdown();
                    subscriberPool.shutdown();
                })
                .flatMap(i -> Observable.just(i)
                        .observeOn(Schedulers.from(observerPool))
                        .doOnNext(i2 -> {
                            log("Processing " + i2);
                            randomSleep(200, 200);
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
                    randomSleep(50, 50);
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

package com.codeinstructions.rx.old;

import com.codeinstructions.log.Log;
import com.codeinstructions.rx.Publisher;
import com.codeinstructions.threading.ThreadUtil;
import rx.Observable;
import rx.Producer;
import rx.schedulers.Schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Domingos on 11/11/2014.
 */
public class Test13 {
    public static void main(String[] args) {
        ExecutorService observerPool = Executors.newFixedThreadPool(4);
        testingSource()
                .take(10)
                .doOnCompleted(() -> {
                    observerPool.shutdown();
                })
                .flatMap(i -> {
                    return Observable.just(i)
                            .observeOn(Schedulers.from(observerPool))
                            .doOnNext(i2 -> {
                                Log.log("Processing " + i2);
                                randomSleep(20, 20);
                                Log.log("Processed " + i2);
                            });
                })
                .subscribe();
    }

    private static Observable<Integer> testingSource() {
        Publisher<Integer> p = new Publisher<>();
        Runnable r = () -> {
            int i = 0;
            while(true) {
                randomSleep(5, 5);
                if (!p.publish(i++)) {
                    Log.log("Finishing...");
                    p.onCompleted();
                    return;
                }
            }
        };

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(r);
        return p.getObservable();
    }

    private static void randomSleep(int base, int factor) {
        ThreadUtil.sleep(base + (int)(factor * Math.random()));
    }
}

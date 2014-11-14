package com.codeinstructions.rx;

import com.codeinstructions.log.Log;
import com.codeinstructions.threading.ThreadUtil;
import rx.Observable;
import rx.Producer;
import rx.schedulers.Schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Domingos on 11/11/2014.
 */
public class Test11 {
    public static void main(String[] args) {
        ExecutorService observerPool = Executors.newFixedThreadPool(1);
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
                                randomSleep(2000, 2000);
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
                randomSleep(500, 500);
                if (!p.publish(i++)) {
                    Log.log("Finishing...");
                    p.finish();
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

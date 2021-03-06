
package com.codeinstructions.rx;

import com.codeinstructions.log.Log;
import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This example introduces the Publisher class, that allows a loop based generator
 * to publish to an observable.
 */
public class Test12 {
    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(1);
        testingSource(0, 2_000)
                .buffer(100)
                .flatMap(l -> Observable.from(l)
                                .observeOn(Schedulers.computation())
                                .doOnNext(Test12::consume)
                )
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
                    }
                });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static Observable<Integer> testingSource(int min, int max) {
        Publisher<Integer> publisher = new Publisher<>();
        Runnable r = () -> {
            for (int i = min; i < max; i++) {
                if (!publisher.publish(i)) {
                    return;
                }
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            publisher.onCompleted();
        };
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(r);
        return publisher.getObservable();
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

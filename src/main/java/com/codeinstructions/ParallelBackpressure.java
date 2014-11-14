package com.codeinstructions;

import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.concurrent.ForkJoinPool;

public class ParallelBackpressure {
    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();
        getPrimesObservable2()
                .take(100)
                .count()
        .subscribe(i -> System.out.println(Thread.currentThread().getName() + ": " + i));
        long end = System.currentTimeMillis() - start;
        System.out.println(end);
    }

    private static void printError(Throwable t) {
        System.err.println(Thread.currentThread().getName());
        t.printStackTrace();
    }

    private static Observable<Integer> getPrimesObservable() {
        return getNumbersObservable()
                //.filter(ParallelBackpressure::isPrime)
                //.filter(i -> {
                //    int sqrt = (int) Math.sqrt(i - 1);
                //    return sqrt * sqrt == i - 1;
                //});
                .flatMap(i -> Observable
                        .just(i)
                        .subscribeOn(Schedulers.from(ForkJoinPool.commonPool()))
                        .filter(ParallelBackpressure::isPrime)
                        .filter(i2 -> {
                                int sqrt = (int) Math.sqrt(i2 - 1);
                                return sqrt * sqrt == i2 - 1;
                            }));
    }

    private static Observable<Integer> getPrimesObservable2() {
        return getNumbersObservable()
                .filter(ParallelBackpressure::isPrime)
                .filter(i -> {
                    int sqrt = (int) Math.sqrt(i - 1);
                    return sqrt * sqrt == i - 1;
                });
    }

    private static Observable<Integer> getNumbersObservable() {
        Observable<Integer> observable = Observable.create(s -> {
            System.out.println(Thread.currentThread().getName());
            int i = 1;
            while (!s.isUnsubscribed()) {
                s.onNext(i);
                i++;
            }
            System.out.println(Thread.currentThread().getName() + ": unsubscribed");
            s.onCompleted();
        });
        //return observable.onBackpressureDrop();
        return observable;
    }

    public static boolean isPrime(int n) {
        if (n < 2) {
            return false;
        }
        int root = (int) Math.sqrt(n);

        for (int i = 2; i <= root; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}

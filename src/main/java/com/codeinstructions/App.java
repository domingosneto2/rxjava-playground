package com.codeinstructions;

import rx.Observable;

import java.util.concurrent.atomic.AtomicInteger;

public class App {
    public static void main(String[] args) throws InterruptedException {
        getPrimesObservable()
                .doOnError(App::printError)
                .onBackpressureDrop()
                .doOnError(App::printError)
                .subscribe(i -> System.out.println(Thread.currentThread().getName() + ": " + i));
    }

    private static Observable<Integer> getPrimesObservable() {
        return getNumbersObservable()
                .filter(App::isPrime)
                .doOnError(App::printError);
    }

    private static void printError(Throwable t) {
        System.err.println(Thread.currentThread().getName());
        t.printStackTrace();
    }

    private static Observable<Integer> getBackpressureNumbersObservable() {
        final AtomicInteger value = new AtomicInteger(1);
        return Observable.create(s -> s.setProducer(n -> {
            int v = value.get();
            for (int i = value.get(); i < v + n; i++) {
                if (s.isUnsubscribed()) {
                    value.set(i);
                }
                s.onNext(i);
            }
            value.set(v + (int)n);
        }));
    }

    private static Observable<Integer> getThreadedNumbersObservable() {

        Observable<Integer> observable = Observable.create(s -> new Thread(() -> {
            System.out.println(Thread.currentThread().getName());
            int i = 1;
            while (!s.isUnsubscribed()) {
                //System.out.println(Thread.currentThread().getName() + ": generating " + i);
                s.onNext(i);
                i++;
            }
            System.out.println(Thread.currentThread().getName() + ": unsubscribed");
            s.onCompleted();
        }).start());
        return observable.onBackpressureBuffer();
    }

    private static Observable<Integer> getNumbersObservable() {
        Observable<Integer> observable = Observable.create(s -> {
            int i = 1;
            while (!s.isUnsubscribed()) {
                s.onNext(i);
                i++;
            }
            s.onCompleted();
        });
        return observable.onBackpressureDrop();
    }

    private static Observable<Integer> getDefaultNumbersObservable() {
        return Observable.range(1, Integer.MAX_VALUE);
    }

    private static boolean isPrime(int n) {
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

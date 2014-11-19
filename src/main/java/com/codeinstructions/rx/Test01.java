package com.codeinstructions.rx;

import rx.Observable;

/**
 * In this example we create an observable that emits 100 integers.  We then subscribe to that observable
 * and invoke the println method of System.out to print each emitted integer.
 *
 * Notice that this is a reactive construct: we invoke print as a response to the emission of integers
 * by the observable.
 */
public class Test01 {
    public static void main(String[] args) {
        Observable.range(0, 100).subscribe(System.out::println);
    }
}

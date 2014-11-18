package com.codeinstructions.rx;

import rx.Observable;

/**
 * Created by Domingos on 17/11/2014.
 */
public class Test01 {
    public static void main(String[] args) {
        Observable.range(0, 100).subscribe(System.out::println);
    }
}

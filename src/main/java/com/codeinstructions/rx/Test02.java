package com.codeinstructions.rx;

import rx.Observable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Test02 {
    public static void main(String[] args) {
        Observable.range(0, 100).subscribe(Test02::println);
    }

    private static void println(int i) {
        System.out.println(label() + i);
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");

    private static String time() {
        return sdf.format(new Date(System.currentTimeMillis()));
    }

    private static String label() {
        return time() + " [" + Thread.currentThread().getName() + "]: ";
    }
}

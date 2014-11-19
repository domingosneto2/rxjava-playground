package com.codeinstructions.threading;

public class ThreadUtil {
    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {

        }
    }

    public static String getThreadName() {
        return Thread.currentThread().getName();
    }
}

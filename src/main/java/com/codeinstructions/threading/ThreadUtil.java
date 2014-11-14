package com.codeinstructions.threading;

/**
 * Created by Domingos on 11/11/2014.
 */
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

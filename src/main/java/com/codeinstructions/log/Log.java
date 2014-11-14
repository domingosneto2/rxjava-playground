package com.codeinstructions.log;

import com.codeinstructions.threading.ThreadUtil;

/**
 * Created by Domingos on 12/11/2014.
 */
public class Log {
    public static void log(String message) {
        System.out.println(label() + message);
    }

    private static String label() {
        return System.currentTimeMillis() + " - " + ThreadUtil.getThreadName() + ": ";
    }
}

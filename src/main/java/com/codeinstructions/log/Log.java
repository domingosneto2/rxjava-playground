package com.codeinstructions.log;

import com.codeinstructions.threading.ThreadUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
    public static void log(String message) {
        System.out.println(label() + message);
    }

    public static void log(int i) {
        System.out.println(label() + i);
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");

    private static String time() {
        return sdf.format(new Date(System.currentTimeMillis()));
    }

    private static String label() {
        return time() + " [" + ThreadUtil.getThreadName() + "]: ";
    }
}

package com.codeinstructions.rx;

import com.codeinstructions.log.Log;
import rx.Observable;

/**
 * Here we extend our example to print the name of the thread where the subscription callback runs.
 * Notice how the subscription is running in the main thread.
 */
public class Test02 {
    public static void main(String[] args) {
        Observable.range(0, 100).subscribe(Log::log);
    }
}

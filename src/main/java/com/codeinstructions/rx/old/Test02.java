package com.codeinstructions.rx.old;

import com.codeinstructions.threading.ThreadUtil;
import rx.Observable;
import rx.Producer;

/**
 * Created by Domingos on 11/11/2014.
 */
public class Test02 {
    public static void main(String[] args) {
        testingSource()
                .take(10)
                .subscribe();
    }

    private static Observable<Integer> testingSource() {
        return Observable.create(s -> {
            s.setProducer(new Producer() {
                int v = 0;

                @Override
                public void request(long n) {
                    log("Requested " + n);
                    for (int i = 0; i < n; i++) {
                        log("Generating " + v);
                        randomSleep(500, 500);
                        log("Generated " + v);
                        log("Emitting " + v);
                        if (!s.isUnsubscribed()) {
                            s.onNext(v);
                        }
                        log("Emitted " + v);
                        v++;
                    }
                }
            });

        });
    }

    private static void randomSleep(int base, int factor) {
        ThreadUtil.sleep(base + (int)(factor * Math.random()));
    }

    private static void log(String message) {
        System.out.println(label() + message);
    }

    private static String label() {
        return System.currentTimeMillis() + " - " + ThreadUtil.getThreadName() + ": ";
    }
}

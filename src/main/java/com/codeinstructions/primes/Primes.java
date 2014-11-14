package com.codeinstructions.primes;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static java.util.stream.Collectors.toList;

/**
 * Created by Domingos on 08/11/2014.
 */
public class Primes {
    public static List<Long> simpleSieve(int n) {
        System.out.println("Allocating...");
        long start = System.currentTimeMillis();
        int[] sieve = new int[n/2 - 1];
        long duration = System.currentTimeMillis() - start;
        System.out.println("Allocation took " + duration);
        int cur = 0;
        int step = cur * 2 + 3;
        int maxTest = (int) Math.sqrt(n);
        while (step <= maxTest) {
            int i = cur + step;
            while (i < sieve.length) {
                sieve[i] = 1;
                i += step;
            }
            cur++;
            while (sieve[cur] == 1) {
                cur++;
            }
            step = cur * 2 + 3;
        }
        return
                LongStream
                        .concat(
                                LongStream.of(2l),
                                IntStream.range(0, sieve.length)
                                        .filter(i -> sieve[i] == 0)
                                        .asLongStream()
                                        .map(i -> i * 2 + 3)
                        )
                        .boxed()
                        .collect(toList());

    }

    public static List<Long> blockSieve(List<Long> basePrimes, long pstart, int size) {
        int[] list = new int[(int)(size / 2L)];
        long start = pstart % 2 == 0 ? pstart + 1 : pstart;

        for (Long prime : basePrimes) {
            if (prime == 2) {
                continue;
            }
            // start index
            long n = prime * (start / prime);
            if (n < start) {
                n += prime;
                if (n % 2 == 0) {
                    n += prime;
                }
            }
            long index = (n - start) / 2L;
            while (index < list.length) {
                list[(int)index] = 1;
                index += prime;
            }
        }

        List<Long> result = IntStream
                .range(0, list.length)
                .filter(i -> list[i] == 0)
                .asLongStream()
                .map(i -> start + i * 2)
                .boxed()
                .collect(toList());
        return result;
    }

    public static boolean isPrime(int n) {
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

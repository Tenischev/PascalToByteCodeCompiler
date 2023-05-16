package ru.ifmo.ctddev.tenishchev.compiler;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by kris13 on 04.06.17.
 */
public class Prover {
    private static final int SIZE = 200000;

    public static void main(String[] args) throws IOException, InterruptedException {
        number();
        runnerFpc();
        runnerClass();
    }

    private static void runnerFpc() throws IOException, InterruptedException {
        double start = System.currentTimeMillis();
        Process p = Runtime.getRuntime().exec("./SimpleNumber");
        p.waitFor();
        InputStream is = p.getInputStream();
        int c;
        while ((c = is.read()) != -1) {
            System.out.print((char) c);
        }
        System.out.println();
        System.out.println((System.currentTimeMillis() - start) / 1000.0);
    }

    private static void runnerClass() throws IOException, InterruptedException {
        double start = System.currentTimeMillis();
        Process p = Runtime.getRuntime().exec("java Simple");
        p.waitFor();
        InputStream is = p.getInputStream();
        int c;
        while ((c = is.read()) != -1) {
            System.out.print((char) c);
        }
        System.out.println((System.currentTimeMillis() - start) / 1000.0);
    }

    private static void number() {
        boolean[] mask = new boolean[SIZE];
        int count = 0;
        for (int i = 2; i < SIZE; i++) {
            if (!mask[i]) {
                for (int j = i + i; j < SIZE; j += i) {
                    mask[j] = true;
                }
                count++;
                if (count == 10000) {
                    System.out.println(i);
                    return;
                }
            }
        }
    }
}

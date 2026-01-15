package com.me.coresmodule.utils;

public class Triple<T, S, U> {
    public T first;
    public S second;
    public U third;

    public Triple(T first, S second, U third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }
}

package com.chapter15;

import net.jcip.annotations.Immutable;

import java.util.concurrent.atomic.AtomicReference;

public class CASNumberRange {
    @Immutable
    private static class IntPair{
        final int lower; //Invariant: lower <= upper
        final int upper;
        private IntPair(int lower, int upper) {
            this.lower = lower;
            this.upper = upper;
        }
    }

    private final AtomicReference<IntPair> values = new AtomicReference<>(new IntPair(0, 0));

    public int getLower(){
        return values.get().lower;
    }

    public int getUpper(){
        return values.get().upper;
    }

    public void setLower(int i){
        while (true){
            IntPair oldV = values.get();
            if (i > oldV.upper)
                throw new IllegalArgumentException("Can't set lower to " + i + " > upper");
            IntPair newV = new IntPair(i, oldV.upper);
            if (values.compareAndSet(oldV, newV))
                return;
        }
    }

    /**
     * similar for setUpper()
     */
}

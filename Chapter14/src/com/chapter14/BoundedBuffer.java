package com.chapter14;

public class BoundedBuffer<V> extends BaseBoundedBuffer<V>{
    /**
     * CONDITION PREDICATE: not-full (!isFull())
     * CONDITION PREDICATE: not-empty !isEmpty())
     */

    public BoundedBuffer(int size){
        super(size);
    }

    //blocks until: not-full
    //Using conditional notification in BoundedBuffer.put
    public synchronized void put(V v)throws InterruptedException{
        while (isFull())
            wait();
        boolean wasEmpty = isEmpty();
        doPut(v);
        if (wasEmpty) {
            notifyAll();
        }
    }

    //blocks until: not-empty
    public synchronized V take() throws InterruptedException{
        while (isEmpty())
            wait();
        V v = doTake();
        notify();
        return v;
    }
}

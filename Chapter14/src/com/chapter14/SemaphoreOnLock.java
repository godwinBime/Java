package com.chapter14;

import net.jcip.annotations.GuardedBy;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Counting Semaphore implemented using Lock
 */
public class SemaphoreOnLock {
    private final Lock lock = new ReentrantLock();

    //CONDITION-PREDICATE: permitsAvailable(permits > 0)
    private final Condition permitsAvailable = lock.newCondition();

    @GuardedBy("lock")
    private int permits;

    SemaphoreOnLock(int initialPermits){
        lock.lock();
        try {
            permits = initialPermits;
        }finally {
            lock.unlock();
        }
    }

    //BLOCKS-UNTIL:permitsAvailable
    public void acquire() throws InterruptedException{
        lock.lock();
        try {
            while (permits <= 0)
                permitsAvailable.await();
            --permits;
        }finally {
            lock.unlock();
        }
    }

    public void release(){
        lock.lock();
        try {
            ++permits;
            permitsAvailable.signal();
        }finally {
            lock.unlock();
        }
    }
}

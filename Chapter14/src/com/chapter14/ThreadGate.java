package com.chapter14;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class ThreadGate {
    /**
     * CONDITION-PREDICATE: open-since(n) (isOpen || generation>n
     */
    @GuardedBy("this")  private boolean isOpen;
    @GuardedBy("this")  private int generation;

    public synchronized void close(){
        isOpen = false;
    }

    public synchronized void open(){
        ++generation;
        isOpen = true;
        notifyAll();
    }

    //BLOCKS-UNTIL: opened-since(generation on entry)
    public synchronized void await() throws InterruptedException{
        int arrivalGeneration = generation;
        while (!isOpen && arrivalGeneration == generation){
            wait();
        }
    }
}

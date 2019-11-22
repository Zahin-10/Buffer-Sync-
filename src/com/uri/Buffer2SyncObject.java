package com.uri;

import java.util.concurrent.TimeoutException;

public class Buffer2SyncObject<T> {
    T data;
    boolean empty;
    private Object r = new Object();
    private Object w = new Object();


    Buffer2SyncObject(){
        data = null;
        empty = true;
    }

    public synchronized T take(long timeout) throws InterruptedException, TimeoutException {
        synchronized (r) {
            while(empty){
                long elapsed = System.nanoTime();
                r.wait(timeout);
                elapsed = (System.nanoTime() - elapsed) / 1000000;
                System.out.println(elapsed);
                if(elapsed > timeout){
                    throw new TimeoutException();
                }
            }
            synchronized (w){
                T value = data;
                data = null;
                empty = true;
                w.notify();
                return value;
            }
        }

    }

    public synchronized void put(T elem) throws InterruptedException{
        synchronized (w)
        {
            while(!empty){
                w.wait();
            }
            synchronized (r)
            {
                data = elem;
                empty = false;
                r.notify();
            }
        }
    }

    public synchronized boolean tryPut(T elem) throws InterruptedException{
        synchronized (w)
        {
            if(!empty){
                return false;
            }
            synchronized (r)
            {
                data = elem;
                empty = false;
                r.notify();
                return true;
            }
        }
    }

    public synchronized void overwrite(T content){
        synchronized (w){
            synchronized (r){
                data = content;
                r.notify();
            }
        }
    }

    public synchronized T read() throws InterruptedException{
        synchronized (r){
            while (empty){
                r.wait();
            }
            synchronized (w){
                T content = data;
                r.notify();
                return content;
            }
        }
    }
}

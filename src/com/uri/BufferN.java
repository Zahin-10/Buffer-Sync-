package com.uri;

public class BufferN<T> {
    T[] data;
    int start,end;
    int capacity;

    BufferN(int size){
        data = (T[]) new Object[size];
        start = 0;
        end = 0;
        capacity = size;
    }

    public synchronized T take() throws InterruptedException{
        while(isEmpty()){
            wait();
        }
        T value = data[start];
        data[start] = null;
        if(start == data.length -1){
            start = 0;
        }else{
            start++;
        }


        return value;
    }

    public synchronized void put(T elem) throws InterruptedException{
        while(capacity == 0){
            wait();
        }

        data[end] = elem;
        capacity--;

        if(end == data.length-1){
            end = 0;
        }else {
            end++;
        }
        if(capacity == data.length - 1){
            notify();
        }
    }

    public synchronized boolean isEmpty(){
        return capacity == data.length;
    }

}

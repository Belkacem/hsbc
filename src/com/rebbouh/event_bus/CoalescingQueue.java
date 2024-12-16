package com.rebbouh.event_bus;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This is an implementation of Blocking queue backed by a LinkedHashMap to preserve order and enables us to handles
 * Coalescence.
 */
public class CoalescingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E> {
    private final Map<Class<?>, E> queue = Collections.synchronizedMap(new LinkedHashMap<>());

    private final ReentrantLock lock = new ReentrantLock();

    private final Condition notEmpty = lock.newCondition();

    private void notEmpty() {
        final ReentrantLock takeLock = this.lock;
        takeLock.lock();
        try {
            notEmpty.signal();
        } finally {
            takeLock.unlock();
        }
    }

    @Override
    public Iterator<E> iterator() {
        return queue.values().iterator();
    }

    @Override
    public int size() {
        return queue.size();
    }



    @Override
    public void put(E e) throws InterruptedException {
        this.lock.lockInterruptibly();
        try {
            queue.put(e.getClass(), e);
            notEmpty();
        } finally {
            this.lock.unlock();
        }

    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        put(e);
        return true;
    }

    @Override
    public E take() throws InterruptedException {
        this.lock.lockInterruptibly();
        try {
            while (queue.size() == 0) {
                notEmpty.await();
            }
            var iterator = iterator();
            var value = iterator.next();
            iterator.remove();
            if (queue.size() > 0) {
                notEmpty();
            }
            return value;
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        return take();
    }

    @Override
    public int remainingCapacity() {
        return 1;
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        var size = queue.keySet().size();
        c.addAll(queue.values());
        queue.clear();
        return size;
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        return drainTo(c);
    }

    @Override
    public boolean offer(E e) {
        try {
            return offer(e, 1, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public E poll() {
        try {
            return take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public E peek() {
        if (iterator().hasNext()) {
            return iterator().next();
        }
        return null;
    }

    @Override
    public String toString() {
        return "CoalescingQueue{" +
                "queue=" + queue +
                '}';
    }
}
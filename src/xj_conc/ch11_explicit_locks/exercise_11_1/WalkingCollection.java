package xj_conc.ch11_explicit_locks.exercise_11_1;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * WalkingCollection allows multiple threads to call the
 * iterate() method concurrently and is still threadsafe
 */
public class WalkingCollection<E> extends AbstractCollection<E> {

    private final Collection<E> wrappedCollection;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public WalkingCollection(Collection<E> wrappedCollection) {
        this.wrappedCollection = wrappedCollection;
    }

    public void iterate(Processor<? super E> processor) {
        // lock using a ReadLock, then iterate through collection calling
        // processor.process(e) on each element

        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        readLock.lock();
        try {
            for (E e : wrappedCollection) {
                if (!processor.process(e)) return;
            }
        } finally {
            readLock.unlock();
        }
    }

    public Iterator<E> iterator() {
        // this method should not really be called by users anymore, instead
        // they should call the iterate(Processor) method

        // return an iterator that locks a ReadLock on hasNext() and next()
        // and a WriteLock on remove().

        // Should throw IllegalMonitorStateException if a thread tries to
        // call remove() during iteration.
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        readLock.lock();
        try {
            Iterator<E> wrappedIterator = wrappedCollection.iterator();
            return new Iterator<E>() {
                public boolean hasNext() {
                    ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
                    readLock.lock();
                    try {
                        return wrappedIterator.hasNext();
                    } finally {
                        readLock.unlock();
                    }
                }

                public E next() {
                    ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
                    readLock.lock();
                    try {
                        return wrappedIterator.next();
                    } finally {
                        readLock.unlock();
                    }
                }

                public void remove() {
                    if (lock.getReadHoldCount() > 0) {
                        throw new IllegalMonitorStateException("thread is already busy iterating");
                    }
                    ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
                    writeLock.lock();
                    try {
                        wrappedIterator.remove();
                    } finally {
                        writeLock.unlock();
                    }
                }
            };
        } finally {
            readLock.unlock();
        }
    }

    public int size() {
        // the size of the wrappedCollection, but wrapped with a ReadLock
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        readLock.lock();
        try {
            return wrappedCollection.size();
        } finally {
            readLock.unlock();
        }
    }

    public boolean add(E e) {
        // adds the element to the collection, throws
        // IllegalMonitorStateException if that thread is busy iterating
        if (lock.getReadHoldCount() > 0) {
            throw new IllegalMonitorStateException("thread is already busy iterating");
        }
        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            return wrappedCollection.add(e);
        } finally {
            writeLock.unlock();
        }
    }
}

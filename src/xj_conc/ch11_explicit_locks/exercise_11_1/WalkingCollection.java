package xj_conc.ch11_explicit_locks.exercise_11_1;

import java.util.*;

/**
 * TODO: Fix the WalkingCollection to allow multiple threads to call the
 * TODO: iterate() method concurrently and to still be threadsafe
 */
public class WalkingCollection<E>
    extends AbstractCollection<E> {
    private final Collection<E> wrappedCollection;

    public WalkingCollection(Collection<E> wrappedCollection) {
        this.wrappedCollection = wrappedCollection;
    }

    public synchronized void iterate(Processor<? super E> processor) {
        // TODO: lock using a ReadLock, then iterate through collection calling
        // TODO: processor.process(e) on each element
        for (E e : wrappedCollection) {
            if (!processor.process(e)) return;
        }
    }

    public synchronized Iterator<E> iterator() {
        // TODO: this method should not really be called by users anymore, instead
        // TODO: they should call the iterate(Processor) method

        // TODO: return an iterator that locks a ReadLock on hasNext() and next()
        // TODO: and a WriteLock on remove().

        // TODO: Should throw IllegalMonitorStateException if a thread tries to
        // TODO: call remove() during iteration.
        Iterator<E> wrappedIterator = wrappedCollection.iterator();
        return new Iterator<E>() {
            public boolean hasNext() {
                synchronized (WalkingCollection.this) {
                    return wrappedIterator.hasNext();
                }
            }

            public E next() {
                synchronized (WalkingCollection.this) {
                    return wrappedIterator.next();
                }
            }

            public void remove() {
                synchronized (WalkingCollection.this) {
                    wrappedIterator.remove();
                }
            }
        };
    }

    public synchronized int size() {
        // TODO: the size of the wrappedCollection, but wrapped with a ReadLock
        return wrappedCollection.size();
    }

    public synchronized boolean add(E e) {
        // TODO: adds the element to the collection, throws
        // TODO: IllegalMonitorStateException if that thread is busy iterating
        return wrappedCollection.add(e);
    }
}

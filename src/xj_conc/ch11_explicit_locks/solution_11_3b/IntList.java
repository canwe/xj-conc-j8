package xj_conc.ch11_explicit_locks.solution_11_3b;

import net.jcip.annotations.*;

import java.util.*;
import java.util.concurrent.locks.*;

@ThreadSafe
public class IntList {
    private final ReadWriteLock rwlock = new ReentrantReadWriteLock();
    private int[] arr = new int[10];
    private int size = 0;

    public int size() {
        rwlock.readLock().lock();
        try {
            return size;
        } finally {
            rwlock.readLock().unlock();
        }
    }

    public int get(int index) {
        rwlock.readLock().lock();
        try {
            rangeCheck(index, this.size);
            return this.arr[index];
        } finally {
            rwlock.readLock().unlock();
        }
    }

    public void trimToSize() {
        rwlock.writeLock().lock();
        try {
            if (size < arr.length) {
                arr = Arrays.copyOf(arr, size);
            }
        } finally {
            rwlock.writeLock().unlock();
        }
    }

    public boolean add(int e) {
        rwlock.writeLock().lock();
        try {
            if (size + 1 > arr.length)
                arr = Arrays.copyOf(arr, size + 10);

            arr[size++] = e;
            return true;
        } finally {
            rwlock.writeLock().unlock();
        }
    }

    public int remove(int index) {
        rwlock.writeLock().lock();
        try {
            rangeCheck(index, size);

            int oldValue = arr[index];

            int numMoved = size - index - 1;
            if (numMoved > 0)
                System.arraycopy(arr, index + 1, arr, index, numMoved);
            arr[--size] = 0;

            return oldValue;
        } finally {
            rwlock.writeLock().unlock();
        }
    }

    private int doActualRemove(int index) {
        int oldValue = arr[index];

        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(arr, index + 1, arr, index, numMoved);
        arr[--size] = 0;

        return oldValue;
    }

    private static void rangeCheck(int index, int size) {
        if (index >= size)
            throw new IndexOutOfBoundsException(
                "Index: " + index + ", Size: " + size);
    }
}

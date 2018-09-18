package xj_conc.ch11_explicit_locks.solution_11_3;

import net.jcip.annotations.*;

import java.util.*;
import java.util.concurrent.locks.*;

@ThreadSafe
public class IntList {
    private static final int OPTIMISTIC_SPIN = 3;
    private final StampedLock sl = new StampedLock();
    private int[] arr = new int[10];
    private int size = 0;

    public int size() {
        sl.tryOptimisticRead(); // volatile read, same as entering a synchronized block
        return this.size;
    }

    public int get(int index) {
        for (int i = 0; i < OPTIMISTIC_SPIN; i++) {
            long stamp = sl.tryOptimisticRead();
            int size = this.size; // completely safe
            int[] arr = this.arr; // completely safe
            if (index < arr.length) { // completely safe
                int r = arr[index]; // completely safe
                if (sl.validate(stamp)) { // only one validate needed
                    rangeCheck(index, size);
                    return r;
                }
            }
        }
        long stamp = sl.readLock();
        try {
            rangeCheck(index, this.size);
            return this.arr[index];
        } finally {
            sl.unlockRead(stamp);
        }
    }

    public void trimToSize() {
        long stamp = sl.tryOptimisticRead();
        int currentSize = size;
        int[] currentArr = arr;
        if (sl.validate(stamp)) {
            // fast optimistic read to accelerate trimToSize() when
            // there is no work to do
            if (currentSize == currentArr.length) return;
        }
        stamp = sl.writeLock();
        try {
            if (size < arr.length) {
                arr = Arrays.copyOf(arr, size);
            }
        } finally {
            sl.unlockWrite(stamp);
        }
    }

    public boolean add(int e) {
        long stamp = sl.writeLock();
        try {
            if (size + 1 > arr.length)
                arr = Arrays.copyOf(arr, size + 10);

            arr[size++] = e;
            return true;
        } finally {
            sl.unlockWrite(stamp);
        }
    }

    // just for illustration purposes to show how an upgrade could be coded
    public int removeWithUpgrade(int index) {
        long stamp = sl.readLock();
        try {
            while (true) {
                rangeCheck(index, size);
                long writeStamp = sl.tryConvertToWriteLock(stamp);
                if (writeStamp == 0) {
                    sl.unlockRead(stamp);
                    stamp = sl.writeLock();
                } else {
                    stamp = writeStamp;
                    return doActualRemove(index);
                }
            }
        } finally {
            sl.unlock(stamp);
        }
    }

    public int remove(int index) {
        long stamp = sl.writeLock();
        try {
            rangeCheck(index, size);
            return doActualRemove(index);
        } finally {
            sl.unlock(stamp);
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

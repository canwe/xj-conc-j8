package xj_conc.ch11_explicit_locks.exercise_11_3;

import net.jcip.annotations.*;

import java.util.*;
import java.util.concurrent.locks.StampedLock;

/*
synchronized on monitor
Best values:
	size()        19,301,017
	get()         15,027,752
	add/remove()  26,467,009
Worst values:
	size()        12,231,428
	get()         5,613,637
	add/remove()  22,254,451

stamped lock
Best values:
	size()        63,349,722
	get()         5,164,578
	add/remove()  15,867,251
Worst values:
	size()        39,158,698
	get()         3,541,922
	add/remove()  10,643,719
 */
@ThreadSafe
public class IntList {
    private final Object monitor = new Object();

    //@GuardedBy("monitor")
    private int[] arr = new int[10];

    //@GuardedBy("monitor")
    private int size = 0;

    private final StampedLock sl = new StampedLock();

    public int size() {
        long stamp = sl.tryOptimisticRead();
        if (!sl.validate(stamp)) {
            stamp = sl.readLock();
            try {
                return size;
            } finally {
                sl.unlockRead(stamp);
            }
        }
        return size;
    }

    public int get(int index) {
        long stamp = sl.readLock();
        try {
            rangeCheck(index, size);
            return arr[index];
        } finally {
            sl.unlockRead(stamp);
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
            if (size < arr.length)
                arr = Arrays.copyOf(arr, size);
        } finally {
            sl.unlockWrite(stamp);
        }
    }

    public int remove(int index) {
        long stamp = sl.writeLock();
        try {
            rangeCheck(index, size);

            int oldValue = arr[index];

            int numMoved = size - index - 1;
            if (numMoved > 0)
                System.arraycopy(arr, index + 1,
                    arr, index, numMoved);
            arr[--size] = 0;

            return oldValue;
        } finally {
            sl.unlock(stamp);
        }
    }

    private static void rangeCheck(int index, int size) {
        if (index >= size)
            throw new IndexOutOfBoundsException(
                "Index: " + index + ", Size: " + size);
    }
}

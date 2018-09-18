package xj_conc.ch09_testing_concurrent_programs.exercise_9_1;

import java.util.concurrent.atomic.*;

/**
 * DO NOT CHANGE.
 */
public class HandoverQueue<E> {
    private static class Node<E> {
        private volatile E item;
        private volatile Node<E> next;

        private static final AtomicReferenceFieldUpdater<Node, Node> nextUpdater =
            AtomicReferenceFieldUpdater.newUpdater(Node.class, Node.class, "next");
        private static final AtomicReferenceFieldUpdater<Node, Object> itemUpdater =
            AtomicReferenceFieldUpdater.newUpdater(Node.class, Object.class, "item");

        Node(E x, Node<E> n) {
            item = x;
            next = n;
        }

        E getItem() {
            return item;
        }

        void setItem(E val) {
            itemUpdater.set(this, val);
        }

        Node<E> getNext() {
            return next;
        }

        boolean casNext(Node<E> cmp, Node<E> val) {
            return nextUpdater.compareAndSet(this, cmp, val);
        }

        void setNext(Node<E> val) {
            nextUpdater.set(this, val);
        }
    }

    private static final AtomicReferenceFieldUpdater<HandoverQueue, Node> tailUpdater =
        AtomicReferenceFieldUpdater.newUpdater(HandoverQueue.class, Node.class, "tail");
    private static final AtomicReferenceFieldUpdater<HandoverQueue, Node> headUpdater =
        AtomicReferenceFieldUpdater.newUpdater(HandoverQueue.class, Node.class, "head");

    private boolean casTail(Node<E> cmp, Node<E> val) {
        return tailUpdater.compareAndSet(this, cmp, val);
    }

    private boolean casHead(Node<E> cmp, Node<E> val) {
        return headUpdater.compareAndSet(this, cmp, val);
    }

    private volatile Node<E> head = new Node<>(null, null);

    private volatile Node<E> tail = head;

    public boolean offer(E e) {
        if (e == null) throw new NullPointerException();
        Node<E> n = new Node<>(e, null);
        while (true) {
            Node<E> t = tail;
            Node<E> s = t.getNext();
            if (t == tail) {
                if (s == null) {
                    if (t.casNext(s, n)) {
                        casTail(t, n);
                        return true;
                    }
                } else {
                    casTail(t, s);
                }
            }
        }
    }

    public E poll() {
        while (true) {
            Node<E> h = head;
            Node<E> t = tail;
            Node<E> first = h.getNext();
            if (h == head) {
                if (h == t) {
                    if (first == null)
                        return null;
                    else
                        casTail(t, first);
                } else if (casHead(h, first)) {
                    E item = first.getItem();
                    if (item != null) {
                        first.setItem(null);
                        return item;
                    }
                    // else skip over deleted item, continue loop,
                }
            }
        }
    }

    public E peek() { // same as poll except don't remove item
        while (true) {
            Node<E> h = head;
            Node<E> t = tail;
            Node<E> first = h.getNext();
            if (h == head) {
                if (h == t) {
                    if (first == null)
                        return null;
                    else
                        casTail(t, first);
                } else {
                    E item = first.getItem();
                    if (item != null)
                        return item;
                    else // remove deleted node and continue
                        casHead(h, first);
                }
            }
        }
    }

    private Node<E> first() {
        while (true) {
            Node<E> h = head;
            Node<E> t = tail;
            Node<E> first = h.getNext();
            if (h == head) {
                if (h == t) {
                    if (first == null)
                        return null;
                    else
                        casTail(t, first);
                } else {
                    if (first.getItem() != null)
                        return first;
                    else // remove deleted node and continue
                        casHead(h, first);
                }
            }
        }
    }


    public boolean isEmpty() {
        return first() == null;
    }

    public int size() {
        int count = 0;
        for (Node<E> p = first(); p != null; p = p.getNext()) {
            if (p.getItem() != null) {
                // Collections.size() spec says to max out
                if (++count == Integer.MAX_VALUE)
                    break;
            }
        }
        return count;
    }
}

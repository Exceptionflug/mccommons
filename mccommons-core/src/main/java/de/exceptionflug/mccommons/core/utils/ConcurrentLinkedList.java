package de.exceptionflug.mccommons.core.utils;

import java.io.Serializable;
import java.util.AbstractSequentialList;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class ConcurrentLinkedList<T> extends AbstractSequentialList<T> implements Serializable {

    private static final long serialVersionUID = -8601367436705211048L;
    private final Node root = new Node();
    private int size = -1;

    @Override
    public ListIterator<T> listIterator(final int index) {
        return new ListItr(index);
    }

    @Override
    public int size() {
        if (size == -1) {
            size = root.elementsAfter();
        }
        return size;
    }

    Node node(final int index) {
        int i = -1;
        Node cursor = root;
        do {
            cursor = cursor.next;
            if (cursor == null) {
                throw new IndexOutOfBoundsException("Requested index " + index + " but size is " + size());
            }
        } while ((i += 1) != index);
        return cursor;
    }

    public void removeFirst() {
        remove(0);
    }

    public void addLast(final T t) {
        add(t);
    }

    public T getLast() {
        return node(size() - 1).getItem();
    }

    class Node implements Serializable {

        private static final long serialVersionUID = 923460414702730917L;
        public volatile Node next;
        public volatile Node prev;
        private T item;

        public Node(final T t) {
            item = t;
        }

        public Node() {
        }

        public int elementsAfter() {
            int i = 1;
            Node cursor = next;
            if (next == null) {
                return 0;
            }
            while ((cursor = cursor.next) != null) {
                i++;
            }
            return i;
        }

        public int elementsBefore() {
            int i = 0;
            Node cursor = prev;
            if (prev == null) {
                return 0;
            }
            while ((cursor = cursor.prev) != null) {
                i++;
            }
            return i;
        }

        public T getItem() {
            return item;
        }

        @Override
        public String toString() {
            return item == null ? "root" : item.toString();
        }
    }

    class ListItr implements ListIterator<T> {

        private Node lastReturned = null;
        private Node next;
        private Node prev;
        private int nextIndex;

        ListItr(final int index) {
            next = (index == size()) ? null : node(index);
            prev = (index == 0) ? null : next != null ? next.prev : node(index - 1);
            nextIndex = index;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            lastReturned = next;
            next = next.next;
            prev = next;
            nextIndex++;
            return lastReturned.getItem();
        }

        @Override
        public boolean hasPrevious() {
            return nextIndex - 1 >= 0;
        }

        @Override
        public T previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
            lastReturned = prev;
            nextIndex--;
            next = prev;
            prev = (nextIndex < 0) ? null : prev.prev;
            return lastReturned.getItem();
        }

        @Override
        public int nextIndex() {
            return nextIndex;
        }

        @Override
        public int previousIndex() {
            return nextIndex - 1;
        }

        @Override
        public void remove() {
            final Node following = lastReturned.next;
            final Node trailing = lastReturned.prev;
            if (following != null) {
                following.prev = trailing;
            }
            if (trailing != null) {
                trailing.next = following;
            }
            size--;
        }

        @Override
        public void set(final T t) {
            final Node newNode = new Node(t);
            final Node following = lastReturned.next;
            final Node trailing = lastReturned.prev;
            if (following != null) {
                newNode.next = following;
            }
            if (trailing != null) {
                newNode.prev = trailing;
            }
            if (following != null) {
                following.prev = newNode;
            }
            if (trailing != null) {
                trailing.next = newNode;
            }
        }

        @Override
        public void add(final T t) {
            final Node newNode = new Node(t);
            final int prevIndex = nextIndex - 1;
            final Node prev = prevIndex < 0 ? root : node(prevIndex);
            prev.next = newNode;
            newNode.prev = prev;

            if (next != null) {
                next.prev = newNode;
                newNode.next = next;
            }
            size++;
        }
    }

}

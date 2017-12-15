import java.util.Iterator;
import java.util.Spliterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class ThreadSafeLinkedList<T> implements Iterable<T> {

  private Node head;
  private Node current;
  private Lock lock;


  ThreadSafeLinkedList() {
    this.head = new Node();
    current = head;
    lock = new ReentrantLock();
  }

  void add(T item) {
    lock.lock();
    Node temp = new Node(item);
    current.next = temp;
    current = temp;
    lock.unlock();
  }

  boolean remove(T item) {
    lock.lock();
    boolean retVal = false;
    Node ptr = head;
    if(ptr.next == null) return false;
    Node nextPtr = head.next;
    while(nextPtr != null) {
      if(nextPtr.value == item) {
        ptr.next = nextPtr.next;
        retVal = true;
      }
      nextPtr = nextPtr.next;
    }
    lock.unlock();
    return retVal;
  }

  @Override public Iterator<T> iterator() {
    return new lockIterator();
  }

  @Override public void forEach(Consumer<? super T> action) {

  }

  @Override public Spliterator<T> spliterator() {
    return null;
  }

  private class lockIterator implements Iterator<T> {

    Node ptr;

    lockIterator() {
      this.ptr = head;
    }

    @Override public boolean hasNext() {
      lock.lock();
      boolean result = ptr.next != null;
      lock.unlock();
      return result;
    }

    @Override public T next() {
      lock.lock();
      T retVal = null;
      if(ptr.next != null) {
        retVal = ptr.next.value;
        ptr = ptr.next;
      }
      lock.unlock();
      return retVal;
    }

    @Override public void remove() {
      lock.lock();

    }

    @Override public void forEachRemaining(Consumer<? super T> action) {

    }
  }


  public class Node {
    T value;
    Node next;

    Node() {
      this.next = null;
    }

    Node(T val) {
      this.value = val;
      this.next = null;
    }
  }

}

import java.util.Iterator;
import java.util.NoSuchElementException;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;


public class Deque<Item> implements Iterable<Item> {
    private int n;                // size of the queue
    private Node first, last;     // front and end of queue

    // linked list node helper data type (sentinel node)
    private class Node {
        private Item item;
        private Node next;
        private Node prev;
    }
    
    /**
     * Initializes an empty queue.
     */
    public Deque() {
        first = null;
        last = null;
        n = 0;
        assert check();
    }
    
    /**
     * Is this queue empty?
     * @return true if this queue is empty; false otherwise
     */
    public boolean isEmpty() {
        return first == null;
    }

    /**
     * Returns the number of items in the queue.
     * @return the number of items in the queue
     */
    public int size() {
        return n;
    }

    /**
     * Adds the item to this front of the queue.
     * @param item the item to add
     */
    public void addFirst(Item item) {
        if (item == null) throw new IllegalArgumentException();
        Node oldfirst = first;
        first = new Node();
        first.item = item;
        first.next = oldfirst;
        first.prev = null;
        if (n == 0) last = first;           // special case for empty queue
        else        oldfirst.prev = first;
        n++;
        assert check();
    }

    /**
     * Adds the item to this end of the queue.
     * @param item the item to add
     */
    public void addLast(Item item) {
        if (item == null) throw new IllegalArgumentException();
        Node oldlast = last;
        last = new Node();
        last.item = item;
        last.prev = oldlast;
        last.next = null;
        if (isEmpty()) first = last;        // special case for empty queue
        else           oldlast.next = last;
        n++;
        assert check();
    }

    /**
     * Removes and returns the item from the front.
     * @return the item from the front of the queue
     * @throws java.util.NoSuchElementException if this stack is empty
     */
    public Item removeFirst() {
        if (isEmpty()) throw new NoSuchElementException("Stack underflow");
        Item item = first.item;        // save item to return
        first = first.next;            // delete first node
        if (isEmpty()) last = null;    // special case for empty queue
        else           first.prev = null;
        n--;
        assert check();
        return item;                   // return the saved item
    }

    /**
     * Removes and returns the item from the end.
     * @return the item from the end of the queue
     * @throws java.util.NoSuchElementException if this stack is empty
     */
    public Item removeLast() {
        if (isEmpty()) throw new NoSuchElementException("Stack underflow");
        Item item = last.item;         // save item to return
        last = last.prev;              // delete last node
        n--;
        if (size() == 0) first = null; // special case for empty queue
        else             last.next = null;
        assert check();
        return item;                   // return the saved item
    }

    /**
     * Returns an iterator over items in order from front to end.
     * @return an iterator over items in order from front to end
     */
    public Iterator<Item> iterator() {
        return new ListIterator();
    }

    // an iterator, doesn't implement remove() since it's optional
    private class ListIterator implements Iterator<Item> {
        private Node current = first;
        public boolean hasNext()  { return current != null;                     }
        public void remove()      { throw new UnsupportedOperationException();  }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            Item item = current.item;
            current = current.next; 
            return item;
        }
    }

    // check internal invariants
    private boolean check() {

        // check a few properties of instance variable 'first' and 'last'
        if (n < 0) {
            return false;
        }
        if (n == 0) {
            if (first != null || last != null) return false;
        }
        else if (n == 1) {
            if (first == null || last == null) return false;
            if (first.next != null || first.prev != null) return false;
            if (last.next != null || last.prev != null)   return false;            
        }
        else if (n == 2) {
            if (first.next != last || last.prev != first) return false;
        }
        else {
            if (first == null || last == null) return false;
            if (first.next == null || last.prev == null) return false;
        }

        // check internal consistency of instance variable n
        int numberOfNodes = 0;
        for (Node x = first; x != null && numberOfNodes <= n; x = x.next) {
            numberOfNodes++;
        }
        if (numberOfNodes != n) return false;

        return true;
    }

    // unit tests the data type
    public static void main(String[] args) {
        Deque<String> mydeque = new Deque<String>();
        while (!StdIn.isEmpty()) {
            String item = StdIn.readString();
            if (!item.equals("-"))
                mydeque.addLast(item);
            else if (!mydeque.isEmpty())
                StdOut.print(mydeque.removeLast() + " ");           
        }
        StdOut.println("(" + mydeque.size() + " left on queue)");
        // Use iterator to display remaining items
        StdOut.println("Items left on queue: ");
        Iterator<String> itr = mydeque.iterator();
        while(itr.hasNext()) {
            String element = itr.next();
            StdOut.print(element + " ");
        }
    }
}
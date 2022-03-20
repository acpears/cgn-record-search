/**
 * Course number:       COMP5511
 * Instructor:          Prof. Bipin C. DESAI
 * Assignment number:   02
 * Question: 4 Part 1
 * Submitted by:        Group 11
 * -
 * Group members:
 * -
 * ID           Name    Last Name       Email                           Group Leader
 * 40003312	    Sima	NOPARAST        s_nopara@encs.concordia.ca      [ ]
 * 40046477	    Matthew	MORGAN          m_rgan@encs.concordia.ca        [ ]
 * 40181490	    Boris	NIJIKOVSKY      b_nijiko@encs.concordia.ca      [ ]
 * 40181988	    Adam	PEARSON         a_ears@encs.concordia.ca        [*]
 */


/***************************************************************************************
 * Please note inspriation for this code was taken from the following sources
 *    Title: Find Length of a Linked List (Iterative and Recursive)
 *    Link: https://www.geeksforgeeks.org/find-length-of-a-linked-list-iterative-and-recursive/
 *    Author: GeeksForGeeks.com - rathbhupendra
 *    Date: 2020-10-25
 *    Code version: 1
 *    Availability: Internet
 ***************************************************************************************/


public class SinglyLinkedList<E> {

	private Node<E> head;
    private Node<E> tail;
    private int size;

    /* constructor */

    public SinglyLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /* accessors */

    public E getFirst() {
        return this.head.data;
    }

    public E getLast() {
        return this.tail.data;
    }

    public E get(int i) {
        Node<E> temp = this.head;
        int count = 0;
        while(count < this.size){
            if(i == count){
                return temp.data;
            }
            count++;
            temp = temp.nextNode;
        }
        return null;
    }

    /* Returns count of nodes in this singly linked list */
    public int getSize() {
        return this.size;
    }

    public boolean isEmpty() {
        return (this.head == null) && (this.tail == null);
    }

    /* mutators */
    public void addFirst(E elementData) {
        Node<E> newNode = new Node<>(elementData, this.head);
        this.head = newNode;
        if (this.tail == null) {
            this.tail = this.head;
        }
        this.size++;
    }

    public void addLast(E elementData) {
        Node<E> newTail = new Node<>(elementData, null);
        if (this.isEmpty()) {
            this.head = newTail;
        } else {
            this.tail.nextNode = newTail;
        }
        this.tail = newTail;
        this.size++;
    }

    public void add(E elementData) {
        addFirst(elementData);
    }

    /* Prints the elements of the list. Custom print for CGN record type */
    public void print() {
        Node<E> temp = head; //set a temp note to the value of the head
        //if at the head, return zero. If not, iterate until the end of the LL
        int count = 1;
        while (temp != null) {
            if(temp.data instanceof CGNRecord){
                CGNRecord t = (CGNRecord) temp.data;
                System.out.print(count + " - [" + t.getId() + "]\n");
            }else{
                System.out.print(count + "[" + temp.data + "] ");
            }
            count++;
            temp = temp.nextNode; //make temp the next node
        }
    }

    /* Searches the list for value */
    public boolean contains(E value) {

        Node<E> temp = head; //set a temp note to the value of the head
        //if at the head, return zero. If not, iterate until the end of the LL
        while (temp != null) {
            if (temp.data.equals(value)){
                return true;
            }
            temp = temp.nextNode; //make temp the next node
        }
        return false;
    }

    /* `Node` class */
    private static class Node<E> {

        private final E data;
        private Node<E> nextNode;

        /* constructor */

        public Node(E data, Node<E> nextNode) {
            this.data = data;
            this.nextNode = nextNode;
        }
    }

    public class SLLIterator {
        private Node<E> currentNode;

        public SLLIterator() {
            this.currentNode = head;
        }

        public boolean hasNext() {
            return this.currentNode != null;
        }

        public void next() {
            this.currentNode = this.currentNode.nextNode;
        }

        public E getCurrent() {
            return currentNode.data;
        }

    }
}


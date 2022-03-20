// Course number:       COMP5511
// Instructor:          Prof. Bipin C. DESAI
// Assignment number:   04
// Question: 6
// Submitted by:        Group 11
// -
// Group members:
// -
// ID           Name    Last Name       Email                           Group Leader
// 40003312	    Sima	NOPARAST        s_nopara@encs.concordia.ca      [ ]
// 40046477	    Matthew	MORGAN          m_rgan@encs.concordia.ca        [ ]
// 40181490	    Boris	NIJIKOVSKY      b_nijiko@encs.concordia.ca      [ ]
// 40181988	    Adam	PEARSON         a_ears@encs.concordia.ca        [*]

/* 
CLASS DESCRIPTION: Binary Search Tree
    - Simple implementation of a binary search tree
*/


public class BST<E>{

    private BST_Node<E> root;
    private boolean autoBalance; //Boolean switch if you want a blanced BST or not

    private final class BST_Node<E>{  
        private int key; // key value
        private E data; // reference to data
        private BST_Node<E> left;
        private BST_Node<E> right;
        private int size;
        private int height;
        private int balance; 

        public BST_Node(int k, E value){
            this.key = k;
            this.data = value;
            this.left = null;
            this.right = null;
            this.size = 1;
            this.height = 1;
            this.balance = 0;
        }
    }

    public BST(boolean b){
        this.root = null;
        this.autoBalance = b;
    }

    // Method to insert element with key k into tree
    public void add(int k, E value){
        this.root = insertRecur(this.root, k, value);
    }

    // Method to retreive element with key k
    public E get(int k){
        BST_Node<E> result = searchRecur(root, k);
        if(result == null){ //If search returns null key was not found
            return null;
        } else{
            return result.data;
         }
    }

    // Search BST for nodes within key range. Return a list of the data elements. 
    // Return empty list if no nodes are found
    public SinglyLinkedList<E> getInRange(int kmin, int kmax){
        SinglyLinkedList<E> temp = new SinglyLinkedList<>();
        return searchRangeRecur(temp, this.root, kmin, kmax);
    }

    /* Recursive methods for inserting & searching */
    
    // Recursively insert an element in the BST with key k
    private BST_Node<E> insertRecur(BST_Node<E> node, int k, E element){
        if(node == null){
            return new BST_Node<>(k, element);
        }
        if(k < node.key ){
            node.left = insertRecur(node.left, k, element);
           
        } else if(k > node.key){
            node.right = insertRecur(node.right, k, element);
            
        } else{
            node.data = element;
            System.out.println(node.key + " key value has been UPDATED");

        }

        //Parameter update of the sub tree after insertion
        updateSize(node); // Update size of each sub tree after insertion
        updateHeight(node); // Update height of each sub tree after insertion
        updateBalance(node); // Update balance factor of each sub stree after insertion

        // If insertion of element has cause sub tree to become unbalanced then rebalance
        if(unbalanced(node) && autoBalance){
            node = balance(node);  
        }

        return node;
    }

    // Recursively search for an element in the BST with key k
    private BST_Node<E> searchRecur(BST_Node<E> node, int k){
        if(node == null || node.key == k)
            return node;
        if(k < node.key )
            return searchRecur(node.left, k);
        else 
            return searchRecur(node.right, k);
    }

    // Recursively search for a ranger of elements in the BST within key values kmin and kmax
    private SinglyLinkedList<E> searchRangeRecur(SinglyLinkedList<E> temp, BST_Node<E> node, int min, int max){
        if(node == null)
            return temp;
        if (node.key > min) {
            searchRangeRecur(temp, node.left, min, max);
        }
        if (min <= node.key && node.key <= max) {
            temp.addFirst(node.data);
        }
        if (node.key < max) {
            searchRangeRecur(temp, node.right, min, max);
        }
        return temp;
    }

    // Return size 0 for a null subtree/node
    private int nodeSize(BST_Node<E> node){
        if(node == null){
            return 0;
        }
        return node.size;
    }

    // Return height 0 for a null subtree/node  
    private int nodeHeight(BST_Node<E> node){
        if(node == null){
            return 0;
        }
        return node.height;
    }


    /* Updates the balance, height & size value of a node */
    
    private void updateBalance(BST_Node<E> node){
        if(node == null)
            return;
        else if (node.left == null && node.right == null)
            node.balance = 0;
        else if(node.left == null)
            node.balance = node.right.height - 0;
        else if (node.right == null)
            node.balance = 0 - node.left.height;
        else
            node.balance = node.right.height - node.left.height;
    }

    private void updateSize(BST_Node<E> node){
        if(node == null){
            return;
        }
        node.size = 1 + nodeSize(node.left) + nodeSize(node.right);
    }

    private void updateHeight(BST_Node<E> node){
        if(node == null){
            return;
        }
        node.height = 1 + Math.max(nodeHeight(node.left),nodeHeight(node.right));
    }

    // Return true if subtree is unbalanced.
    private boolean unbalanced(BST_Node<E> node){
        return (node.balance > 1 || node.balance < -1);
    }

    
    /* Rotation methods used in the rotation algorithm */
    private BST_Node<E> rotateLeft(BST_Node<E> node){
        if(node == null)
            return node;

        BST_Node<E> temp = node.right;
        node.right = temp.left;
        temp.left = node;

        updateHeight(node);
        updateHeight(temp);
        updateSize(node);
        updateSize(temp);

        return temp;
    }

    private BST_Node<E> rotateRight(BST_Node<E> node){
        if(node == null)
            return node;

        BST_Node<E> temp = node.left;      
        node.left = temp.right;
        temp.right = node;

        updateHeight(node);
        updateHeight(temp);
        updateSize(node);
        updateSize(temp);

        return temp;
    }

    // Method to rebalance sub tree using rotate functions depening on balance factor
    private BST_Node<E> balance(BST_Node<E> node){
        if(node.balance < -1){
            if(node.left.balance > 0){
                node.left = rotateLeft(node.left);
            }
            node = rotateRight(node);
            return node;
        } else if (node.balance > 1){
            if(node.right.balance < 0){
                node.right = rotateRight(node.right);
            }
            node = rotateLeft(node);
            return node;
        } else{
            return node;
        }
    }


    /* Generic BST methods */

    public boolean empty(){
        if(root == null)
            return true;
        return false;
    }

    public E root(){
        return root.data;
    }

    public int size(){
        return root.size;
    }

    public int height(){
        return root.height;
    }

    public int balanceFactor(){
        return root.balance;
    }

    public SinglyLinkedList<E> toList(){
        SinglyLinkedList<E> temp = new SinglyLinkedList<>();
        temp = preOrderTraverse(temp, this.root);
        return temp; 
    }

    /* Traversal methods */
    private SinglyLinkedList<E> preOrderTraverse(SinglyLinkedList<E> temp, BST_Node<E> t){
        if(t == null){
            return temp;
        }
        if (t.left == null && t.right == null){ // leaf node
            temp.addFirst(t.data);
            return temp;
        }
        else {
            temp = preOrderTraverse(temp, t.left);
            temp.addFirst(t.data);
            temp = preOrderTraverse(temp, t.right);
            return temp;
        }
    }
}
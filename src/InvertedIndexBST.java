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
CLASS DESCRIPTION: Inverted Index using BST
    - Implementation of an inverted index using a BST
    - For easy insertions into BST strings are converted to integers using a hash function
    - Option also to insert using using an integer. Helpful for creating the inverted indexs on the CGN coordinates
*/


import java.util.ArrayList;
import java.util.Arrays;

public class InvertedIndexBST<E> {

    /***************************************************************************************
     * Please note inspiration for the STOP WORDS was taken from the following sources
     *    Title: https://snowballstem.org/algorithms/english/stop.txt
     *    Link: https://snowballstem.org/algorithms/english/stop.txt
     *    Author: "Snowball"
     *    Date: 2020-12-08
     *    Code version: 1
     *    Availability: Internet
     ***************************************************************************************/
    
    private static SinglyLinkedList<String> stopWords = new SinglyLinkedList<>();

    private BST<SinglyLinkedList<E>> index; //use a BST of Singly linked lists (dictionaries) for inverted index
    private boolean tokenize; //boolean to determine if we split the words in the name and add them to the index
    private int size; //number of entries in index

    public InvertedIndexBST(boolean tokenize)
    {
        this.index = new BST<>(true);
        this.tokenize = tokenize;
        this.size = 0;
    }

    // Insert method using a String
    public void put(String word, E ref){

        ArrayList<String> words = new ArrayList<>(); // List of words to add to the inverted index 
        
        // If tokenize is true we will also add the individual words of the string (eg. Ruisseau des Prairies -> Ruisseau, Prairies)
        if(tokenize){
            words = new ArrayList<>(Arrays.asList(word.toLowerCase().replace("-"," ").split("\\s")));
        }
        words.add(word.toLowerCase()); //Adds the word in lower case to the list of words to add to inv index
        
        for (String w : words){
            if(stopWords.contains(w)) continue; // Skip stop words
            int hash = hashString(w.toLowerCase()); // lower case standard for insertion
            insert(hash, ref);
        }
    }

    // Insert method using an integer
    public void put(int word, E ref){
        insert(word, ref);
    }

    private void insert(int word, E ref){
        // If inverted index is empty
        if (this.index.empty()){
            SinglyLinkedList<E> list = new SinglyLinkedList<>();
            list.addFirst(ref);
            this.index.add(word,list);
            size++;
            return;
        }
        
        // Pull back list from matching node if one exists, if not pull back null
        SinglyLinkedList<E> nodeList = this.index.get(word);

        // If its null, does not already exist. make a new one.
        if (nodeList == null) {
            SinglyLinkedList<E> list = new SinglyLinkedList<>();
            list.addFirst(ref);
            this.index.add(word,list);
            size++;
            return;
        }

        nodeList.addLast(ref); // if it already exists, append the CGN refernce to the list
    }


    // Returns null if word not found
    public SinglyLinkedList<E> get(int word){
        return this.index.get(word);
    }

    // Returns null if word not found
    public SinglyLinkedList<E> get(String word){
        return get(hashString(word.toLowerCase()));
    }

    // Return a list of CGN records within the key min and key max. Returns null if nothing found.
    public SinglyLinkedList<E> getInRange(int min, int max){
        // inverts the order if min is larger than max 
        if(min > max){
            int temp = min;
            min = max;
            max = temp;
        }
        
        SinglyLinkedList<E> temp = new SinglyLinkedList<>();
        SinglyLinkedList<SinglyLinkedList<E>> list = this.index.getInRange(min, max);
        SinglyLinkedList<SinglyLinkedList<E>>.SLLIterator iter1 = list.new SLLIterator();
        
        while(iter1.hasNext()){

            SinglyLinkedList<E>.SLLIterator iter2 = iter1.getCurrent().new SLLIterator();
            while(iter2.hasNext()){
                // Check if CGN recrord is already contained in the list
                if(temp.contains(iter2.getCurrent())){ 
                    continue;
                }
                temp.addLast(iter2.getCurrent());
                iter2.next();
            }
            iter1.next();
        }
        if(temp.isEmpty()) return null;
        return temp;
    }

    public SinglyLinkedList<SinglyLinkedList<E>> toList(){
        return this.index.toList();
    }

    public int size(){
        return this.size;
    }

    private int hashString(String s){
        return 31 * 17 + s.hashCode();
    }

    public static boolean loadStopWords(String fileName){
        try{
            stopWords = FileHandler.makeSinglyLinkedList(FileHandler.makePath(fileName));
            return true;
        } catch(Exception e){
            return false;
        }
    }

}

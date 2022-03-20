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
CLASS DESCRIPTION: Command Reader
    - Methods to interpret the query file
    - Decodes the file in to a set of instructions and their associated data elements
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class CommandReader{

    private File file;
    private ArrayList<String> lines; // Array to store query lines from reading query file
    private ArrayList<Pair<Integer,ArrayList<String>>> queries; // List of queries that contains paired instruction and data
    private int size; // Number of commands

    public CommandReader(File file){
        
        this.file = file;
        this.lines = null;
        this.queries = null;
        this.size = 0;
    }

    // Reads the lines of the query file in to an array list for easy acces
    public boolean readQueryFile(){
        this.lines = new ArrayList<>();

        try (Scanner scanner = new Scanner(this.file)) {
            
            while (scanner.hasNext()) {
                String inputLine = scanner.nextLine().trim(); // read next line from query file
                // Make sure line is formatted properly for instruction and add it to list of queries
                if(validateLineFormat(inputLine)){
                    this.lines.add(inputLine);
                }
            }
            
            // Seperate queries lines into instruction and data pair
            decodeInstructions();
            return true;
            
        } catch (Exception e) {
            return false;
        }
    }

    // Decodes the instructions in each line of the query file. Validates formats
    public void decodeInstructions(){
        this.queries = new ArrayList<>();
        int instruction;
        ArrayList<String> data;

        for(String s : this.lines){
            String[] sep = s.split(":",2); // Split into instruction string and data string
            instruction = mapInstruction(sep[0]);
            data = extractData(sep[1]);
            this.queries.add(new Pair<Integer,ArrayList<String>>(instruction, data));
            size++;

        }
    }

    // Extract data to list from string. If empty string return empty list;
    public static ArrayList<String> extractData(String s){
        ArrayList<String> data = new ArrayList<>(); // List for data elements

        if(s.trim().length() == 0) return data;

        String[] temp = s.trim().split(","); // If data has multiple entries split into string array
        
        for(String i : temp){
            String j = i.trim();
            data.add(j);
        }
        return data;
    }

    // Map the instruction format to a specific interger for easy controlling functions. Need
    // to update when we change the query file;
    public static int mapInstruction(String s){
        String temp = s.trim().toLowerCase();

        switch (temp)
        {
            case "query records with name":
                return 1;
            case "query records containing":
                return 2;
            case "query records with coordinates":
                return 3;
            case "query records between coordinates":
                return 4;
            case "query coordinates of":
                return 5;
            case "query record details with id":
                return 6;
            default:
                return -1;
        }
    }

    // Method to validate line from query file is a query line
    private boolean validateLineFormat(String s){

        // Line if empty
        if(s.isEmpty()) return false; 
        
        // First word must be query
        String[] test = s.split(" ",2);
        if(!test[0].trim().equalsIgnoreCase("query")) return false;

        // Line is not formatted with ":" to seperate instruction and data
        char[] array = s.toCharArray();
        int count = 0;
        for(char c : array){
            if(c == ':') count++;
        }
        if(count != 1) return false; 
  
        return true;
    }

    public int size(){
        return this.size;
    }

    // Retrieval functions used in the controller
    public int getInstruction(int i){
        return this.queries.get(i).getInstruction();
    }

    public ArrayList<String> getData(int i){
        return this.queries.get(i).getData();
    }

    public String getQuery(int i){
        return this.lines.get(i);
    }

    // Data structure to hold paire values (eg. instruction and data)
    private class Pair<E,K>{
        private E key;
        private K data;
    
        public Pair(E i, K d){
            this.key = i;
            this.data = d;
        }
    
        public E getInstruction(){
            return this.key;
        }
    
        public K getData(){
            return this.data;
        }
    }


    /* Printing methods */

    // Method to print the query line from file to the command line
    public void printCommandFile(){
        System.out.println("COMMAND FILE:");
        for(int i = 0 ; i < this.lines.size() ; i++){
            System.out.println(i+1 + ":" +this.lines.get(i));
        }
    }

    // Method to print the queries to the command line
    public void printQueries(){
        System.out.println("INTRUCTIONS: \n");
        for(int i = 0 ; i < this.queries.size() ; i++){
            Pair<Integer,ArrayList<String>> temp = this.queries.get(i);
            System.out.print("Intruction: " + temp.getInstruction() + ", Data: {");
            int count = 0;
            for(String s : temp.getData()){
                System.out.print(s);
                count++;
                if(count != temp.getData().size()){
                    System.out.print(", ");
                }
            }
            System.out.print("}\n");
        }
        
    }
}


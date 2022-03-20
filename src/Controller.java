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
CLASS DESCRIPTION: Controller
    - The back bone of the program
    - Contains all the methods that for logical operations/searches
    - Contains all the methods that produce an output for a given query instruction
    - Outputs to a log file results, warning and errors
*/

import LogWriter.LogWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

//* Controller class that manages all the commands decoded from the command reader class *//
public class Controller {

    private final Parser csvParser;
    private final CommandReader commandReader;
    private final LogWriter logger;
    private final BST<CGNRecord> bstIndex;
    private final InvertedIndexBST<CGNRecord> invIndexOnToken;
    private final InvertedIndexBST<CGNRecord> invIndexOnName;
    private final InvertedIndexBST<CGNRecord> invIndexOnLat;
    private final InvertedIndexBST<CGNRecord> invIndexOnLon;

    // B+ Tree versions not finalized
    // private final BPT<CGNRecord> bstIndex;
    // private final InvertedIndexBPT<CGNRecord> invIndexOnToken;
    // private final InvertedIndexBPT<CGNRecord> invIndexOnName;

    public Controller(Parser csvParser, CommandReader commandReader, LogWriter logger) {

        this.csvParser = csvParser;
        this.commandReader = commandReader;
        this.logger = logger;

        // Indexes
        this.bstIndex = new BST<>(true);
        this.invIndexOnName = new InvertedIndexBST<>(false);
        this.invIndexOnToken = new InvertedIndexBST<>(true);
        this.invIndexOnLat = new InvertedIndexBST<>(false);
        this.invIndexOnLon = new InvertedIndexBST<>(false);

        // B+ Tree version (not finalized)
        // this.bstIndex = new BPT<>(4);
        // this.invIndexOnName = new InvertedIndexBPT<>(false);
        // this.invIndexOnToken = new InvertedIndexBPT<>(true);
    }

    public void start() {

        // Parse csv
        if (!csvParser.parseFile()) {
            logger.logError("Parsing CSV file failed. Please make sure that a proper CGN record CSV has been supplied.");
            return;
        }
        
        // Build indexes
        if (!buildIndexes()) {
            logger.logError("Build indexes failed");
            return;
        }

        // Scan query file and decode it
        if (!commandReader.readQueryFile()) {
            logger.logError("Read query file failed");
            return;
        }

        if (commandReader.size() == 0) {
            logger.logError("No queries found in the query file. Please check formating instructions.");
            return;
        }

        if (csvParser.getLines().isEmpty()) {
            logger.logError("No data found in the cgn record file.");

        }

        // Loop through the list of queries
        for (int i = 0; i < this.commandReader.size(); i++) {
            String query = commandReader.getQuery(i);
            String output = "";

            int instruction = commandReader.getInstruction(i);
            ArrayList<String> data = commandReader.getData(i);

            if(instruction == -1){
                output += "\n" + outputWarning("Query is not formatted properly");
            } else if(data.isEmpty()){
                output += "\n" + outputWarning("Query contains no data");
            }else{
                switch (instruction) {
                    case 1:
                        output += case1(data);
                        break;
                    case 2:
                        output += case2(data);
                        break;
                    case 3:
                        output += case3(data);
                        break;
                    case 4:
                        output += case4(data);
                        break;
                    case 5:
                        output += case5(data);
                        break;
                    case 6:
                        output += case6(data);
                        break;
                    default:
                        return;
                }
            }

            // Output to log //
            logger.addEntry(output, query);
        }
        logger.writeToLogFile();
        System.out.println("Program Finished! Please refer to \"" + logger.getFileName() + "\" for results of query.\n");
    }

    // Build the require indexes for all search opeartions. Returns false if failed.
    private boolean buildIndexes() {
        try {
            SinglyLinkedList<CGNRecord>.SLLIterator iter = this.csvParser.getLines().new SLLIterator(); //SSL iterator
            Instant start = Instant.now(); // Measure elapsed time
            System.out.print("Building Indexes... ");
            while (iter.hasNext()) {
                CGNRecord temp = iter.getCurrent();
                this.bstIndex.add(temp.hashCode(), temp); // build index BST
                this.invIndexOnName.put(temp.getName(), temp);  // build inverted index on names
                this.invIndexOnToken.put(temp.getName(), temp);
                this.invIndexOnLat.put(temp.getLat(), temp); // build inverted index on lat integer
                this.invIndexOnLon.put(temp.getLon(), temp); // build inverted index on lon integer
                iter.next();
            }

            Instant stop = Instant.now(); // Measure elapsed time
            long timeElapsed = Duration.between(start, stop)
                    .toMillis();  //in millis
            System.out.println("Success!! 5 indexes built in " + timeElapsed + "ms\n");
            System.out.println("    > Binary search tree with ID as key: " + bstIndex.size() + " values inserted.");
            System.out.println("    > Inverted index on name: " + invIndexOnName.size() + " values inserted.");
            System.out.println("    > Inverted index on tokens: " + invIndexOnToken.size() + " values inserted.");
            System.out.println("    > Inverted index on latitude: " + invIndexOnLat.size() + " values inserted.");
            System.out.println("    > Inverted index on longitude: " + invIndexOnLon.size() + " values inserted.\n");
            

            return true;
        } catch (Exception e) {
            System.out.print(" Failed\n");
            return false;
        }
    }

    // Case1: Query details of records with name: string1, string2
    private String case1(ArrayList<String> data) {
        String output = "";

        for (String s : data) {
            SinglyLinkedList<CGNRecord> result = invIndexOnName.get(s); //Return record list from inverted index search on string s
            output += "\n-> Details of the records with the name: " + s + "\n\n";

            if (result != null) {
                output += outputRecords(result);
            } else {
                output += outputWarning("No records found");
            }
        }
        return output;
    }

    // Case2: Query records with the substring: string1, string2, ...
    private String case2(ArrayList<String> data) {
        String output = "";

        for (String s : data) {
            SinglyLinkedList<CGNRecord> result = invIndexOnToken.get(s); //Return record list from inverted index search on string s
            output += "\n-> Details of the records containing the substring: " + s + "\n\n";
            if (result != null) {
                output += outputRecords(result);
            } else {
                output += outputWarning("No records found");
            }
        }
        return output;
    }

    // Case3: Query records with the coordinates: latitude, longitude
    private String case3(ArrayList<String> data) {
        String output = "\n";

        if (data.size() != 2) { // Validates the number of data elements for this type of query
            output += outputWarning("To many or to few arguments supplied for this type of query");
            return output;
        }

        // Check input data elements can be converted to decimal for this type of query
        if (!isNumber(data.get(0)) || !isNumber(data.get(1))) {
            output += outputWarning("One of the arguments is not a number");
            return output;
        }

        int lat = CGNRecord.latlonToInt(data.get(0));
        int lon = CGNRecord.latlonToInt(data.get(1));
        SinglyLinkedList<CGNRecord> latRecords = invIndexOnLat.get(lat);
        SinglyLinkedList<CGNRecord> lonRecords = invIndexOnLon.get(lon);
        SinglyLinkedList<CGNRecord> result = findCommonRecords(latRecords, lonRecords);

        if (result != null) {
            output += outputRecords(result);
        } else{
            output += outputWarning("No records found");
        }
        return output;
    }

    // Case4: Query records found between the coordinaates: latitude1, longitude1, latitude2, longitude2    
    private String case4(ArrayList<String> data) {
        String output = "\n";

        if (data.size() != 4) { // Validates 4 data elements for this type of query
            output += outputWarning("To many or to few arguments supplied for this type of query");
            return output;
        }
        // Check input data elements can be converted to decimal for this type of query
        if (!isNumber(data.get(0)) || !isNumber(data.get(1)) || !isNumber(data.get(2)) || !isNumber(data.get(3))) {
            output += outputWarning("One of the arguments is not a number");
            return output;
        }

        int lat1 = CGNRecord.latlonToInt(data.get(0));
        int lat2 = CGNRecord.latlonToInt(data.get(1));
        int lon1 = CGNRecord.latlonToInt(data.get(2));
        int lon2 = CGNRecord.latlonToInt(data.get(3));

        SinglyLinkedList<CGNRecord> latRecords = invIndexOnLat.getInRange(lat1, lat2);
        SinglyLinkedList<CGNRecord> lonRecords = invIndexOnLon.getInRange(lon1, lon2);
        SinglyLinkedList<CGNRecord> result = findCommonRecords(latRecords, lonRecords);

        // Search of inverted index has returned no results
        if (result != null) {
            output += outputRecords(result);
        } else{
            output += outputWarning("No records found between latitudes: " + CGNRecord.latlonToString(lat1) + ", " +
                    CGNRecord.latlonToString(lat2) + " & longitudes: " + CGNRecord.latlonToString(lon1) + ", " +
                    CGNRecord.latlonToString(lon2));
        }

        return output;
    }

    private String case5(ArrayList<String> data) {
        String output = "\n";

        for (String s : data) {
            SinglyLinkedList<CGNRecord> result = invIndexOnName.get(s); //Return record list from inverted index search on string s
            output += "Coordinates of the records with the name: " + s + "\n\n";
            if (result != null) {
                output += outputCoordinates(result);
            } else {
                output += outputWarning("No records found");
            }
        }
        return output;
    }

    private String case6(ArrayList<String> data) {

        String output = "";
        for (String s : data) {
            output += "\nDetails of the record with id: " + s.toUpperCase() + "\n\n";
            
            if(s.length() != 5 || !isWord(s)){
                output += outputWarning("id must contain 5 letters only");
                continue;
            }
            
            int id = CGNRecord.hashId(s);
            CGNRecord result = bstIndex.get(id); //Return list from inverted index for string s

            if (result != null) {
                output += outputRecord(result);
            } else{
                output += outputWarning("No record found");
            }
        }
        return output;
    }

//// HELPER FUNCTIONS FOR CONTROLLER ////

    // Returns string format of a CGN record
    private String outputRecord(CGNRecord rec) {
        String lat = rec.getLatitude();
        String lon = rec.getLongitude();
        String temp = "\t> ID: " + rec.getId().toUpperCase() + "\t/ Name: " + rec.getName() + " / Coordinates: " + lat + ", " + lon + "\n";
        return temp;
    }

    // Returns string format of a CGN record coordinates
    private String outputCoordinate(CGNRecord rec) {
        String lat = rec.getLatitude();
        String lon = rec.getLongitude();
        String temp = "\t> Coordinates: " + lat + ", " + lon;
        return temp;
    }

    // Returns string format of a warning
    private String outputWarning(String s){
        return "\t*** " + s.toUpperCase() + " ***" + "\n";
    }

    // Returns string format of a list of CGN records
    private String outputRecords(SinglyLinkedList<CGNRecord> recs) {
        String temp = "";
        SinglyLinkedList<CGNRecord>.SLLIterator iter = recs.new SLLIterator();
        while (iter.hasNext()) {
            temp += outputRecord(iter.getCurrent());
            iter.next();
        }
        return temp;
    }

    // Returns string format of a list of CGN record coordinates
    private String outputCoordinates(SinglyLinkedList<CGNRecord> recs) {
        String temp = "";
        SinglyLinkedList<CGNRecord>.SLLIterator iter = recs.new SLLIterator();
        while (iter.hasNext()) {
            temp += outputCoordinate(iter.getCurrent()) + "\n";
            iter.next();
        }
        return temp;
    }

    // Function to validate if a string is numeric
    private boolean isNumber(String s) {
        try{
            Double.parseDouble(s);
            return true;
        }catch(Exception e){
            return false;
        }  
    }

    // Function to validate if a word only has letters
    private boolean isWord(String s){
        if(s.matches("^[a-zA-Z]*$")) return true;
        return false;
    }

    // Find commun records in two lists. Returns a new list containing these records or null if none found
    private SinglyLinkedList<CGNRecord> findCommonRecords(SinglyLinkedList<CGNRecord> latList, SinglyLinkedList<CGNRecord> lonList) {
        if (latList == null || lonList == null) return null;
        SinglyLinkedList<CGNRecord> temp = new SinglyLinkedList<>();
        SinglyLinkedList<CGNRecord>.SLLIterator iter1 = latList.new SLLIterator();
        SinglyLinkedList<CGNRecord>.SLLIterator iter2 = lonList.new SLLIterator();
        while (iter1.hasNext()) {
            while (iter2.hasNext()) {
                if (iter1.getCurrent()
                        .equals(iter2.getCurrent())) { // Comparet elements from both lists
                    temp.addLast(iter1.getCurrent());
                }
                iter2.next();
            }
            iter2 = lonList.new SLLIterator();
            iter1.next();
        }
        if (temp.isEmpty()) return null;
        return temp;
    }


    // INDEX STRUCTURE PRINTING FUNCTIONS TO COMMAND LINE (NO RECOMMENDED CONSIDERING SIZE OF STRUCTURES)
    private void printBst(BST<CGNRecord> bst){
        SinglyLinkedList<CGNRecord> list = bst.toList();

        if(list == null){
            System.out.println("Empty BST");
            return;
        }
        int count = 1;
        SinglyLinkedList<CGNRecord>.SLLIterator iter = list.new SLLIterator();
        while(iter.hasNext()){
            System.out.print(count + ": " + iter.getCurrent().getId().toUpperCase() + "\t");
            if(count % 10 == 0) System.out.print("\n");
            count++;
            iter.next();
        }
        System.out.print("\n");
        
    }

    private void printInvertedIndex(InvertedIndexBST<CGNRecord> inv){
        SinglyLinkedList<SinglyLinkedList<CGNRecord>> list = inv.toList();

        if(list == null){
            System.out.println("Empty Inverted Index");
            return;
        }
        int count = 1;
        SinglyLinkedList<SinglyLinkedList<CGNRecord>>.SLLIterator iter1 = list.new SLLIterator();
        while(iter1.hasNext()){
            System.out.print(count + ": [");
            SinglyLinkedList<CGNRecord>.SLLIterator iter2 = iter1.getCurrent().new SLLIterator();
            while(iter2.hasNext()){
                System.out.print(iter2.getCurrent().getId().toUpperCase() + ", ");
                iter2.next();
            }
            System.out.print("]\n");
            count++;
            iter1.next();
        }
        
    }
}



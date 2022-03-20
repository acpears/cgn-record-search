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
CLASS DESCRIPTION: Parser
    - Class that is used to parse a csv into the memory
    - Uses a singly linked list and adds CGN record data elements references to it. 
    - We use the linked list to build our indexes
*/

import java.io.*;
import java.time.Duration;
import java.time.Instant;

public class Parser {
    private SinglyLinkedList<CGNRecord> listOfLines; 
    private int listSize;
    private File file;
    private int[] headerLocation;

    public Parser(File file){
        this.listOfLines = null;
        this.listSize = 0;
        this.file = file;
        this.headerLocation = null;
    }

    public boolean parseFile() {
        
        //trys to read a file
        try {
            Instant start = Instant.now(); // Measure elapsed time

            System.out.print("\nParsing CSV... ");
            
            //File file = new File(fileLocation);
            // this grabs the file we specified and makes a new file object
            FileInputStream fileStream = new FileInputStream(this.file);
            //this takes the file object and passes it into the fileInputStream to create a fileInputStream object
            InputStreamReader input = new InputStreamReader(fileStream);
            //This takes the fileInputSteam Object and makes an InputStreamReader object
            BufferedReader reader = new BufferedReader(input);
            //finally we are making a BufferedReader Objects


            String lineString; //make a string variable to be a read line

            // List of cgn records (lines)
            SinglyLinkedList<CGNRecord> lineList = new SinglyLinkedList<>();

            //get the headerLocation in the file
            lineString = reader.readLine(); //Read first line (aka header line)
            
            if (lineString == null){
                reader.close();
                System.out.print("Failled!! Empty CSV \n");
                return false;
            }
            if(!findHeaderLocation(lineString.toLowerCase())){
                reader.close();
                System.out.print("Failled!! Bad Header Format\n");
                return false;
            }

            //while not at the end of the file
            while ((lineString = reader.readLine()) != null ) {
                //lineString = lineString.toLowerCase();
                
                //if the length is not zero
                if ((lineString.length() != 0)) {

                    //Creat CGN record from CSV line and add it to list
                    String[] lineArray = onlyImportantColumns(headerLocation, lineString);

                    CGNRecord record = new CGNRecord(lineArray[0], lineArray[1], lineArray[2], lineArray[3]); //Create new CGN record object from 
                    lineList.addLast(record);
                }
            }

            this.listOfLines = lineList;
            this.listSize = lineList.getSize();

            //Timer
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();  //in millis
            //-----

            System.out.print("Success!! " + this.listSize + " records parsed in " + timeElapsed + "ms \n");
            reader.close();
            return true;

        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            System.out.print("Failled!! \n");
            return false;
        } catch (IOException e) {
            System.out.print("Failled!! \n");
            //e.printStackTrace();
            return false;
        }
    }

    public SinglyLinkedList<CGNRecord> getLines(){
        return this.listOfLines;
    }

    public int getSize(){
        return this.listSize;
    }

        /**
         * get the location of the headers
         * 
         * @throws Exception
         */
    private boolean findHeaderLocation(String line) {
        
        String[] tokenizedValues = tokenizeLine(line);

        int idLocation, nameLocation, latLocation, longLocation, headerSum;
        idLocation = nameLocation = latLocation = longLocation = headerSum = 0;
        
        
       for (int i = 0; i < tokenizedValues.length; i++) {
            if(tokenizedValues[i].contains("cgndb id")){tokenizedValues[i] = "cgndb id";}
            switch (tokenizedValues[i]) {
                case "\uFEFFcgndb id" :
                case "cgndb id" :
                    idLocation = i;
                    headerSum += 2;
                    continue;
                case "geographical name":
                    nameLocation = i;
                    headerSum += 4;
                    continue;
                case "latitude":
                    latLocation = i;
                    headerSum += 8;
                    continue;
                case "longitude":
                    longLocation = i;
                    headerSum += 16;
                    continue;
                default:
                    continue;
            }
        }
        if (headerSum != 30) {
            return false;
        }

        //return header location
        this.headerLocation = new int[]{idLocation, nameLocation, latLocation, longLocation};
        return true;
    }

    /**
     * remove all unnecessary columns
     */
    private String[] onlyImportantColumns(int[] headers, String lineManipulated) {
        String[] tokens = Parser.tokenizeLine(lineManipulated);
        String[] shortenedArray = new String[4];
        for (int i = 0; i < 4; i++) {
            shortenedArray[i] = tokens[headers[i]];
        }
        return shortenedArray;
    }


    /** put each word in the name into its own token */
    private static String[] tokenizeName(String name) {
        name = name.replaceAll("-", " ");
        String[] tokens = name.split(" ");
        return tokens;
    }

    /** put each word in the name into its own token */
    private static String[] tokenizeLine(String lineManipulated) {
        String[] tokens = lineManipulated.split(",(?=(([^\"]*\"){2})*[^\"]*$)");
        return tokens;
    }

    private boolean isNumber(String s) {
        try{
            Double.parseDouble(s);
            return true;
        }catch(Exception e){
            return false;
        }  
    }

}


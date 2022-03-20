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
CLASS DESCRIPTION: File Handler
    - Methods used to validate the input files required for the program
    - Returns errors to the command line if file formats are not correct
*/

import java.io.*;
import java.util.Scanner;

public class FileHandler {

    /* Requests unput from users. Makes sure its not blank*/
    public static String promptForInput(){
        Scanner scan = new Scanner( System.in );
        String input = scan.nextLine();
        if (input.isEmpty()){System.out.println("Please provide valid input:"); promptForInput();}
        return input;
    }

    /* Determine if user wants to use file name or file path.*/
    public static Boolean fileOrFilepath(String decision){
            if (decision.toLowerCase().equals("filename")){return true;}
            if (decision.toLowerCase().equals("filepath")){return false;}
            System.out.println("Invalid Answer Arg 0. Please try again. Insert ('filename' or 'filepath')");
            System.exit(0);
            return null;
    }

    public static File fullFileCheck(Boolean fileNameOrPath, String filetype, String argument)
            throws Exception {
        String filePath;
            /*Get user input and translate that to the official path.*/
            String userInput = argument.toLowerCase();
            
            /* Make the path */
            if (fileNameOrPath == true){
                filePath = makePath(userInput, filetype);
            } else {
                filePath = userInput;
            }

            /* Check for invalid characters*/
            if(onlyValidChars(userInput) == false){
                //System.out.print("\nFile Error: File name contains one of the invalid characters" + "!@#$%&*()'+,:;<=>?[]^`{|} \n"); 
                throw new IllegalArgumentException("File \"" + argument + "\" contains one of the following invalid characters: \"!@#$%&*()'+,:;<=>?[]^`{|}\" ");
            }
            
            /* Check if file extension is what we are expecting */
            if (validType(filePath, filetype) == false) {
                //System.out.print("\nFile Error: File extension expected: ." + filetype +"\n"); 
                throw new IllegalArgumentException("File \"" + argument + "\" should have the file extension \"."+ filetype +"\"");
            }
            
            /* If log file doesn't exist create it */
            if(filetype == "log" && !validLocation(filePath)){
                if(!createFile(filePath)) {
                    //System.out.print("\nFile Error: Log file creation failed.\n"); 
                    throw new IOException("Log file access error");
                }
            }
            
            /* Check if file exists */
            if (validLocation(filePath) == false) {
                //System.out.print("\nFile Error: File does not exist at: " + filePath +"\n"); 
                throw new FileNotFoundException("File \"" + argument + "\" not found at path: " + filePath);
            }
            /* Check if file contains information or not. Do we want it to? */
            if (isNotEmpty(filePath, filetype) == false) {
                //System.out.print("\nFile Error: File is empty when it should have contents. File type: " + filetype +"\n"); 
                throw new Exception("File \"" + argument + "\" is empty");
            }
        
        /* make and return the file assuming it passed all our checks */
        File FileHandler = new File(filePath);
        return FileHandler;
    }

    /* make sure the file exists */
    public static boolean validLocation(String filePath){
        File tempFile = new File(filePath);
        return tempFile.exists();
    }

    public static boolean createFile(String filePath){
        try{
            File tempFile = new File(filePath);
            return tempFile.createNewFile();
        }catch (IOException e){
            return false;
        }
    }

    /* makes the path */
    public static String makePath(String filename, String  filetype) {
        String location;
        switch (filetype) {
            case "csv": location = "data";break;
            case "txt": location = "queries";break;
            case "log": location = "logs";break;
            default:
                throw new IllegalStateException("Unexpected file: " + filetype);
        }
        /* get present working directory */
        String pwd = System.getProperty("user.dir");
        /* get the input file path by generating the system-default directory separator */
        String filePath = pwd + File.separator + location + File.separator + filename;
        return filePath;
    }

    /* make path simply to src folder independent of filetype */
    public static String makePath(String filename) {
        /* get present working directory */
        String pwd = System.getProperty("user.dir");
        /* get the input file path by generating the system-default directory separator */
        String filePath = pwd + File.separator + filename;
        return filePath;
    }

    public static boolean validType(String filePath, String filetype){
        /* get the extension */
        String extension = null;
        int dot = filePath.lastIndexOf('.');
        if (dot >= 0) { extension = filePath.substring(dot+1); }
        else return false;
        /* is the extension what we are expecting for this call? */
        return extension.equals(filetype);
    }

    public static boolean isNotEmpty(String filePath, String filetype){
        File tempFile = new File(filePath);
        /* if we have a log file, it does not matter if its empty. */
        if (filetype.equals("log")){return true;}
        /* is the file empty? that is no good to us. */
        if (tempFile.length() == 0){return false;}
        return true;
    }

    /* Requests unput from users. Makes sure its not blank*/
    private static boolean onlyValidChars(String userInput){
        String specialCharacters = "!@#$%&*()'+,:;<=>?[]^`{|}";
        /* Check the whwole string to see if the special character exists */
        for (int i=0; i < userInput.length() ; i++)
        {
            char character = userInput.charAt(i);
            if(specialCharacters.contains(Character.toString(character))) { return false;}
        }
        return true;
    }

    /* make the singly linked list out of a file. open the file. Read it. Iterate through it adding the values to a singly linked list  */
    /* At this time, this is ONLY used for stop words  */
    public static SinglyLinkedList<String> makeSinglyLinkedList(String filePath) throws Exception {
        File fileBeingRead = new File(filePath);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(fileBeingRead));
        } catch (FileNotFoundException e) {
            System.out.println("File " + fileBeingRead.getName() + " not found");
            throw new Exception();
        }
        String line = null;
        SinglyLinkedList<String> list = new SinglyLinkedList<String>();
        while (true) {
            try {
                if (!((line = br.readLine()) != null)) break;
            } catch (Exception e) {
                System.out.println("Error reading lines from " + fileBeingRead.getName());
                br.close();
                throw new Exception();
            }
            list.addLast(line.trim());
        }
        br.close();
        return list;
    }

}

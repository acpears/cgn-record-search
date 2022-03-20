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
CLASS DESCRIPTION: MAIN CLASS
    * There are a few requirements for the end user to know.
    *     1. The record file must be a .csv
    *     2. The query file must be a .txt
    *     3. The log file must be a .log file and is the only file that will be created if it does not exist.
    * The user can either decide to enter file names or file paths.
    *     1. In either case the user MUST include the extension (eg. filename.txt or ../filename.txt)
    *     2. If you select the file name option note that the filepath automatically found is from
    *        System.getProperty("user.dir") + src + (data or query or LogWriter).
    *        Results will differ if you are working inside or outside an IDE. To be safe, put the full path.
    *     3. It is expected the query and record file are not empty. The log file will be over written
    *        if its not empty.
    *     4. The log file MUST exist prior to running the program.
    *
    * Syntax:  <naming syntax> <data file> <query file> <log file>
    *          e.g: filename data.csv query.txt log.log
    *          e.g: filepath C:/path/to/data.csv C:/path/to/query.txt C:/path/to/log.log
*/

import LogWriter.LogWriter;
import java.io.File;
import java.io.FileNotFoundException;

class Main {

    public static void main(String[] args) {

        File logFile;
        File csvFile;
        File queriesFile;
        LogWriter logger;
        Parser cgndParser;
        CommandReader commandReader;

        // Validates that 3 arguments have been supplied to the command line 
        if(args.length != 3){
            printError("Please input to the command line a data file (.csv), query file (.txt) & log file (.log)");
            return;
        }

        // Try block for log file. Program will print to command line if there is an error
        // with the log file. All other errors will be logged to the log file.
        try{
            logFile = FileHandler.fullFileCheck(true, "log", args[2]);
            System.out.println("\nLOG .LOG FILE LOADED - " + logFile.getAbsolutePath());
            
            //Instantiate a logger with the log file received from the command line.
            logger = new LogWriter(logFile); 

        }catch (Exception e) {
            printError(e.getMessage());
            //e.printStackTrace();
            return;
        } 
        
        try{
            csvFile = FileHandler.fullFileCheck(true, "csv", args[0]);
            System.out.println("RECORD .CSV FILE LOADED - " + csvFile.getAbsolutePath());
            
            queriesFile = FileHandler.fullFileCheck(true, "txt", args[1]);
            System.out.println("QUERY .TXT FILE LOADED - " + queriesFile.getAbsolutePath());

            //Instantiate a cgnd parser with the csv file from the command line
            cgndParser = new Parser(csvFile);

            //Instantiate a command reader with the script file from the command line.
            commandReader = new CommandReader(queriesFile);

            //Loading of stop words from file "stop-words.txt" for inverted index
            if(!InvertedIndexBST.loadStopWords("stop-words.txt")){
                System.out.println("Stop words: Loading failed");
            }else{
                System.out.println("Stop words: Loaded");
            }
            
            // Start the main application controller
            new Controller(cgndParser, commandReader, logger).start();
            
        } catch (FileNotFoundException e){
            logger.logError(e.getMessage());
            printError(e.getMessage());
            //e.printStackTrace();
            return;
        } catch (IllegalArgumentException e){
            logger.logError(e.getMessage());
            printError(e.getMessage());
            //e.printStackTrace();
            return;
        }catch (Exception e){
            logger.logError(e.getMessage());
            printError(e.getMessage());
            //e.printStackTrace();
            return;
        }
    }

    private static void printError(String err){
        System.out.println("\nERROR... Program Exiting: " + err + "\n");
    }
}

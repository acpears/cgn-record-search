package LogWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A simple {@code LogWriter} class backed by an {@code ArrayList<String>}
 * to accumulate log entry strings. A <i>log entry</i> is the result of a query
 * preformatted into a string. The query text itself can be included in the log too
 * as text prepended to the entry and specifically formatted of clarity.
 * The actual writing to a file is done by means of a dedicated method.
 * <strong>Note that following a successful writing to a file all entries
 * are deleted from the {@code ArrayList} and thus should be called at the very end
 * of the program</strong>.
 */
public class LogWriter {

    private final List<String> logEntries;
    private final File logFile;

    // get system-default end-of-line character(s)
    private static final String EOL = System.getProperty("line.separator");

    /**
     * Creates an new instance of LogWriter with an empty ArrayList of log entries.
     *
     * @param logFile File to store the list of entries in this LogWriter
     */
    public LogWriter(File logFile) {
        this.logEntries = new ArrayList<>();
        this.logFile = logFile;
    }

    /**
     * Removes all entries from this LogWriter instance.
     * Provided for the case when entry reset is needed without first writing all entries to file.
     */
    public void clearLog() {
        this.logEntries.clear();
        writeToLogFile();
    }


    /**
     * Appends an entry to this LogWriter's list of entries.
     *
     * @param entry the entry string to appended to this {@code LogWriter}'s list of entries
     */
    public void addEntry(String entry) {
        this.logEntries.add(entry);
    }

    /**
     * Appends an entry to this LogWriter's list of entries prepended with the given text.
     * Meant for including the query string in the log file like so:
     * <pre>
     * LogWriter logWriter = new LogWriter();
     * logwriter.addEntry(queryResult, queryText);
     * </pre>
     * For clarity, the prepended text will be indented and preceded with special characters:
     * <pre>
     * +--
     * | Query names: "lake", "Abbot", "Hill"
     * +--
     * </pre>
     *
     * @param entry          the entry string to appended to this {@code LogWriter}'s list of entries
     * @param prependingText the text that precedes the specified entry string
     */
    public void addEntry(String entry, String prependingText) {
        if (entry == null || entry.isBlank()) {
            return;
        }
        if (prependingText == null || prependingText.isBlank()) {
            addEntry(entry);
            return;
        }
        String prependedEntry;

        prependedEntry =    "   +--" + EOL +
                            "   | QUERY: "+prependingText  + EOL +
                            "   +--" + EOL + EOL +
                            "RESULT:" +
                            EOL +
                            entry;

        addEntry(prependedEntry);
    }

    public void logError(String error){
        if (error == null || error.isBlank()) {
            return;
        }

        String output;
        output = "ERROR: " + error + EOL;
        clearLog();
        addEntry(output);
        writeToLogFile();
    }

    /**
     * Writes all log entries to the specified log file, then clears all entries.
     * If an exception is thrown while attempting to write to file,
     * the entries will not be cleared.
     *
     * @return {@code true} if writing to file was successful, or {@code false} otherwise
     */
    public boolean writeToLogFile() {
        if (logFile == null) {
            return false;
        }

        String allEntryString = IntStream.rangeClosed(1, logEntries.size())
                .mapToObj(i -> logEntries.get(i - 1) + EOL
                        + "-----------------------------------------------------"
                        /*+ String.format("     e n d   o f   q u e r y   %2d", i) + EOL*/)
                .collect(Collectors.joining(
                        EOL 
                                ,
                        "========== BEGINNING OF LOG ==========" + EOL + // log header line 1
                                                                // log header line 2
                                EOL,                                 // log header line 3
                        EOL +                                  // log footer line 1
                                EOL +                                // log footer line 2
                                "========== END OF LOG =========="));          // log footer line 3

        try (Writer writer = new BufferedWriter(new FileWriter(logFile,
                false)) // overwrite log if it already exists
        ) {
            // uncomment the following two lines to create the log file in case it does not exist
            // File log = new File(filePath);
            // log.createNewFile();
            
            writer.write(allEntryString);
            this.logEntries.clear(); // will not execute if exception occurs

        } catch (IOException e) {

            return false;
        }

        return true;
    }

    /**
     * Return a formatted {@code String} representation of the entry list in this LogWriter
     * Provided for debugging only (the returned {@code String } is not meant to be shown to the user).
     */
    @Override
    public String toString() {
        return "LogWriter{" +
                "logEntries=" + logEntries +
                '}';
    }

    public String getFileName(){
        return logFile.getName();
    }
}

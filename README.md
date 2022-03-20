# CGN Record Search

## Description

Simple java cli application to search Canada's CGN record CSVs

---
## Steps to run the program in terminal or cmd
1. Download or clone the repository
2. CD to the directory of the respository
3. Run the java command: ```javac *.java```
4. Run the program by executing command: ```java Main <CGN csv file name> <query file name> <log file name>```

eg. ```java Main cgn_on_csv_eng.csv query-example.txt logfile.log```


## INPUT FILES:

### Input CGN CSV File:
- CGN csv files must be located in '/src/data/'
- The headers of the csv files must contain at a minimum: "CGNDB ID", "Geographical Name", "Latitude" & "Longitude"
- Any english CGN csv file from the Geographic Names Information System can be used (https://open.canada.ca/data/en/dataset/e27c6eba-3c5d-4051-9db2-082dc6411c2c)

### Query File:
- Query files must be located in '/src/queries/'
- Query files must be a plain text with .txt extension
- Please refer to query-example.txt for formatting instructions of the query file

### Log File:
- Log files must be located in '/src/logs/'
- The log file is the only input file that will be created if it does not exists
- Log file will be overwritten every time the program is run

### General Notes:
- Working directory must be "src"!!!
- Example log file include in '/src/logs/'
- Command line will output information about the program but all results will be written to the log file
- All errors besides log file errors will be output to the log file
- Parsing of the CSV take a while since we discovered errors in the csv file. Some strings between comma separated values contain an extra comma. This causes the csv to behave unpredictably and parsing more difficult and lengthy.
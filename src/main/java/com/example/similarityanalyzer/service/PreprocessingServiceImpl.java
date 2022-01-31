package com.example.similarityanalyzer.service;

import gnu.trove.map.hash.TIntByteHashMap;
import gnu.trove.set.hash.TLongHashSet;

import java.io.*;
import java.util.Scanner;

public class PreprocessingServiceImpl implements PreprocessingService {

    private final String _pathToInputFile;
    private final String _pathToOutputFile;
    private final String _pathToUniquePagesFile;
    private final String _pathToUniqueTimestampsFile;
    private final String _pathToUniqueTimestampsNormalizedFile;

    private static final String COMMA_DELIMITER = ",";

    public PreprocessingServiceImpl(String pathToInputFile,
                                    String pathToOutputFile,
                                    String pathToUniquePagesFile,
                                    String pathToUniqueTimestampsFile,
                                    String pathToUniqueTimestampsNormalizedFile) {
        _pathToInputFile = pathToInputFile;
        _pathToOutputFile = pathToOutputFile;
        _pathToUniquePagesFile = pathToUniquePagesFile;
        _pathToUniqueTimestampsFile = pathToUniqueTimestampsFile;
        _pathToUniqueTimestampsNormalizedFile = pathToUniqueTimestampsNormalizedFile;
    }

    /**
     * Function to find max length of row in file from pathToFile
     * @return max length of row
     * @throws IOException when cannot open file
     */
    private int findMaxLengthOfRow(String pathToFile) throws IOException {
        int lineLength, maxLineLength = 0;
        try (Scanner scanner = new Scanner(new FileInputStream(pathToFile))){
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lineLength = line.length();
                if (lineLength > maxLineLength) maxLineLength = lineLength;
            }
            if (scanner.ioException() != null) {
                throw scanner.ioException();
            }
        }
        return maxLineLength;
    }

    private void normalizeLengthOfRows(int lengthToNormalize, String pathToInputFile,String pathToOutputFile) throws IOException {
        try (Scanner scanner = new Scanner(new FileInputStream(pathToInputFile));
             FileOutputStream outputStream = new FileOutputStream(pathToOutputFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String newLine = line + " ".repeat(lengthToNormalize - line.length()) + "\n";
                byte[] strToBytes = newLine.getBytes();
                outputStream.write(strToBytes);
            }
            if (scanner.ioException() != null) {
                throw scanner.ioException();
            }
        }
    }

    private void createFilesWithUniqueFields() throws IOException {
        // !FIXME Use hashSet instead of HashMap
        TIntByteHashMap uniquePages = new TIntByteHashMap();
        TIntByteHashMap uniqueTimestamps = new TIntByteHashMap();
        int page;
        int timestamp;

        try (FileInputStream inputStream = new FileInputStream(_pathToInputFile);
             Scanner scanner = new Scanner(inputStream);
             FileOutputStream outputStream = new FileOutputStream(_pathToUniquePagesFile);
             FileOutputStream timestampsOutputStream = new FileOutputStream(_pathToUniqueTimestampsFile)) {
            int lineNumber = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                try (Scanner rowScanner = new Scanner(line)) {
                    rowScanner.useDelimiter(COMMA_DELIMITER);
                    while (rowScanner.hasNext()) {
                        rowScanner.nextLong();
                        page = rowScanner.nextInt();
                        timestamp = rowScanner.nextInt();
                        if (!uniquePages.containsKey(page)) {
                            uniquePages.put(page, (byte) 1);
                            String newLine = page + "\n";
                            byte[] strToBytes = newLine.getBytes();
                            outputStream.write(strToBytes);
                        }
                        if (!uniqueTimestamps.containsKey(timestamp)) {
                            uniqueTimestamps.put(timestamp, (byte) 1);
                            String newLine = lineNumber + "," + timestamp + "\n";
                            byte[] strToBytes = newLine.getBytes();
                            timestampsOutputStream.write(strToBytes);
                        }
                    }
                }
                lineNumber++;
            }
        }
    }

    public int normalizeLengthOfRows() {
        int maxLengthOfRow = 0;
        try {
            maxLengthOfRow = findMaxLengthOfRow(_pathToInputFile);
            normalizeLengthOfRows(maxLengthOfRow, _pathToInputFile, _pathToOutputFile);
        }
        catch (IOException exception){
            System.err.println("ERROR!!!");
        }
        return maxLengthOfRow;
    }

    public int createUtilityFiles(){
        int maxLengthOfRowInUniqueTimestamps = 0;
        try {
            createFilesWithUniqueFields();
            maxLengthOfRowInUniqueTimestamps = findMaxLengthOfRow(_pathToUniqueTimestampsFile);
            normalizeLengthOfRows(maxLengthOfRowInUniqueTimestamps, _pathToUniqueTimestampsFile, _pathToUniqueTimestampsNormalizedFile);
        } catch (IOException exception){
            System.err.println(exception.getMessage());
        }
        return maxLengthOfRowInUniqueTimestamps;
    }

    public void createCountOLAP(int page) throws IOException{
        TLongHashSet uniqueUid = new TLongHashSet();
        long uid = 0;
        int currentPage = 0;
        int currentTimestamp=0, prevTimestamp=0;
        int count = 0;

        try (FileInputStream inputStream = new FileInputStream(_pathToInputFile);
             Scanner scanner = new Scanner(inputStream);
             FileOutputStream outputStream = new FileOutputStream("test_OLAP.csv")) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                try (Scanner rowScanner = new Scanner(line)) {
                    rowScanner.useDelimiter(COMMA_DELIMITER);
                    while (rowScanner.hasNext()) {
                        uid = rowScanner.nextLong();
                        currentPage = rowScanner.nextInt();
                        currentTimestamp = rowScanner.nextInt();
                    }
                    if (currentTimestamp != prevTimestamp) {
                        String newLine = currentTimestamp + "," + count + "\n";
                        byte[] strToBytes = newLine.getBytes();
                        outputStream.write(strToBytes);
                    }
                    prevTimestamp = currentTimestamp;
                    if (page == currentPage && !uniqueUid.contains(uid)){
                        count++;
                        uniqueUid.add(uid);
                    }
                }
            }
        }
    }

    @Override
    public int preprocess() {
        normalizeLengthOfRows();
        createUtilityFiles();
        try {
            createCountOLAP(15448);
        }
        catch (IOException exception) {
            System.err.println(exception.getMessage());
        }
        return 0;
    }
}

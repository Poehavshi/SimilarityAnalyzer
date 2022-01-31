package com.example.similarityanalyzer.service;

import gnu.trove.set.hash.TIntHashSet;
import gnu.trove.set.hash.TLongHashSet;

import java.io.*;
import java.util.Scanner;

public class PreprocessingServiceImpl implements PreprocessingService {

    private final String _pathToInputFile;
    private final String _pathToOutputFile;
    private final String _pathToUniquePagesFile;
    private final String _pathToUniqueTimestampsFile;
    private final String _pathToUniqueTimestampsNormalizedFile;
    private final String _pathToOLAP;

    private static final String COMMA_DELIMITER = ",";

    public PreprocessingServiceImpl(String pathToInputFile,
                                    String pathToOutputFile,
                                    String pathToUniquePagesFile,
                                    String pathToUniqueTimestampsFile,
                                    String pathToUniqueTimestampsNormalizedFile,
                                    String pathToOLAP) {
        _pathToInputFile = pathToInputFile;
        _pathToOutputFile = pathToOutputFile;
        _pathToUniquePagesFile = pathToUniquePagesFile;
        _pathToUniqueTimestampsFile = pathToUniqueTimestampsFile;
        _pathToUniqueTimestampsNormalizedFile = pathToUniqueTimestampsNormalizedFile;
        _pathToOLAP = pathToOLAP;
    }

    /**
     * Function to find max length of row in file from pathToFile
     *
     * @return max length of row
     * @throws IOException when cannot open file
     */
    private int findMaxLengthOfRow(String pathToFile) throws IOException {
        int lineLength, maxLineLength = 0;
        try (Scanner scanner = new Scanner(new FileInputStream(pathToFile))) {
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


    private void normalizeLengthOfRows(int lengthToNormalize, String pathToInputFile, String pathToOutputFile) throws IOException {
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
        TIntHashSet uniquePages = new TIntHashSet();
        TIntHashSet uniqueTimestamps = new TIntHashSet();
        int page;
        int timestamp;

        try (FileInputStream inputStream = new FileInputStream(_pathToInputFile);
             Scanner scanner = new Scanner(inputStream);
             FileOutputStream outputStream = new FileOutputStream(_pathToUniquePagesFile);
             FileOutputStream timestampsOutputStream = new FileOutputStream(_pathToUniqueTimestampsFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                try (Scanner rowScanner = new Scanner(line)) {
                    rowScanner.useDelimiter(COMMA_DELIMITER);
                    while (rowScanner.hasNext()) {
                        rowScanner.nextLong();
                        page = rowScanner.nextInt();
                        timestamp = rowScanner.nextInt();
                        if (!uniquePages.contains(page)) {
                            uniquePages.add(page);
                            String newLine = page + "\n";
                            byte[] strToBytes = newLine.getBytes();
                            outputStream.write(strToBytes);
                        }
                        if (!uniqueTimestamps.contains(timestamp)) {
                            uniqueTimestamps.add(timestamp);
                            String newLine = timestamp + "\n";
                            byte[] strToBytes = newLine.getBytes();
                            timestampsOutputStream.write(strToBytes);
                        }
                    }
                }
            }
        }
    }

    public int normalizeLengthOfRows() {
        int maxLengthOfRow = 0;
        try {
            maxLengthOfRow = findMaxLengthOfRow(_pathToInputFile);
            normalizeLengthOfRows(maxLengthOfRow, _pathToInputFile, _pathToOutputFile);
        } catch (IOException exception) {
            System.err.println("ERROR!!!");
        }
        return maxLengthOfRow;
    }

    public int createUtilityFiles() {
        int maxLengthOfRowInUniqueTimestamps = 0;
        try {
            createFilesWithUniqueFields();
            maxLengthOfRowInUniqueTimestamps = findMaxLengthOfRow(_pathToUniqueTimestampsFile);
            normalizeLengthOfRows(maxLengthOfRowInUniqueTimestamps, _pathToUniqueTimestampsFile, _pathToUniqueTimestampsNormalizedFile);
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
        }
        return maxLengthOfRowInUniqueTimestamps;
    }

    public void createUniqueUIDsOfIndividualPage(int page) throws IOException {
        TLongHashSet uniqueUIDs = new TLongHashSet();
        long uid = 0;
        int currentPage = 0;
        int currentTimestamp = 0, prevTimestamp = 0;
        if (new File(_pathToOLAP + "individual/" + page + "/").mkdirs()) {
            try (FileInputStream inputStream = new FileInputStream(_pathToInputFile);
                 Scanner scanner = new Scanner(inputStream)) {
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
                            try (FileOutputStream fileOutputStream = new FileOutputStream(_pathToOLAP + "individual/" + page + "/" + currentTimestamp);
                                 ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
                                uniqueUIDs.writeExternal(objectOutputStream);
                                uniqueUIDs = new TLongHashSet();
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                        prevTimestamp = currentTimestamp;
                        if (page == currentPage && !uniqueUIDs.contains(uid)) {
                            uniqueUIDs.add(uid);
                        }
                    }
                }
            }
        }
    }

    public void createCountOLAP() throws IOException {
        if (new File(_pathToOLAP + "individual/").mkdirs()) {
            try (Scanner scanner = new Scanner(new FileInputStream(_pathToUniquePagesFile))) {
                while (scanner.hasNext()) {
                    int page = scanner.nextInt();
                    createUniqueUIDsOfIndividualPage(page);
                }
                if (scanner.ioException() != null) {
                    throw scanner.ioException();
                }
            }
        }
    }


    @Override
    public int preprocess() {
        normalizeLengthOfRows();
        createUtilityFiles();
        try {
            createCountOLAP();
        }
        catch (IOException exception) {
            System.err.println(exception.getMessage());
        }
        return 0;
    }
}

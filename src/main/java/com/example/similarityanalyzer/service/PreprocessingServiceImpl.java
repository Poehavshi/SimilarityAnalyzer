package com.example.similarityanalyzer.service;

import gnu.trove.map.hash.TIntByteHashMap;
import gnu.trove.map.hash.TLongIntHashMap;

import java.io.*;
import java.util.Scanner;

public class PreprocessingServiceImpl implements PreprocessingService {

    private final String _pathToInputFile;
    private final String _pathToOutputFile;
    private final String _pathToUniquePagesFile;

    private static final String COMMA_DELIMITER = ",";

    public PreprocessingServiceImpl(String pathToInputFile, String pathToOutputFile, String pathToUniquePagesFile) {
        _pathToInputFile = pathToInputFile;
        _pathToOutputFile = pathToOutputFile;
        _pathToUniquePagesFile = pathToUniquePagesFile;
    }

    /**
     * Function to find max length of row in file from pathToFile
     * @return max length of row
     * @throws IOException when cannot open file
     */
    private int findMaxLengthOfRow() throws IOException {
        FileInputStream inputStream = null;
        Scanner scanner = null;
        int lineLength, maxLineLength = 0;
        try {
            inputStream = new FileInputStream(_pathToInputFile);
            scanner = new Scanner(inputStream);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lineLength = line.length();
                if (lineLength > maxLineLength) maxLineLength = lineLength;
            }
            if (scanner.ioException() != null) {
                throw scanner.ioException();
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (scanner != null) {
                scanner.close();
            }
        }
        return maxLineLength;
    }

    private void normalizeLengthOfRows(int lengthToNormalize) throws IOException {
        FileInputStream inputStream = null;
        Scanner scanner = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(_pathToInputFile);
            outputStream = new FileOutputStream(_pathToOutputFile);
            scanner = new Scanner(inputStream);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String newLine = line + " ".repeat(lengthToNormalize-line.length()) +"\n";
                byte[] strToBytes = newLine.getBytes();
                outputStream.write(strToBytes);
            }
            if (scanner.ioException() != null) {
                throw scanner.ioException();
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    private void createFileWithUniquePages() throws IOException {
        TIntByteHashMap uniquePages = new TIntByteHashMap();
        long uid = 0;
        int page = 0;
        int timestamp = 0;
        FileInputStream inputStream = null;
        Scanner scanner = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(_pathToInputFile);
            scanner = new Scanner(inputStream);
            outputStream = new FileOutputStream(_pathToUniquePagesFile);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                try (Scanner rowScanner = new Scanner(line)) {
                    rowScanner.useDelimiter(COMMA_DELIMITER);
                    while (rowScanner.hasNext()) {
                        uid = rowScanner.nextLong();
                        page = rowScanner.nextInt();
                        timestamp = rowScanner.nextInt();
                        if (!uniquePages.containsKey(page)) {
                            uniquePages.put(page,(byte) 1);
                            String newLine = page + "\n";
                            byte[] strToBytes = newLine.getBytes();
                            outputStream.write(strToBytes);
                        }
                    }
                }
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (scanner != null) {
                scanner.close();
            }
            if (outputStream != null){
                outputStream.close();
            }
        }
    }

    @Override
    public int preprocess() {
        int maxLengthOfRow = 0;
        try {
            maxLengthOfRow = findMaxLengthOfRow();
            normalizeLengthOfRows(maxLengthOfRow);
            createFileWithUniquePages();
            return maxLengthOfRow;
        }
        catch (IOException exception){
            System.err.println("ERROR!!!");
        }
        return maxLengthOfRow;
    }
}

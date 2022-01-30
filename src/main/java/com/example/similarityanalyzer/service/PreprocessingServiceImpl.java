package com.example.similarityanalyzer.service;

import gnu.trove.map.hash.TLongIntHashMap;

import java.io.*;
import java.util.Scanner;

public class PreprocessingServiceImpl implements PreprocessingService {

    private final String _pathToInputFile;
    private final String _pathToOutputFile;
    private final String _pathToUniqueUIDsFile;

    public PreprocessingServiceImpl(String pathToInputFile, String pathToOutputFile, String pathToUniqueUIDsFile) {
        _pathToInputFile = pathToInputFile;
        _pathToOutputFile = pathToOutputFile;
        _pathToUniqueUIDsFile = pathToUniqueUIDsFile;
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

    private void createFileWithUniqueUIDs() throws IOException{
        TLongIntHashMap uniqueUIDs = new TLongIntHashMap();
        FileInputStream inputStream = null;
        Scanner scanner = null;
        BufferedWriter out = null;
        try {
            inputStream = new FileInputStream(_pathToInputFile);
            out = new BufferedWriter(new FileWriter(_pathToUniqueUIDsFile));
            scanner = new Scanner(inputStream);
            scanner.useDelimiter(",");
            long uid;
            while (scanner.hasNextLine()) {
                uid = scanner.nextLong();
                if (!uniqueUIDs.containsKey(uid)) {
                    uniqueUIDs.put(uid,0);
                    out.write(uid + "\n");
                }
                for (int i = 0;i<2;i++) scanner.nextInt();
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
            if (out != null){
                out.close();
            }
        }
    }

    @Override
    public int preprocess() {
        int maxLengthOfRow = 0;
        try {
            maxLengthOfRow = findMaxLengthOfRow();
            normalizeLengthOfRows(maxLengthOfRow);
            createFileWithUniqueUIDs();
            return maxLengthOfRow;
        }
        catch (IOException exception){
            System.err.println("ERROR!!!");
        }
        return maxLengthOfRow;
    }
}

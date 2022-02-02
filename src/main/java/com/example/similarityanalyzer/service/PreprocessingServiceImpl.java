package com.example.similarityanalyzer.service;

import au.com.bytecode.opencsv.CSVReader;
import gnu.trove.set.hash.TIntHashSet;
import gnu.trove.set.hash.TLongHashSet;

import java.io.*;
import java.util.Scanner;

public class PreprocessingServiceImpl implements PreprocessingService {

    private final String _pathToInputFile;
    private final String _pathToUniquePagesFile;
    private final String _pathToUniqueTimestampsFile;
    private final String _pathToOLAP;

    private final int _timeIntervalInSeconds;

    private static final String COMMA_DELIMITER = ",";

    public PreprocessingServiceImpl(String name, String pathToInputFile) {
        _pathToInputFile = pathToInputFile;
        _pathToUniquePagesFile = name+"unique_pages.csv";
        _pathToUniqueTimestampsFile = name+"unique_timestamps.csv";
        _pathToOLAP = name+"OLAP/";
        _timeIntervalInSeconds = 5;
    }

    private void createFilesWithUniqueTimestampsAndPages() throws IOException {
        // FIXME this method can count number of lines in unique timestamps and return it to controller binary search method
        TIntHashSet uniquePages = new TIntHashSet();

        try (CSVReader reader = new CSVReader(new FileReader(_pathToInputFile), ',', '"', 1);
             BufferedWriter pagesBufferedWriter = new BufferedWriter(new FileWriter(_pathToUniquePagesFile));
             BufferedWriter timestampBufferedWriter = new BufferedWriter(new FileWriter(_pathToUniqueTimestampsFile))) {
            int prevRecordedTimestamp = 0;
            String[] values;
            while ((values = reader.readNext()) != null) {
                int page = Integer.parseInt(values[1]);
                if (!uniquePages.contains(page)) {
                    uniquePages.add(page);
                    String pageString = values[1] + "\n";
                    pagesBufferedWriter.write(pageString);
                }

                int currentTimestamp = Integer.parseInt(values[2]);
                if (currentTimestamp - prevRecordedTimestamp >= _timeIntervalInSeconds) {
                    prevRecordedTimestamp = currentTimestamp;
                    String timestampString = values[2] + "\n";
                    timestampBufferedWriter.write(timestampString);
                }
            }
        }
    }

    public void createUniqueUIDsOfIndividualPage(int page) throws IOException {
        // !FIXME create indexes for several pages like 50 pages or greater?
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
    public void preprocess() {
        try {
            createFilesWithUniqueTimestampsAndPages();
            createCountOLAP();
        }
        catch (IOException exception) {
            System.err.println(exception.getMessage());
        }
    }
}

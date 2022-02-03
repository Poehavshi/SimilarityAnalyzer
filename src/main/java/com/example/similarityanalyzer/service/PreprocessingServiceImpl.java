package com.example.similarityanalyzer.service;

import au.com.bytecode.opencsv.CSVReader;
import gnu.trove.set.hash.TIntHashSet;
import gnu.trove.set.hash.TLongHashSet;

import java.io.*;
import java.util.HashMap;

public class PreprocessingServiceImpl implements PreprocessingService {

    private static final char COMMA_DELIMITER = ',';
    private final String _pathToInputFile;
    private final String _pathToUniquePagesFile;
    private final String _pathToUniqueTimestampsFile;
    private final String _pathToOLAP;
    private final int _timeIntervalInSeconds;

    public PreprocessingServiceImpl(String pathToInputFile,
                                    String pathToUniquePagesFile,
                                    String pathToUniqueTimestampsFile,
                                    String pathToOLAP,
                                    int timeIntervalInSeconds) {
        _pathToInputFile = pathToInputFile;
        _pathToUniquePagesFile = pathToUniquePagesFile;
        _pathToUniqueTimestampsFile = pathToUniqueTimestampsFile;
        _pathToOLAP = pathToOLAP;
        _timeIntervalInSeconds = timeIntervalInSeconds;
    }

    private void createFilesWithUniqueTimestampsAndPages() throws IOException {
        TIntHashSet uniquePages = new TIntHashSet();

        try (CSVReader reader = new CSVReader(new FileReader(_pathToInputFile), COMMA_DELIMITER, '"', 1); BufferedWriter pagesBufferedWriter = new BufferedWriter(new FileWriter(_pathToUniquePagesFile)); BufferedWriter timestampBufferedWriter = new BufferedWriter(new FileWriter(_pathToUniqueTimestampsFile))) {
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

    private void createUniqueUIDsSetsOfPages() throws IOException {
        HashMap<Integer, TLongHashSet> pages = new HashMap<>();
        File directory = new File(_pathToOLAP);
        if (!directory.exists()) directory.mkdir();

        try (CSVReader reader = new CSVReader(new FileReader(_pathToInputFile), COMMA_DELIMITER, '"', 1)) {
            int prevRecordedTimestamp = 0;
            String[] values;
            while ((values = reader.readNext()) != null) {
                long uid = Long.parseLong(values[0]);
                int page = Integer.parseInt(values[1]);

                if (pages.containsKey(page)) {
                    pages.get(page).add(uid);
                } else {
                    TLongHashSet uniqueUIDs = new TLongHashSet();
                    uniqueUIDs.add(uid);
                    pages.put(page, uniqueUIDs);
                }

                int currentTimestamp = Integer.parseInt(values[2]);
                if (currentTimestamp - prevRecordedTimestamp >= _timeIntervalInSeconds) {
                    prevRecordedTimestamp = currentTimestamp;
                    for (int pageKey : pages.keySet()) {
                        File pageDirectory = new File(_pathToOLAP + pageKey + "/");
                        if (!pageDirectory.exists()) pageDirectory.mkdir();
                        try (FileOutputStream fileOutputStream = new FileOutputStream(_pathToOLAP + pageKey + "/" + currentTimestamp); ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
                            pages.get(pageKey).writeExternal(objectOutputStream);
                        }
                    }
                    pages = new HashMap<>();
                }
            }
        }
    }

    @Override
    public void preprocess() {
        try {
            createFilesWithUniqueTimestampsAndPages();
            createUniqueUIDsSetsOfPages();
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
        }
    }
}

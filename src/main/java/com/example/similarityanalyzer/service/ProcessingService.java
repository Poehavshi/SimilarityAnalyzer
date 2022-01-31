package com.example.similarityanalyzer.service;


import java.io.IOException;
import java.util.ArrayList;

public interface ProcessingService {
    /**
     * Returns all unique page field values found in the log.
     */
    void readUniquePages(String pathToUniquePagesFile) throws IOException;

    /**
     * Return Jaccard Similarity metrics for pages page1 and page2 for
     * time interval ["from","to")
     * @return Jaccard Similarity metrics
     */
    double getSimilarity(int page1, int page2, int from, int to) throws IOException;

    /**
     * Getter for uniquePages field
     * @return uniquePages
     */
    ArrayList<Integer> getUniquePages();

    /**
     * preprocess UniqueTimestamps file to find his lengthOfRow and number of lines
     * @param pathToUniqueTimestampsFile path to file with timestamps
     * @throws IOException when cannot open uniqueTimestampsFile
     */
    void preProcessUniqueTimestamps(String pathToUniqueTimestampsFile) throws IOException;
}
package com.example.similarityanalyzer.service;


import java.io.IOException;
import java.util.ArrayList;

public interface ProcessingService {
    /**
     * Returns all unique page field values found in the log.
     * @return list of unique pages
     */
    ArrayList<Integer> readUniquePages() throws IOException;

    /**
     * Return Jaccard Similarity metrics for pages page1 and page2 for
     * time interval ["from","to")
     * @return Jaccard Similarity metrics
     */
    double getSimilarity(int page1, int page2, int from, int to);
}
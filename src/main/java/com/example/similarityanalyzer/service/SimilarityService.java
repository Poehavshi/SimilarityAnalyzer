package com.example.similarityanalyzer.service;

import java.util.List;

public interface SimilarityService {
    /**
     * Returns all unique page field values found in the log.
     * @return list of unique pages
     */
    List<Integer> readUniquePages();

    /**
     * Return Jaccard Similarity metrics for pages page1 and page2 for
     * time interval [from, to)
     * @return Jaccard Similarity metrics
     */
    Double getSimilarity(int page1, int page2, int from, int to);
}
package com.example.similarityanalyzer.service;

import gnu.trove.list.array.TIntArrayList;

import java.util.List;

public interface SimilarityService {
    /**
     * Returns all unique page field values found in the log.
     * @return list of unique pages
     */
    TIntArrayList readUniquePages();

    /**
     * Return Jaccard Similarity metrics for pages page1 and page2 for
     * time interval [from, to)
     * @return Jaccard Similarity metrics
     */
    double getSimilarity(int page1, int page2, int from, int to);
}
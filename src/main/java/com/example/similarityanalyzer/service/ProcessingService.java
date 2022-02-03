package com.example.similarityanalyzer.service;


import gnu.trove.set.hash.TLongHashSet;

import java.io.IOException;
import java.util.ArrayList;

public interface ProcessingService {


    /**
     * Calculate Jaccard Index of two sets set1 and set2
     * K_J = c/(a+b-c), where c = |set1 ⋂ set2|; a = |set1|; b = |set2|
     * https://en.wikipedia.org/wiki/Jaccard_index
     *
     * @param set1 first set
     * @param set2 second set
     * @return Jaccard index of two sets
     */
    double getJaccardIndex(TLongHashSet set1, TLongHashSet set2);

    /**
     * Return Jaccard Similarity metrics for pages page1 and page2 for
     * time interval ["from","to")
     *
     * @return Jaccard Similarity metrics
     */
    double getSimilarity(int page1, int page2, int from, int to) throws IOException;

    /**
     * Getter for uniquePages field
     *
     * @return uniquePages
     */
    ArrayList<Integer> getUniquePages();


    /**
     * Setup OLAP
     * @param pathToOlap path to OLAP with sets of uniqueUIDs
     */
    void setPathToOLAP(String pathToOlap);

    /**
     * Preprocess unique_timestamps and unique_pages files to enter into working state
     */
    void readIndexFiles(String pathToUniquePagesFile, String pathToUniqueTimestampsFile) throws IOException;
}
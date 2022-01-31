package com.example.similarityanalyzer.service;


import java.io.IOException;

public interface PreprocessingService {


    /**
     * Create unique timestamps and unique pages files
     */
    void createUtilityFiles();

    /**
     * Create files with sets of unique UIDs for page and every time interval
     * @param page page to create sets
     * @throws IOException when cannot open timestamp or input_file
     */
    void createUniqueUIDsOfIndividualPage(int page) throws IOException;

    /**
     * Perform preprocess:
     * create OLAP and index files
     */
    void preprocess();

}

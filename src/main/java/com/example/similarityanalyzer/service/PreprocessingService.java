package com.example.similarityanalyzer.service;


import java.io.IOException;

public interface PreprocessingService {

    int normalizeLengthOfRows();

    int createUtilityFiles();

    void createUniqueUIDsOfIndividualPage(int page) throws IOException;

    int preprocess();

}

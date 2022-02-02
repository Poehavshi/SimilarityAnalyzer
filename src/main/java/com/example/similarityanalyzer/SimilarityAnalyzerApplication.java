package com.example.similarityanalyzer;

import au.com.bytecode.opencsv.CSVReader;
import com.example.similarityanalyzer.service.PreprocessingService;
import com.example.similarityanalyzer.service.PreprocessingServiceImpl;


import gnu.trove.set.hash.TIntHashSet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.util.Scanner;


@SpringBootApplication
public class SimilarityAnalyzerApplication {
    public static void preprocess(String[] args){
        boolean TEST = false;
        String pathToInputFile;
        if (TEST) {
            pathToInputFile = "test_uid_page_timestamp.sorted.csv";
        } else {
            pathToInputFile = "uid_page_timestamp.sorted.csv";
        }
        PreprocessingService preprocessingService = new PreprocessingServiceImpl("", pathToInputFile);
        preprocessingService.preprocess();
    }

    public static void run(String[] args){
        preprocess(args);
        SpringApplication.run(SimilarityAnalyzerApplication.class, args);
    }

    public static void testOutOfMemory(){
        int i=0;
        try {
            TIntHashSet testHashSet = new TIntHashSet();
            for (i = 0; i < 1000 * 1000 * 1000; ++i) {
                testHashSet.add(i);
            }
        }
        catch (OutOfMemoryError exception)
        {
            System.err.println(i);
        }
    }

    public static void main(String[] args) {
        testOutOfMemory();
        //preprocess(args);
    }



}

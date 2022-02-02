package com.example.similarityanalyzer;

import com.example.similarityanalyzer.service.PreprocessingService;
import com.example.similarityanalyzer.service.PreprocessingServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.*;



@SpringBootApplication
public class SimilarityAnalyzerApplication {
    public static void preprocess(String[] args){
        boolean TEST = true;
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


    public static void main(String[] args) throws IOException {
        //testOutOfMemory();
        //preprocess(args);


    }
}

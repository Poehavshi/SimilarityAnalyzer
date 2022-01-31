package com.example.similarityanalyzer;

import com.example.similarityanalyzer.service.PreprocessingService;
import com.example.similarityanalyzer.service.PreprocessingServiceImpl;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;


@SpringBootApplication
public class SimilarityAnalyzerApplication {


    public static void main(String[] args) throws IOException{

        PreprocessingService preprocessingService = new PreprocessingServiceImpl(
                "test_uid_page_timestamp.sorted.csv",
                "test_uid_page_timestamp_new.sorted.csv",
                "test_unique_pages.csv",
                "test_unique_timestamps.csv",
                "test_unique_timestamps_normalized.csv"
                );
        preprocessingService.preprocess();
        //SpringApplication.run(SimilarityAnalyzerApplication.class, args);
    }

}

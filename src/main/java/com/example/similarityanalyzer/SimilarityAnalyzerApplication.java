package com.example.similarityanalyzer;

import com.example.similarityanalyzer.service.PreprocessingService;
import com.example.similarityanalyzer.service.PreprocessingServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SimilarityAnalyzerApplication {

    public static void preprocess() {
        PreprocessingService preprocessingService = new PreprocessingServiceImpl(
                "uid_page_timestamp.sorted.csv",
                "unique_pages.csv",
                "unique_timestamps.csv",
                "OLAP/",
                10*60
        );
        preprocessingService.preprocess();
    }

    public static void run(String[] args) {
        preprocess();
        SpringApplication.run(SimilarityAnalyzerApplication.class, args);
    }


    public static void main(String[] args) {
        run(args);
    }
}

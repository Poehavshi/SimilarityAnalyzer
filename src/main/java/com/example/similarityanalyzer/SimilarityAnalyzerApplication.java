package com.example.similarityanalyzer;

import com.example.similarityanalyzer.service.PreprocessingService;
import com.example.similarityanalyzer.service.PreprocessingServiceImpl;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class SimilarityAnalyzerApplication {

    public static void run(String[] args){
        PreprocessingService preprocessingService = new PreprocessingServiceImpl("");
        preprocessingService.preprocess();
        SpringApplication.run(SimilarityAnalyzerApplication.class, args);
    }

    public static void main(String[] args) {
        run(args);
    }

}

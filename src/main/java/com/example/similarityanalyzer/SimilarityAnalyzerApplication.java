package com.example.similarityanalyzer;

import com.example.similarityanalyzer.service.PreprocessingService;
import com.example.similarityanalyzer.service.PreprocessingServiceImpl;

import gnu.trove.impl.hash.TByteIntHash;
import gnu.trove.list.array.TIntArrayList;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;


@SpringBootApplication
public class SimilarityAnalyzerApplication {

    public static void run(String[] args){
        PreprocessingService preprocessingService = new PreprocessingServiceImpl(
                "test_uid_page_timestamp.sorted.csv",
                "test_uid_page_timestamp_new.sorted.csv",
                "test_unique_pages.csv",
                "test_unique_timestamps.csv",
                "test_unique_timestamps_normalized.csv"
        );
        preprocessingService.preprocess();
        SpringApplication.run(SimilarityAnalyzerApplication.class, args);
    }

    public static void testTIntArrayList(){
        TIntArrayList pages = new TIntArrayList();
        pages.add(1);
        pages.add(2);
        System.out.println(pages);
    }

    public static void main(String[] args) {
        testTIntArrayList();
        run(args);
    }

}

package com.example.similarityanalyzer;

import com.example.similarityanalyzer.service.PreprocessingService;
import com.example.similarityanalyzer.service.PreprocessingServiceImpl;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.map.hash.TLongIntHashMap;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.*;

@SpringBootApplication
public class SimilarityAnalyzerApplication {

    public static void testBufferedReader() {

    }

    public static void testSeek() {

    }

    public static void testStreamReading(){
        int n = 40; // The line number
        String line;
        try (Stream<String> lines = Files.lines(Paths.get("test_uid_page_timestamp.sorted.csv"))) {
            line = lines.skip(n).findFirst().get();
            System.out.println(line);
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
    }



    public static void main(String[] args) throws IOException{

        PreprocessingService preprocessingService = new PreprocessingServiceImpl(
                "test_uid_page_timestamp.sorted.csv",
                "test_uid_page_timestamp_new.sorted.csv",
                "test_unique_pages.csv");
        preprocessingService.preprocess();
        //SpringApplication.run(SimilarityAnalyzerApplication.class, args);
    }

}

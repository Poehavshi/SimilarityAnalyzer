package com.example.similarityanalyzer;

import com.example.similarityanalyzer.service.PreprocessingService;
import com.example.similarityanalyzer.service.PreprocessingServiceImpl;
import gnu.trove.map.hash.TLongIntHashMap;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    public static void testScannerDelimiter() throws IOException{
        FileInputStream inputStream = null;
        Scanner scanner = null;

        try {
            inputStream = new FileInputStream("test_uid_page_timestamp_new.sorted.csv");
            scanner = new Scanner(inputStream);
            scanner.useDelimiter(",");
            long uid;
            while (scanner.hasNext()) {
                uid = scanner.nextLong();
                System.out.println(uid);
                for (int i = 0;i<2;i++) scanner.nextInt();
            }
            if (scanner.ioException() != null) {
                throw scanner.ioException();
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    public static void main(String[] args) throws IOException{
        testScannerDelimiter();
//        PreprocessingService preprocessingService = new PreprocessingServiceImpl(
//                "test_uid_page_timestamp.sorted.csv",
//                "test_uid_page_timestamp_new.sorted.csv",
//                "test_unique_uid.csv");
//        preprocessingService.preprocess();
        //SpringApplication.run(SimilarityAnalyzerApplication.class, args);
    }

}

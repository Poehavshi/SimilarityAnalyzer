package com.example.similarityanalyzer.service;


import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


@Service
public class ProcessingServiceImpl implements ProcessingService {
    @Override
    public ArrayList<Integer> readUniquePages() throws IOException {
        ArrayList<Integer> pages = new ArrayList<>();
        try (Scanner scanner = new Scanner(new FileInputStream("test_unique_pages.csv"))) {
            while (scanner.hasNext()) {
                int page = scanner.nextInt();
                pages.add(page);
            }
            if (scanner.ioException() != null) {
                throw scanner.ioException();
            }
            return pages;
        }
    }

    @Override
    public double getSimilarity(int page1, int page2, int from, int to) {
        // !TODO create implementation of getting similarity metrics
        return 0.5;
    }

}
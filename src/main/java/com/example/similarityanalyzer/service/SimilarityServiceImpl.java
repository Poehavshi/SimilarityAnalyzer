package com.example.similarityanalyzer.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class SimilarityServiceImpl implements SimilarityService {
    @Override
    public List<Integer> readUniquePages() {
        // !TODO Create implementation of finding unique pages
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        return list;
    }


    public Double getSimilarity(int page1, int page2, int from, int to) {
        // !TODO create implementation of getting similarity metrics
        return 0.5;
    }

}
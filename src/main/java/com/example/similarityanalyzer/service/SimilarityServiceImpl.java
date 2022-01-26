package com.example.similarityanalyzer.service;

import gnu.trove.list.array.TIntArrayList;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class SimilarityServiceImpl implements SimilarityService {
    @Override
    public TIntArrayList readUniquePages() {
        // !TODO Create implementation of finding unique pages
        TIntArrayList list = new TIntArrayList();
        list.add(1);
        list.add(2);
        return list;
    }


    public double getSimilarity(int page1, int page2, int from, int to) {
        // !TODO create implementation of getting similarity metrics
        return 0.5;
    }

}
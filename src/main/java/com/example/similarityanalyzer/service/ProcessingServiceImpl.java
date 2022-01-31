package com.example.similarityanalyzer.service;

import gnu.trove.list.array.TIntArrayList;
import org.springframework.stereotype.Service;


@Service
public class ProcessingServiceImpl implements ProcessingService {
    @Override
    public TIntArrayList readUniquePages() {
        // !TODO Create reading unique pages from file
        TIntArrayList list = new TIntArrayList();
        list.add(1);
        list.add(2);
        return list;
    }

    @Override
    public double getSimilarity(int page1, int page2, int from, int to) {
        // !TODO create implementation of getting similarity metrics
        return 0.5;
    }

}
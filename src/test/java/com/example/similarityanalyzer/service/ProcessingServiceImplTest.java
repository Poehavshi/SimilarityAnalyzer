package com.example.similarityanalyzer.service;

import gnu.trove.set.hash.TLongHashSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

class ProcessingServiceImplTest {

    private final ProcessingService processingService = new ProcessingServiceImpl();

    @BeforeEach
    void setUp() {
        try {
            processingService.readIndexFiles("test_unique_pages.csv", "test_unique_timestamps.csv");
            processingService.setPathToOLAP("test_OLAP/");
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
        }
    }

    @Test
    void getUniquePages() {
        ArrayList<Integer> expected = new ArrayList<>();
        for (int i = 1; i <= 10; ++i) expected.add(i);

        Assertions.assertEquals(expected, processingService.getUniquePages());
    }

    @Test
    void getJaccardIndex() {
        TLongHashSet set1 = new TLongHashSet();
        TLongHashSet set2 = new TLongHashSet();
        set1.add(1);
        set1.add(2);
        set1.add(3);
        set1.add(4);

        set2.add(1);
        set2.add(2);

        // set1 = {1, 2, 3, 4}
        // set2 = {1, 2}
        // set1 ⋂ set2 = {1, 2}
        // set1 ∪ set2 = {1, 2, 3, 4}
        // K_J = 2/4 = 0.5

        Assertions.assertEquals(0.5, processingService.getJaccardIndex(set1, set2));
    }

    @Test
    void getSimilarity() {
        PreprocessingService preprocessingService = new PreprocessingServiceImpl(
                "test_uid_page_timestamp.sorted.csv",
                "test1_unique_pages.csv",
                "test1_unique_timestamps.csv",
                "test_OLAP/",
                1);
        preprocessingService.preprocess();

        try {
            processingService.readIndexFiles("test1_unique_pages.csv", "test1_unique_timestamps.csv");
            Assertions.assertEquals(0.5, processingService.getSimilarity(10607,12727,1511737200,1511737205));
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
        }

    }
}
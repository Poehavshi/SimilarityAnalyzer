package com.example.similarityanalyzer.controller;

import com.example.similarityanalyzer.service.ProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;

@RestController
public class ProcessingController {

    private final ProcessingService processingService;

    @Autowired
    public ProcessingController(ProcessingService processingService) {
        this.processingService = processingService;
        try {
            processingService.readIndexFiles("unique_pages.csv", "unique_timestamps.csv");
            processingService.setPathToOLAP("OLAP/");
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
        }
    }

    @GetMapping(value = "/pages")
    public ResponseEntity<ArrayList<Integer>> readUniquePages() {
        ArrayList<Integer> pages;
        pages = processingService.getUniquePages();

        return pages != null && !pages.isEmpty()
                ? new ResponseEntity<>(pages, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/similarity")
    public ResponseEntity<Double> getSimilarity(@RequestParam int page1,
                                                @RequestParam int page2,
                                                @RequestParam int from,
                                                @RequestParam int to) {
        double similarity = -1;
        try {
            similarity = processingService.getSimilarity(page1, page2, from, to);
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
        }
        return similarity >= 0
                ? new ResponseEntity<>(similarity, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}
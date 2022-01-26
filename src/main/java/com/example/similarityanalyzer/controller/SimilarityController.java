package com.example.similarityanalyzer.controller;

import com.example.similarityanalyzer.service.SimilarityService;
import gnu.trove.list.array.TIntArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SimilarityController {

    private final SimilarityService similarityService;

    @Autowired
    public SimilarityController(SimilarityService similarityService) {
        this.similarityService = similarityService;
    }

    @GetMapping(value = "/pages")
    public ResponseEntity<TIntArrayList> readUniquePages() {
        final TIntArrayList pages = similarityService.readUniquePages();

        return pages != null &&  !pages.isEmpty()
                ? new ResponseEntity<>(pages, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/similarity")
    public ResponseEntity<Double> getSimilarity(@RequestParam int page1,
                                                @RequestParam int page2,
                                                @RequestParam int from,
                                                @RequestParam int to) {

        return new ResponseEntity<>(similarityService.getSimilarity(page1, page2, from, to), HttpStatus.OK);
    }


}
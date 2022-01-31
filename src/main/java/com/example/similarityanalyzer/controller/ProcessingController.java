package com.example.similarityanalyzer.controller;

import com.example.similarityanalyzer.service.ProcessingService;
import gnu.trove.list.array.TIntArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;
import java.io.IOException;
import java.util.ArrayList;

@RestController
public class ProcessingController {

    private final ProcessingService processingService;

    @Autowired
    public ProcessingController(ProcessingService processingService) {
        this.processingService = processingService;
    }

    @GetMapping(value = "/pages")
    public ResponseEntity<ArrayList<Integer>> readUniquePages() {
        ArrayList<Integer> pages = null;
        try {
            pages = processingService.readUniquePages();
        }
        catch (IOException exception){
            System.err.println(exception.getMessage());
        }
        return pages != null &&  !pages.isEmpty()
                ? new ResponseEntity<>(pages, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }



    @GetMapping(value = "/similarity")
    public ResponseEntity<Double> getSimilarity(@RequestParam int page1,
                                                @RequestParam int page2,
                                                @RequestParam int from,
                                                @RequestParam int to) {

        return new ResponseEntity<>(processingService.getSimilarity(page1, page2, from, to), HttpStatus.OK);
    }


}
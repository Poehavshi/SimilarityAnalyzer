package com.example.similarityanalyzer.service;


import gnu.trove.set.hash.TLongHashSet;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


@Service
public class ProcessingServiceImpl implements ProcessingService{

    private ArrayList<Integer> pages;
    private int lengthOfRowInUniqueTimestamps;
    private int numberOfUniqueTimestamps = 0;
    private RandomAccessFile file;

    @Override
    public void readUniquePages(String pathToUniquePagesFile) throws IOException {
        pages = new ArrayList<>();
        try (Scanner scanner = new Scanner(new FileInputStream(pathToUniquePagesFile))) {
            while (scanner.hasNext()) {
                int page = scanner.nextInt();
                pages.add(page);
            }
            if (scanner.ioException() != null) {
                throw scanner.ioException();
            }
        }
    }

    @Override
    public ArrayList<Integer> getUniquePages() {
        return pages;
    }

    public void preProcessUniqueTimestamps(String pathToUniqueTimestampsFile) throws IOException {
        try (Scanner scanner = new Scanner(new FileInputStream(pathToUniqueTimestampsFile))) {
            if (scanner.hasNextLine()) {
                lengthOfRowInUniqueTimestamps = scanner.nextLine().length() + 1;
                numberOfUniqueTimestamps++;
            }
            while (scanner.hasNextLine()){
                scanner.nextLine();
                numberOfUniqueTimestamps++;
            }
            if (scanner.ioException() != null) {
                throw scanner.ioException();
            }
        }
        file = new RandomAccessFile(pathToUniqueTimestampsFile, "r");
    }

    private int binarySearchTimestamp(int timestamp, boolean findFrom) throws IOException{
        int low = 0, high = numberOfUniqueTimestamps;
        int mid = 0, value = 0;
        while (low <= high && high - low > 1) {
            mid = (high+low)/2;
            file.seek((long) mid *lengthOfRowInUniqueTimestamps);
            value = Integer.parseInt(file.readLine().replace(" ",""));
            if (timestamp < value){
                high = mid;
            }
            else if (timestamp > value){
                low = mid;
            }
            else {
                break;
            }
        }
        if (timestamp < value && findFrom && mid!=0){
            mid--;
        }
        if (timestamp > value && !findFrom && mid!=numberOfUniqueTimestamps){
            mid++;
        }
        return mid;
    }

    TLongHashSet getUniqueUIDs(int fromPos, int toPos, int page) throws IOException, ClassNotFoundException{
        file.seek((long)lengthOfRowInUniqueTimestamps*fromPos);
        TLongHashSet allUniqueUIDs = new TLongHashSet();
        TLongHashSet uniqueUIDs = new TLongHashSet();
        while (fromPos != toPos) {
            int value = Integer.parseInt(file.readLine().replace(" ", ""));
            try (FileInputStream fileInputStream = new FileInputStream("OLAP/individual/" + page + "/" + value);
                 ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
                uniqueUIDs.readExternal(objectInputStream);
                allUniqueUIDs.addAll(uniqueUIDs);
            }
            fromPos++;
        }
        return allUniqueUIDs;
    }

    @Override
    public double getSimilarity(int page1, int page2, int from, int to) throws IOException{
        // 1) Find from in timestamps
        // 2) Find to in timestamps
        // 3) Generate page1 set
        // 4) Generate page2 set
        // 5) Find length of page1 and page2 sets
        // 6) Perform intersection of page1 and page2 sets
        // 7) Find length of intersection of page1 and page2 sets
        // 8) Return result

        double result = 0;
        int fromPos = binarySearchTimestamp(from, true);
        int toPos = binarySearchTimestamp(to, false);
        try {
            TLongHashSet page1Set = getUniqueUIDs(fromPos, toPos, page1);
            TLongHashSet page2Set = getUniqueUIDs(fromPos, toPos, page2);
            int page1SetSize = page1Set.size();
            int page2SetSize = page2Set.size();
            page1Set.retainAll(page2Set);
            int intersectionSetSize = page1Set.size();
            result = (double) intersectionSetSize / (page1SetSize+page2SetSize-intersectionSetSize);
        } catch (Exception exception){
            System.err.println(exception.getMessage());
        }

        return result;
    }

}
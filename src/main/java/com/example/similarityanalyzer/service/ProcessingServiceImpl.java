package com.example.similarityanalyzer.service;

import gnu.trove.set.hash.TLongHashSet;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;


@Service
public class ProcessingServiceImpl implements ProcessingService {
    private ArrayList<Integer> pages;
    private int lengthOfRowInUniqueTimestamps;
    private long numberOfUniqueTimestamps = 0;
    private RandomAccessFile file;
    private String _pathToOlap = "OLAP/";

    private void readUniquePages(String pathToUniquePagesFile) throws IOException {
        pages = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(pathToUniquePagesFile))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                int page = Integer.parseInt(line);
                pages.add(page);
            }
        }
    }

    private void preProcessUniqueTimestamps(String pathToUniqueTimestampsFile) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(pathToUniqueTimestampsFile))) {
            String line;
            if ((line = bufferedReader.readLine()) != null) {
                lengthOfRowInUniqueTimestamps = line.length() + 1;
            }
        }

        file = new RandomAccessFile(pathToUniqueTimestampsFile, "r");
        numberOfUniqueTimestamps = file.length() / lengthOfRowInUniqueTimestamps;
    }


    @Override
    public void readIndexFiles(String pathToUniquePagesFile, String pathToUniqueTimestampsFile) throws IOException {
        readUniquePages(pathToUniquePagesFile);
        preProcessUniqueTimestamps(pathToUniqueTimestampsFile);
    }

    @Override
    public ArrayList<Integer> getUniquePages() {
        return pages;
    }

    @Override
    public void setPathToOLAP(String pathToOlap){
        _pathToOlap = pathToOlap;
    }

    private long binarySearchTimestamp(int timestamp, boolean findFrom) throws IOException {
        long low = 0, high = numberOfUniqueTimestamps;
        long mid = 0, value = 0;
        while (low <= high && high - low > 1) {
            mid = (high + low) / 2;
            file.seek(mid * lengthOfRowInUniqueTimestamps);
            value = Integer.parseInt(file.readLine());
            if (timestamp < value) {
                high = mid;
            } else if (timestamp > value) {
                low = mid;
            } else {
                break;
            }
        }
        if (timestamp < value && findFrom && mid != 0) {
            mid--;
        }
        if (timestamp > value && !findFrom && mid != numberOfUniqueTimestamps) {
            mid++;
        }
        return mid;
    }

    private TLongHashSet getUniqueUIDs(long fromPos, long toPos, int page) throws IOException, ClassNotFoundException {
        file.seek(lengthOfRowInUniqueTimestamps * fromPos);
        TLongHashSet allUniqueUIDs = new TLongHashSet();
        TLongHashSet uniqueUIDs = new TLongHashSet();
        while (fromPos != toPos) {
            int value = Integer.parseInt(file.readLine());
            File fileWithSet = new File(_pathToOlap + page + "/" + value);
            if (fileWithSet.exists()) {
                try (FileInputStream fileInputStream = new FileInputStream(_pathToOlap + page + "/" + value);
                     ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
                    uniqueUIDs.readExternal(objectInputStream);
                    allUniqueUIDs.addAll(uniqueUIDs);
                }
            }
            fromPos++;
        }
        return allUniqueUIDs;
    }

    public double getJaccardIndex(TLongHashSet set1, TLongHashSet set2) {
        int set1Size = set1.size();
        int set2Size = set2.size();
        set1.retainAll(set2);
        int intersectionSize = set1.size();
        return (double) intersectionSize / (set1Size + set2Size - intersectionSize);
    }

    @Override
    public double getSimilarity(int page1, int page2, int from, int to) throws IOException {
        // 1) Find from in timestamps
        // 2) Find to in timestamps
        // 3) Generate page1 set
        // 4) Generate page2 set
        // 5) Find length of page1 and page2 sets
        // 6) Perform intersection of page1 and page2 sets
        // 7) Find length of intersection of page1 and page2 sets
        // 8) Return result

        double result = 0;
        long fromPos = binarySearchTimestamp(from, true);
        long toPos = binarySearchTimestamp(to, false);

        try {
            TLongHashSet page1Set = getUniqueUIDs(fromPos, toPos, page1);
            TLongHashSet page2Set = getUniqueUIDs(fromPos, toPos, page2);
            result = getJaccardIndex(page1Set, page2Set);
        } catch (Exception exception) {
            System.err.println(exception.getMessage());
        }

        return result;
    }

}
package com.texteditor.iterators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinesRangeIterator implements Iterator<String> {

    private ArrayList<String> lines = new ArrayList<>();
    private int index1;
    private int index2;


    public LinesRangeIterator(ArrayList<String> lines, int index1, int index2) {
        this.lines = lines;
        this.index1 = index1;
        this.index2 = index2;
    }

    @Override
    public boolean hasNext() {
        return index1 < index2;
    }

    @Override
    public String next() {
        if (hasNext()) {
            String value = lines.get(index1);
            index1 ++;
            return value;
        }
        throw new NoSuchElementException("No more positions available");
    }
}

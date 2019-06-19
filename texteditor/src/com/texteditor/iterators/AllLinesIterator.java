package com.texteditor.iterators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class AllLinesIterator implements Iterator<String> {

    private ArrayList<String> lines = new ArrayList<>();
    private int index = 0;

    public AllLinesIterator(ArrayList<String> lines) {
        this.lines = lines;
    }

    @Override
    public boolean hasNext() {
        return index < lines.size();
    }

    @Override
    public String next() {
        if (hasNext()) {
            String value = lines.get(index);
            index ++;
            return value;
        }
        throw new NoSuchElementException("No more positions available");
    }
}

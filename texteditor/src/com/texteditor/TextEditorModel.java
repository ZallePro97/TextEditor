package com.texteditor;

import com.EditAction;
import com.UndoManager;
import com.actions.*;
import com.texteditor.cursor_observer.CursorObserver;
import com.texteditor.iterators.AllLinesIterator;
import com.texteditor.iterators.LinesRangeIterator;
import com.texteditor.text_observer.TextObserver;

import java.util.ArrayList;
import java.util.Iterator;

public class TextEditorModel {

    private ArrayList<String> lines;
    private Location cursorLocation = new Location(0, 0);
    private LocationRange selectionRange = new LocationRange();
    private ArrayList<CursorObserver> cursorObservers = new ArrayList<>();
    private ArrayList<TextObserver> textObservers = new ArrayList<>();

    public UndoManager manager = UndoManager.instance();


    public TextEditorModel(String startingText) {
        this.lines = new ArrayList<>();
        splitText(startingText);
    }

    public void attachCursorObserver(CursorObserver cursorObserver) {
        cursorObservers.add(cursorObserver);
    }

    public void dettachCursorObserver(CursorObserver cursorObserver) {
        cursorObservers.remove(cursorObserver);
    }

    public void notifyCursorObservers() {
        for (CursorObserver o : cursorObservers) {
            o.updateCursorLocation(cursorLocation);
        }
    }

    public void attachTextObserver(TextObserver observer) {
        this.textObservers.add(observer);
    }

    public void dettachTextObserver(TextObserver observer) {
        this.textObservers.remove(observer);
    }

    public void notifyTextObservers() {
        for (TextObserver o : textObservers) {
            o.updateText();
        }
    }


    public String getSelectedText() {

        String result = "";

        if (selectionRange.getLoc1() != selectionRange.getLoc2()) {

            // ako je oznacen samo jedan redak
            if (selectionRange.getLoc1().getRow() == selectionRange.getLoc2().getRow()) {
                String current = lines.get(selectionRange.getLoc1().getRow());

                result += current.substring(selectionRange.getLoc1().getColumn(), selectionRange.getLoc2().getColumn());

                return result;
            } else {
                // ako je oznaceno vise redaka
                for (int i = selectionRange.getLoc1().getRow(); i <= selectionRange.getLoc2().getRow(); i++) {

                    // ako je prvi redak
                    if (i == selectionRange.getLoc1().getRow()) {
                        String current = lines.get(i);

                        result += current.substring(selectionRange.getLoc1().getColumn(), current.length());
                    }

                    // redci izmedu
                    if (i > selectionRange.getLoc1().getRow() && i < selectionRange.getLoc2().getRow()) {

                        if (i != selectionRange.getLoc2().getRow()) {
                            String current = lines.get(i);
                            result += current;
                        }

                    }

                    // zadnji redak
                    if (i == selectionRange.getLoc2().getRow()) {
                        String current = lines.get(i);

                        result += current.substring(0, selectionRange.getLoc2().getColumn());

                    }

                    result += "\n";

                }

            }

        }

        return result;
    }

    public void insert(char c) {

        EditAction action = new InsertCharAction(this, c);

        manager.push(action);
        action.execute_do();
        manager.notifyUndoItems();
        manager.notifyRedoItems();
    }

    public void breakString() {

        EditAction action = new BreakStringAction(this);

        manager.push(action);
        action.execute_do();
        manager.notifyUndoItems();
        manager.notifyRedoItems();
    }

    // mozda ce trebat promjena kad se dogada CTRL + V
    public void insert(String text) {

        EditAction action = new InsertAction(this, text);

        action.execute_do();
        manager.push(action);
        manager.notifyUndoItems();
        manager.notifyRedoItems();
    }

    public void deleteBefore() {

        EditAction action = new DeleteCharAction(this);

        manager.push(action);
        action.execute_do();
        manager.notifyUndoItems();
        manager.notifyRedoItems();
    }


    public void deleteRange(LocationRange r) {

        int rowStart = r.getLoc1().getRow();
        int rowEnd = r.getLoc2().getRow();
        int columnStart = r.getLoc1().getColumn();
        int columnEnd = r.getLoc2().getColumn();

        EditAction action = new DeleteRangeAction(this, rowStart, rowEnd, columnStart, columnEnd);

        manager.push(action);
        action.execute_do();
        manager.notifyUndoItems();
        manager.notifyRedoItems();
    }

    public LocationRange getSelectionRange() {
        return this.selectionRange;
    }


    public void setSelectionRange(LocationRange range) {
        this.selectionRange = range;
    }


    public int getLineIndex() {
        return (cursorLocation.getRow() / TextEditor.COLUMN_START) - 1;
    }


    /**
     *  Return current line where cursor is
     */

    public String getLine() {
        return lines.get(getLineIndex());
    }

    /**
     *  Returns string based on current cursor location
     */
    public String getLine(Location location) {
        int index = (location.getRow() / TextEditor.COLUMN_START) - 1;

        return lines.get(index);
    }

    public void moveCursorLeft() {

        // pocetak
        if (cursorLocation.getRow() == 0 && cursorLocation.getColumn() == 0) {
            System.out.println("Ne idem dalje, sami pocetak!!");
            return;
        }

        // prelazak u redak iznad
        if (cursorLocation.getColumn() == 0) {
            cursorLocation.refresh(-1, 0);
            cursorLocation.setColumn(getLine(cursorLocation.getRow()).length());
            notifyCursorObservers();
            return;
        }

        cursorLocation.refresh(0, -1);
        notifyCursorObservers();
    }


    public void moveCursorRight() {

        String currentLine = getLine(cursorLocation.getRow());
        char[] chars = currentLine.toCharArray();
        int lastCharIndex = chars.length - 1;

        if ((cursorLocation.getRow() == lines.size() - 1) && cursorLocation.getColumn() == lastCharIndex + 1) {
            System.out.println("Sami kraj!!");
            return;
        }

        if (cursorLocation.getColumn() == lastCharIndex + 1) {
            cursorLocation.refresh(1, 0);
            cursorLocation.setColumn(0);
            notifyCursorObservers();
            return;
        }

        cursorLocation.refresh(0, 1);
        notifyCursorObservers();
    }


    public void moveCursorUp() {

        // u najgornjem sam retku
        if (cursorLocation.getRow() == 0) {
            System.out.println("Skroz gore sam!!");
            return;
        }

        cursorLocation.refresh(-1, 0);

        // ako je redak iznad kraci nego trenutni redak
        if (cursorLocation.getColumn() > getLine(cursorLocation.getRow()).length()) {
            cursorLocation.setColumn(getLine(cursorLocation.getRow()).length());
        }

        // u slucaju da sam skroz lijevo i stisnem deleteBefore()
        if (cursorLocation.getColumn() == 0) {
            System.out.println("Tu sam");
            cursorLocation.setColumn(getLine(cursorLocation.getRow()).length());
        }

        notifyCursorObservers();
    }

    public void moveCursorDown() {

        int lastLineIndex = lines.size() - 1;

        // na najdonjem sam retku
        if (cursorLocation.getRow() == lastLineIndex) {
            System.out.println("Skroz dolje sam!!");
            return;
        }

        // redak iznad dulji nego redak ispod
        if (cursorLocation.getColumn() > getLine(cursorLocation.getRow() + 1).length()) {
            cursorLocation.setColumn(getLine(cursorLocation.getRow() + 1).length());
        }

        cursorLocation.refresh(1, 0);
        notifyCursorObservers();
    }


    public String getLine(int index) {
        return lines.get(index);
    }

    public char[] getCharsForLine(Location location) {
        String line = getLine(location);

        return line.toCharArray();
    }


    public Iterator allLines() {
        return new AllLinesIterator(this.lines);
    }


    public Iterator linesRange(int index1, int index2) {
        return new LinesRangeIterator(this.lines, index1, index2);
    }

    private void splitText(String text) {
        String[] parts = text.split("\n");

        for (int i = 0; i < parts.length; i++) {
            this.lines.add(parts[i]);
        }
    }



    public ArrayList<String> getLines() {
        return lines;
    }

    public void setLines(ArrayList<String> lines) {
        this.lines = lines;
    }

    public Location getCursorLocation() {
        return cursorLocation;
    }

    public void setCursorLocation(Location cursorLocation) {
        this.cursorLocation = cursorLocation;
    }

}

package com.actions;

import com.EditAction;
import com.texteditor.Location;
import com.texteditor.TextEditorModel;

import java.util.ArrayList;
import java.util.Stack;

public class DeleteRangeAction implements EditAction {

    private TextEditorModel model;

    private Stack<Location> cursorLocations = new Stack<>();
    private Stack<ArrayList<String>> lines = new Stack<>();

    private int rowStart;
    private int rowEnd;
    private int columnStart;
    private int columnEnd;

    public DeleteRangeAction(TextEditorModel model, int rowStart, int rowEnd, int columnStart, int columnEnd) {
        this.model = model;
        this.rowStart = rowStart;
        this.rowEnd = rowEnd;
        this.columnStart = columnStart;
        this.columnEnd = columnEnd;
    }

    @Override
    public void execute_do() {

        cursorLocations.push(new Location(model.getCursorLocation().getRow(), model.getCursorLocation().getColumn()));
        lines.push(new ArrayList<>(model.getLines()));

        if (rowStart == rowEnd && columnStart > columnEnd) {
            int tmp = columnStart;
            columnStart = columnEnd;
            columnEnd = tmp;

            StringBuilder sb = new StringBuilder(model.getLines().get(rowStart));

            sb.delete(columnStart, columnEnd);

            String result = sb.toString();
            System.out.println(result);
            model.getLines().set(rowStart, result);

            model.notifyTextObservers();
            model.notifyCursorObservers();
            return;
        }

        // oznaceno vise redaka, ali s desna na lijevo
//        if (rowStart > rowEnd) {
//
//            Location tmp = new Location(model.getSelectionRange().getLoc2());
//            model.getSelectionRange().setLoc2(new Location(model.getSelectionRange().getLoc1()));
//            model.getSelectionRange().setLoc1(new Location(tmp));
//
//            rowStart = model.getSelectionRange().getLoc1().getRow();
//            rowEnd = model.getSelectionRange().getLoc2().getRow();
//            columnStart = model.getSelectionRange().getLoc1().getColumn();
//            columnEnd = model.getSelectionRange().getLoc2().getColumn();
//
//            modifyStrings(rowStart, columnStart, rowEnd, columnEnd);
//
//            model.notifyTextObservers();
//            return;
//        }

        // prvo rjesavam slucaj da je oznaceno sve u istom retku
        if (rowEnd == rowStart) {
            StringBuilder sb = new StringBuilder(model.getLines().get(rowStart));

            sb.delete(columnStart, columnEnd);

            String result = sb.toString();
            System.out.println(result);
            this.model.getLines().set(rowStart, result);

            int d = columnEnd - columnStart;

            model.getCursorLocation().refresh(0, -d);

            model.notifyTextObservers();
            model.notifyCursorObservers();
        }

        // oznaceno je vise redaka
        if (rowEnd > rowStart) {


            modifyStrings(rowStart, columnStart, rowEnd, columnEnd);

            model.getCursorLocation().refresh(-(rowEnd - rowStart), 0);
            model.getCursorLocation().setColumn(model.getSelectionRange().getLoc1().getColumn());
            model.notifyTextObservers();
            model.notifyCursorObservers();
        }
    }


    @Override
    public void execute_undo() {
        if (!cursorLocations.isEmpty() && !lines.isEmpty()) {
            model.setLines(lines.pop());
            model.setCursorLocation(cursorLocations.pop());
            model.notifyCursorObservers();
        }
    }


    public void modifyStrings(int rowStart, int columnStart, int rowEnd, int columnEnd) {
        for (int i = rowStart; i <= rowEnd; i++) {

            StringBuilder sb = new StringBuilder(model.getLines().get(i));

            if (i == rowStart) {
                sb.delete(model.getSelectionRange().getLoc1().getColumn(), model.getLines().get(i).length());
                String result = sb.toString();
                model.getLines().set(i, result);
                continue;
            }

            if (i == rowEnd) {
                sb.delete(0, model.getSelectionRange().getLoc2().getColumn());
                String result = sb.toString();
                model.getLines().set(i, result);
                break;
            }

            model.getLines().set(i, "");

        }
    }

}

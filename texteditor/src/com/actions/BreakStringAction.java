package com.actions;

import com.EditAction;
import com.texteditor.Location;
import com.texteditor.TextEditorModel;

import java.util.ArrayList;
import java.util.Stack;

public class BreakStringAction implements EditAction {

    private TextEditorModel model;

    private Stack<ArrayList<String>> lines = new Stack<>();
    private Stack<Location> cursorLocations = new Stack<>();

    public BreakStringAction(TextEditorModel model) {
        this.model = model;
    }

    @Override
    public void execute_do() {

        cursorLocations.push(new Location(model.getCursorLocation().getRow(), model.getCursorLocation().getColumn()));
        lines.push(new ArrayList<>(model.getLines()));

        String current = model.getLines().get(model.getCursorLocation().getRow());
        StringBuilder sb = new StringBuilder(current);

        sb.delete(model.getCursorLocation().getColumn(), current.length());

        String result1 = sb.toString();

        String result2 = current.substring(model.getCursorLocation().getColumn(), current.length());

        System.out.println("ODSJECENI: " + result2);

        model.getLines().set(model.getCursorLocation().getRow(), result1);
        model.getLines().add(model.getCursorLocation().getRow() + 1, result2);

        model.getCursorLocation().refresh(1, 0);
        model.getCursorLocation().setColumn(0);
        model.notifyTextObservers();
        model.notifyCursorObservers();
    }

    @Override
    public void execute_undo() {

        if (!cursorLocations.isEmpty() && !lines.isEmpty()) {
            model.setLines(lines.pop());
            model.setCursorLocation(cursorLocations.pop());
            model.notifyCursorObservers();
        }

    }
}

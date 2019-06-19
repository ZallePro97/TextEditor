package com.actions;

import com.EditAction;
import com.texteditor.Location;
import com.texteditor.TextEditorModel;

import java.util.ArrayList;
import java.util.Stack;

public class InsertAction implements EditAction {

    private TextEditorModel model;
    private String text;

    private Stack<Location> cursorLocations = new Stack<>();
    private Stack<ArrayList<String>> lines = new Stack<>();

    public InsertAction(TextEditorModel model, String text) {
        this.model = model;
        this.text = text;
    }


    @Override
    public void execute_do() {

        cursorLocations.push(new Location(model.getCursorLocation().getRow(), model.getCursorLocation().getColumn()));
        lines.push(new ArrayList<>(model.getLines()));

        String newLines[] = text.split("\\r?\\n");

        if (newLines.length <= 1) {
            String current = model.getLines().get(model.getCursorLocation().getRow());

            StringBuilder sb = new StringBuilder(current);

            sb.insert(model.getCursorLocation().getColumn(), text);

            String resultString = sb.toString();

            model.getLines().set(model.getCursorLocation().getRow(), resultString);

            model.getCursorLocation().refresh(0, text.length());
            model.notifyTextObservers();
            model.notifyCursorObservers();
        } else {

            int listIndex = model.getCursorLocation().getRow();

            for (int i = 0; i< newLines.length; i++) {
                model.getLines().add(listIndex, newLines[i]);
                model.getCursorLocation().refresh(1, 0);
                listIndex++;
            }

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
}

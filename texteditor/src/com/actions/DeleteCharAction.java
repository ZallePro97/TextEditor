package com.actions;

import com.EditAction;
import com.texteditor.Location;
import com.texteditor.TextEditorModel;

import java.util.Stack;

public class DeleteCharAction implements EditAction {

    private TextEditorModel model;

    private Stack<Location> cursorLocations = new Stack<>();
    private Stack<String> strings = new Stack<>();

    public DeleteCharAction(TextEditorModel model) {
        this.model = model;
    }

    @Override
    public void execute_do() {

        cursorLocations.push(new Location(model.getCursorLocation().getRow(), model.getCursorLocation().getColumn()));
        strings.push(model.getLines().get(model.getCursorLocation().getRow()));

        String str = model.getLines().get(model.getCursorLocation().getRow());

        if (model.getCursorLocation().getColumn() - 1 < 0) {
            System.out.println("Ne gledam nijedan znak");
            model.moveCursorUp();
            return;
        }

        char current = str.toCharArray()[model.getCursorLocation().getColumn() - 1];

        StringBuilder sb = new StringBuilder(str);
        sb.deleteCharAt(model.getCursorLocation().getColumn() - 1);

        String resultString = sb.toString();

        model.getLines().set(model.getCursorLocation().getRow(), resultString);

        model.moveCursorLeft();
        model.notifyTextObservers();
    }

    @Override
    public void execute_undo() {
        if (!cursorLocations.isEmpty() && !strings.isEmpty()) {
            model.getLines().set(cursorLocations.peek().getRow(), strings.pop());
            model.setCursorLocation(cursorLocations.pop());
            model.notifyCursorObservers();
        }
    }
}

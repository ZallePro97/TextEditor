package com.actions;

import com.EditAction;
import com.texteditor.Location;
import com.texteditor.TextEditorModel;

import java.util.ArrayList;
import java.util.Stack;
import java.util.function.Consumer;

public class InsertCharAction implements EditAction {

    private TextEditorModel model;
    private char c;

    private Stack<Location> cursorLocations = new Stack<>();
    private Stack<String> strings = new Stack<>();

    public InsertCharAction(TextEditorModel model, char c) {
        this.model = model;
        this.c = c;
    }

    @Override
    public void execute_do() {

        System.out.println("PUSHAM");

        cursorLocations.push(new Location(model.getCursorLocation().getRow(), model.getCursorLocation().getColumn()));

        // u slucaju da je kursor na pocetku i nemam nikakve stringove baca IndexOutOfBoundsException
        try {
            strings.push(model.getLines().get(model.getCursorLocation().getRow()));
            String currentString = model.getLines().get(model.getCursorLocation().getRow());

            StringBuilder sb = new StringBuilder(currentString);
            sb.insert(model.getCursorLocation().getColumn(), c);

            String resultString = sb.toString();

            model.getLines().set(model.getCursorLocation().getRow(), resultString);
            model.moveCursorRight();
        } catch (IndexOutOfBoundsException exc) {
            strings.push("");
        }

        model.notifyTextObservers();
    }

    @Override
    public void execute_undo() {

//        cursorLocations.forEach(new Consumer<Location>() {
//            @Override
//            public void accept(Location location) {
//                System.out.println(location);
//            }
//        });

        if (!cursorLocations.isEmpty() && !strings.isEmpty()) {
            model.getLines().set(cursorLocations.peek().getRow(), strings.pop());
            model.setCursorLocation(cursorLocations.pop());
            model.notifyCursorObservers();
        }
    }

}

package com.actions;

import com.EditAction;
import com.texteditor.TextEditor;
import com.texteditor.TextEditorModel;

import java.util.ArrayList;
import java.util.Stack;

public class BigLetterAction implements EditAction {

    private TextEditorModel model;

    private Stack<ArrayList<String>> lines = new Stack<>();

    public BigLetterAction(TextEditorModel model) {
        this.model = model;
    }

    @Override
    public void execute_do() {

        lines.push(new ArrayList<>(model.getLines()));

        int i = 0;
        for (String s : model.getLines()) {
            s = s.toUpperCase();
            model.getLines().set(i, s);
            i++;
        }

        model.notifyTextObservers();
        model.notifyCursorObservers();
    }

    @Override
    public void execute_undo() {

        System.out.println("nazad hehe");

        if (!lines.isEmpty()) {
            model.setLines(lines.pop());
        }
        model.notifyTextObservers();
    }
}

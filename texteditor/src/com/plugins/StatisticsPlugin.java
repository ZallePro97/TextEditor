package com.plugins;

import com.UndoManager;
import com.texteditor.ClipboardStack;
import com.texteditor.TextEditor;
import com.texteditor.TextEditorModel;

import javax.swing.*;
import java.util.ArrayList;

public class StatisticsPlugin implements Plugin {

    @Override
    public String getName() {
        return "Get statistics";
    }

    @Override
    public String getDescription() {
        return "Plugin counts rows, words, and characters";
    }

    @Override
    public void execute(TextEditorModel model, UndoManager undoManager, ClipboardStack clipboardStack) {

        int rows = model.getLines().size();

        int words = 0;
        for (String s : model.getLines()) {

            if (s.equals("")) {
                continue;
            }

            String newLines[] = s.split("\\s+");
            System.out.println(newLines.length);
            words += newLines.length;
        }

        int letters = 0;
        for (String s : model.getLines()) {
            letters += s.length();
        }

        JOptionPane.showMessageDialog(null, "There is " + rows + " rows in text.\n" + "There is " + words + " words in text.\n" +
                "There is " + letters + " letters in text.");
    }
}

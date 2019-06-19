package com.plugins;

import com.EditAction;
import com.UndoManager;
import com.actions.BigLetterAction;
import com.texteditor.ClipboardStack;
import com.texteditor.TextEditorModel;

public class BigLetter implements Plugin {

    @Override
    public String getName() {
        return "To uppercase";
    }

    @Override
    public String getDescription() {
        return "Plugin that changing start little letter to big.";
    }

    @Override
    public void execute(TextEditorModel model, UndoManager undoManager, ClipboardStack clipboardStack) {

        EditAction action = new BigLetterAction(model);

        action.execute_do();
        undoManager.push(action);
    }

}

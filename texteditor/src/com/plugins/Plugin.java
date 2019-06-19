package com.plugins;

import com.UndoManager;
import com.texteditor.ClipboardStack;
import com.texteditor.TextEditorModel;

public interface Plugin {
    public String getName();    // ime plugina (za izbornicku stavku)
    public String getDescription(); // kratki opis
    public void execute(TextEditorModel model, UndoManager undoManager, ClipboardStack clipboardStack);
}

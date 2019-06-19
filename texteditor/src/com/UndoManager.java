package com;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Stack;

public class UndoManager {

    private Stack<EditAction> undoStack = new Stack<>();
    private Stack<EditAction> redoStack = new Stack<>();

    private ArrayList<JMenuItem> undoItems = new ArrayList<>();
    private ArrayList<JMenuItem> redoItems = new ArrayList<>();

    private static UndoManager manager = null;

    private UndoManager() {
    }

    public static UndoManager instance() {
        if (manager == null) {
            manager = new UndoManager();
        }

        return manager;
    }

    public void attachUndoItem(JMenuItem item) {
        undoItems.add(item);
    }

    public void dettachUndoItem(JMenuItem item) {
        undoItems.remove(item);
    }

    public void notifyUndoItems() {
        for (JMenuItem i : undoItems) {
            if (getUndoStack().isEmpty()) {
                i.setEnabled(false);
            } else {
                i.setEnabled(true);
            }
        }
    }

    public void attachRedoItem(JMenuItem item) {
        redoItems.add(item);
    }

    public void dettachRedoItem(JMenuItem item) {
        redoItems.remove(item);
    }

    public void notifyRedoItems() {
        for (JMenuItem i : redoItems) {
            if (getRedoStack().isEmpty()) {
                i.setEnabled(false);
            } else {
                i.setEnabled(true);
            }
        }
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            EditAction action = undoStack.pop();

            redoStack.push(action);
            action.execute_undo();
            notifyUndoItems();
            notifyRedoItems();
        }


    }

    public void push(EditAction c) {
        redoStack.clear();
        undoStack.push(c);
        notifyUndoItems();
        notifyRedoItems();
    }

    public Stack<EditAction> getUndoStack() {
        return undoStack;
    }

    public void setUndoStack(Stack<EditAction> undoStack) {
        this.undoStack = undoStack;
    }

    public Stack<EditAction> getRedoStack() {
        return redoStack;
    }

    public void setRedoStack(Stack<EditAction> redoStack) {
        this.redoStack = redoStack;
    }
}

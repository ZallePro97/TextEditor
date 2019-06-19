package com.texteditor;

import com.texteditor.clipboard_observer.ClipboardObserver;

import java.util.ArrayList;
import java.util.Stack;

public class ClipboardStack {

    private Stack<String> texts = new Stack<>();
    private ArrayList<ClipboardObserver> clipboardObservers = new ArrayList<>();

    public ClipboardStack() {

    }

    public ClipboardStack(Stack<String> texts) {
        this.texts = texts;
    }

    public void attachClipboardObserver(ClipboardObserver observer) {
        clipboardObservers.add(observer);
    }

    public void dettachClipboardObserver(ClipboardObserver observer) {
        clipboardObservers.remove(observer);
    }

    public void notifyClipboardObservers() {
        for (ClipboardObserver o : clipboardObservers) {
            o.updateClipboard();
        }
    }

    public void push(String text) {
        texts.push(text);
    }

    public String pop() {
        return texts.pop();
    }


    public String readFromTop() {
        if (!texts.empty()) {
            return texts.peek();
        }

        return null;
    }

    public boolean isEmpty() {
        return texts.isEmpty();
    }

    public void deleteTexts() {
        texts.clear();
    }


    public Stack<String> getTexts() {
        return texts;
    }

    public void setTexts(Stack<String> texts) {
        this.texts = texts;
    }
}

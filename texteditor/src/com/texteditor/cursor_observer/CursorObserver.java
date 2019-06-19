package com.texteditor.cursor_observer;

import com.texteditor.Location;

public interface CursorObserver {
    public void updateCursorLocation(Location loc);
}

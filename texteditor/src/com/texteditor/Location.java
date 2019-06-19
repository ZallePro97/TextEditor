package com.texteditor;

public class Location {

    private int row;
    private int column;

    public Location(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public Location(Location location) {
        this.row = location.getRow();
        this.column = location.getColumn();
    }

    public void refresh(int drow, int dcolumn) {
        setRow(getRow() + drow);
        setColumn(getColumn() + dcolumn);
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    @Override
    public String toString() {
        return row + ", " + column;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Location)) {
            return false;
        }

        return this.getRow() == ((Location) obj).getRow() && this.getColumn() == ((Location) obj).getColumn();
    }
}

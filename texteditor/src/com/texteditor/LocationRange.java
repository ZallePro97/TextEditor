package com.texteditor;

public class LocationRange {

    private Location loc1;
    private Location loc2;

    public LocationRange() {

    }

    public LocationRange(Location loc1, Location loc2) {
        this.loc1 = loc1;
        this.loc2 = loc2;
    }

    public Location getLoc1() {
        return loc1;
    }

    public void setLoc1(Location loc1) {
        this.loc1 = loc1;
    }

    public Location getLoc2() {
        return loc2;
    }

    public void setLoc2(Location loc2) {
        this.loc2 = loc2;
    }

    public boolean isSelected() {
        return loc1.equals(loc2);
    }

    @Override
    public String toString() {
        return "Start location: " + loc1 + ", end location: " + loc2;
    }
}

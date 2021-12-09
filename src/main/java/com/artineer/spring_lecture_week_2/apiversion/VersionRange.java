package com.artineer.spring_lecture_week_2.apiversion;

public class VersionRange {
    private Version from;
    private Version to;

    public VersionRange(int from, int to) {
        this.from = new Version(from);
        this.to = new Version(to);
    }

    public boolean includes(int other) {
        Version otherVersion = new Version(other);

        int fromCondition = from.compareTo(otherVersion);
        int toCondition = to.compareTo(otherVersion);

        if(fromCondition <= 0 && toCondition >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public int compareTo(VersionRange other) {
        return this.from.compareTo(other.from);
    }
}

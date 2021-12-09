package com.artineer.spring_lecture_week_2.apiversion;

public class Version implements Comparable<Version> {
    public static final int MAX_VERSION = 9999999;

    private final int version;

    public Version(int version) {
        this.version = version;
    }

    @Override
    public int compareTo(Version other) {
        return Integer.compare(this.version, other.version);
    }
}

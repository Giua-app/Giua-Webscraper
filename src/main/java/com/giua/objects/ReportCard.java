package com.giua.objects;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ReportCard implements Serializable {
    public final boolean isFirstQuarterly;
    public final Map<String, List<String>> allVotes;
    public final boolean exists;

    public ReportCard(boolean isFirstQuarterly, Map<String, List<String>> allVotes, boolean exists) {
        this.isFirstQuarterly = isFirstQuarterly;
        this.allVotes = allVotes;
        this.exists = exists;
    }

    public String toString() {
        return String.valueOf(isFirstQuarterly);
    }
}

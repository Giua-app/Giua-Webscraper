package com.giua.objects;

import java.util.List;
import java.util.Map;

public class ReportCard {
    public final boolean isFirstQuarterly;
    public final Map<String, List<String>> allVotes;

    public ReportCard(boolean isFirstQuarterly, Map<String, List<String>> allVotes){
        this.isFirstQuarterly = isFirstQuarterly;
        this.allVotes = allVotes;
    }

    public String toString(){
        return String.valueOf(isFirstQuarterly);
    }
}

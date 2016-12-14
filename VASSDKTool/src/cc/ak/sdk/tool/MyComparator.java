package cc.ak.sdk.tool;

import java.util.Comparator;

public class MyComparator implements Comparator<String> {
    @Override
    public int compare(String arg0, String arg1) {
        int id1 = Integer.valueOf(ResourceSort.getValue(arg0, 3), 16);
        int id2 = Integer.valueOf(ResourceSort.getValue(arg1, 3), 16);
        return id1 - id2;
    }
}

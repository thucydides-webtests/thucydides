package net.thucydides.core.requirements;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

public class CachedReq {
    private static Map<String, SortedMap<String, Req>> reqByDirByRootDir = new HashMap<String, SortedMap<String, Req>>();

    private String rootDirectory;

    public CachedReq(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public SortedMap<String, Req> get() {
        return reqByDirByRootDir.get(rootDirectory);
    }

    public void set(SortedMap<String, Req> map) {
        reqByDirByRootDir.put(rootDirectory, map);
    }
}

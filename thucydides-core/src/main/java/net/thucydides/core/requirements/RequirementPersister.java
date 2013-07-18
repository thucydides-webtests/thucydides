package net.thucydides.core.requirements;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;

public class RequirementPersister {
    private final Logger logger = LoggerFactory.getLogger(RequirementPersister.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final File outputDirectory;
    private final String rootDirectory;

    public RequirementPersister(File outputDirectory, String rootDirectory) {
        this.outputDirectory = outputDirectory;
        this.rootDirectory = rootDirectory;
    }

    public SortedMap<String, Req> read() {
        SortedMap<String, Req> map = new ChildrenFirstOrderedMap();
        try {
            JavaType type = mapper.getTypeFactory().constructMapType(map.getClass(), String.class, Req.class);
            File jsonFile = new File(outputDirectory, rootDirectory + ".json");
            if(!jsonFile.exists()) {
                return map;
            }
            SortedMap<String, Req> m = mapper.readValue(jsonFile, type);
            map.putAll(m);
            //reset the parents
            for (Map.Entry<String, Req> entry : m.entrySet()) {
                String key = entry.getKey();
                if (key.contains(".")) {
                    String parent = key.substring(0, key.lastIndexOf("."));
                    addChildIfNotPresent(map.get(parent), entry.getValue());
                }
            }
        } catch (IOException e) {
            logger.error("Error while reading requirements from output directory: " + outputDirectory +
                    " ,file: " + rootDirectory + ".json", e);
        }
        return map;
    }

    private void addChildIfNotPresent(Req req, Req child) {
        if (!req.getChildren().contains(child)) {
            req.getChildren().add(child);
        }
    }

    public void write(SortedMap<String, Req> map) {
        try {
            FileOutputStream os = new FileOutputStream(new File(outputDirectory, rootDirectory + ".json"));
            mapper.writeValue(os, map);
            os.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}

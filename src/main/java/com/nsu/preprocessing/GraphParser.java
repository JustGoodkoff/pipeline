package com.nsu.preprocessing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nsu.preprocessing.model.GraphDefinition;
import java.io.File;

public class GraphParser {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static GraphDefinition parse(File jsonFile) {
        try {
            return mapper.readValue(jsonFile, GraphDefinition.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

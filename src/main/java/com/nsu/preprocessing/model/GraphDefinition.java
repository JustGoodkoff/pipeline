package com.nsu.preprocessing.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;

public class GraphDefinition {
    public Map<String, TypeDef> types;
    public Map<String, OperatorDef> operators;
    public Map<String, SourceDef> sources;


    public static class OperatorDef {
        public String kind;
        @JsonProperty("funBody")
        public String funBody;
        public Map<String, PortDef> inputs;
        public Map<String, PortDef> outputs;
        public Map<String, Object> config;
        public Integer parallelism;
    }

    public static class TypeDef {
        @JsonProperty("extends")
        public String extendsType;
        public String javaType;
        public Map<String, String> fields;
    }

    public static class SourceDef {
        public List<SourceItemDef> data;
    }

    public static class SourceItemDef {
        public String type;
        public Map<String, Object> value;
    }
}


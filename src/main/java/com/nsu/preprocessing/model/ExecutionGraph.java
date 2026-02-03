package com.nsu.preprocessing.model;

import com.nsu.preprocessing.model.GraphDefinition.OperatorDef;
import java.util.*;

public class ExecutionGraph {
    public final Map<String, ExecNode> nodes = new HashMap<>();
    public final List<ExecEdge> edges = new ArrayList<>();


    public static class ExecEdge {
        public final ExecNode fromNode;
        public final Port fromPort;
        public final ExecNode toNode;
        public final Port toPort;
        public final boolean isLoop;

        public ExecEdge(
                ExecNode fromNode,
                Port fromPort,
                ExecNode toNode,
                Port toPort,
                boolean isLoop
        ) {
            this.fromNode = fromNode;
            this.fromPort = fromPort;
            this.toNode = toNode;
            this.toPort = toPort;
            this.isLoop = isLoop;
        }
    }

    public static class ExecNode {
        public final String id;
        public final String kind;
        public final Map<String, Port> inputs = new HashMap<>();
        public final Map<String, Port> outputs = new HashMap<>();
        public final Map<String, Object> config;
        public final int parallelism;
        public final String funBody;

        public ExecNode(String id, OperatorDef def) {
            this.id = id;
            this.kind = def.kind;
            this.config = def.config != null ? def.config : Map.of();
            this.parallelism = def.parallelism != null ? def.parallelism : 1;
            this.funBody = def.funBody;

            if (def.inputs != null) {
                def.inputs.forEach((k, v) ->
                        inputs.put(k, new Port(k, v.type))
                );
            }
            if (def.outputs != null) {
                def.outputs.forEach((k, v) ->
                        outputs.put(k, new Port(k, v.type))
                );
            }
        }
    }
}

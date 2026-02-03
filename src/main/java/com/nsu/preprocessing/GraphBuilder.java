package com.nsu.preprocessing;

import com.nsu.preprocessing.model.ExecutionGraph;
import com.nsu.preprocessing.model.ExecutionGraph.ExecEdge;
import com.nsu.preprocessing.model.ExecutionGraph.ExecNode;
import com.nsu.preprocessing.model.GraphDefinition;
import com.nsu.preprocessing.model.GraphDefinition.TypeDef;
import com.nsu.preprocessing.model.Port;
import java.util.*;

public class GraphBuilder {

    public static ExecutionGraph build(GraphDefinition def) {
        ExecutionGraph graph = new ExecutionGraph();

        def.operators.forEach((id, opDef) ->
                graph.nodes.put(id, new ExecNode(id, opDef))
        );

        def.operators.forEach((fromOpId, opDef) -> {
            if (opDef.outputs == null) {
                return;
            }
            ExecNode fromNode = graph.nodes.get(fromOpId);
            opDef.outputs.forEach((outPortName, outPortDef) -> {
                if (outPortDef.to == null) {
                    return;
                }

                String[] target = outPortDef.to.split("\\.");
                if (target.length != 2) {
                    throw new IllegalStateException(
                            "Некорректная ссылка to: " + outPortDef.to
                    );
                }
                String toOpId = target[0];
                String inPortName = target[1];

                ExecNode toNode = graph.nodes.get(toOpId);
                if (toNode == null) {
                    throw new IllegalStateException(
                            "Неизвестный оператор: " + toOpId
                    );
                }
                Port outPort = fromNode.outputs.get(outPortName);
                Port inPort = toNode.inputs.get(inPortName);

                if (outPort == null || inPort == null) {
                    throw new IllegalStateException(
                            "Неизвестный порт: " + outPortDef.to
                    );
                }

                if (!isTypeCompatible(outPort.type, inPort.type, def.types)) {
                    throw new IllegalStateException(
                            "Несовместимые типы: " +
                                    outPort.type + " -> " + inPort.type
                    );
                }
                graph.edges.add(new ExecEdge(
                        fromNode,
                        outPort,
                        toNode,
                        inPort,
                        false
                ));
            });
        });

        return graph;
    }

    private static boolean isTypeCompatible(
            String from,
            String to,
            Map<String, TypeDef> types
    ) {
        if (from.equals(to)) {
            return true;
        }

        TypeDef t = types.get(to);
        while (t != null && t.extendsType != null) {
            if (t.extendsType.equals(from)) {
                return true;
            }
            t = types.get(t.extendsType);
        }
        return false;
    }
}


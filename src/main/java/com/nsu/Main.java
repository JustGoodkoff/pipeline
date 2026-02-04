package com.nsu;

import com.nsu.preprocessing.model.ExecutionGraph;
import com.nsu.preprocessing.GraphBuilder;
import com.nsu.preprocessing.model.GraphDefinition;
import com.nsu.preprocessing.GraphParser;
import com.nsu.runtime.RuntimeBuilder;
import com.nsu.runtime.RuntimeTypeRegistry;
import com.nsu.runtime.model.factory.SourceDataFactory;
import com.nsu.runtime.RuntimeGraph;
import java.io.File;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        GraphDefinition def = GraphParser.parse(
                new File("E:\\Users\\Dmitriy\\IntelliJIDEAProjects\\StreamingR\\src\\main\\resources\\pipeline.json")
        );

        ExecutionGraph graph = GraphBuilder.build(def);
        RuntimeTypeRegistry registry = new RuntimeTypeRegistry(def.types);
        Map<String, List<Object>> sourceData = SourceDataFactory.build(def);

        System.out.println("Узлы:");
        graph.nodes.values().forEach(n ->
                System.out.println(" - " + n.id + " [" + n.kind + "]")
        );

        System.out.println("Рёбра:");
        graph.edges.forEach(e ->
                System.out.println(
                        e.fromNode.id + "." + e.fromPort.name +
                                " -> " +
                                e.toNode.id + "." + e.toPort.name +
                                (e.isLoop ? " (loop)" : "")
                )
        );

        RuntimeGraph graph1 = RuntimeBuilder.build(graph, registry, sourceData);

        graph1.start();

        graph1.join();
        RuntimeGraph runtime = new RuntimeGraph();

//        Channel<String> ch1 = new Channel<>();
//        Channel<String> ch2 = new Channel<>();
//
//        runtime.addNode(
//                new SourceNode<>("source",
//                        ch1,
//                        List.of("img1", "img2").iterator()
//                )
//        );
//
//        runtime.addNode(
//                new MapNode<>("denoise",
//                        ch1,
//                        ch2,
//                        v -> v + " -> denoise"
//                )
//        );
//
//        runtime.addNode(
//                new SinkNode<>("sink",
//                        ch2
//                )
//        );
//
//        runtime.start();
//        runtime.join();
//
    }
}

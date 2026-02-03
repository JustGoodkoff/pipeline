package com.nsu.runtime;

import com.nsu.data.ImageMeta;
import com.nsu.data.JpgImage;
import com.nsu.preprocessing.model.ExecutionGraph;
import com.nsu.preprocessing.model.ExecutionGraph.ExecEdge;
import com.nsu.preprocessing.model.ExecutionGraph.ExecNode;
import com.nsu.runtime.model.Channel;
import com.nsu.runtime.model.factory.MapNodeFactory;
import com.nsu.runtime.model.factory.RuntimeNodeFactory;
import com.nsu.runtime.model.factory.SinkNodeFactory;
import com.nsu.runtime.model.factory.SourceNodeFactory;
import com.nsu.runtime.model.node.RuntimeNode;
import java.util.*;

public final class RuntimeBuilder {

    public static RuntimeGraph build(
            ExecutionGraph exec,
            RuntimeTypeRegistry types,
            Map<String, List<Object>> sourceData
    ) {
        RuntimeGraph runtime = new RuntimeGraph();

        Map<ExecEdge, Channel<?>> channels = new HashMap<>();
        for (ExecEdge e : exec.edges) {
            Class<?> cls = types.resolve(e.fromPort.type);
            channels.put(e, new Channel<>(cls));
        }

        for (ExecNode node : exec.nodes.values()) {

            Map<String, Channel<?>> inputChannels = new HashMap<>();
            Map<String, Class<?>> inputTypes = new HashMap<>();

            for (ExecEdge e : exec.edges) {
                if (e.toNode == node) {
                    Channel<?> ch = channels.get(e);
                    inputChannels.put(e.toPort.name, ch);
                    inputTypes.put(
                            e.toPort.name,
                            types.resolve(e.toPort.type)
                    );
                }
            }

            Map<String, Channel<?>> outputChannels = new HashMap<>();
            Map<String, Class<?>> outputTypes = new HashMap<>();

            for (ExecEdge e : exec.edges) {
                if (e.fromNode == node) {
                    Channel<?> ch = channels.get(e);
                    outputChannels.put(e.fromPort.name, ch);
                    outputTypes.put(
                            e.fromPort.name,
                            types.resolve(e.fromPort.type)
                    );
                }
            }

            RuntimeNodeFactory factory = factories.stream()
                    .filter(f ->
                            f.kind().equals(node.kind)
                                    && f.supports(inputTypes, outputTypes)
                    )
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "Нет factory для узла " + node.id +
                                            " kind=" + node.kind +
                                            " inputs=" + inputTypes +
                                            " outputs=" + outputTypes
                            )
                    );

            RuntimeNode<?, ?> runtimeNode =
                    factory.create(
                            node,
                            inputChannels,
                            outputChannels,
                            sourceData.getOrDefault(node.id, List.of())
                    );

            runtime.addNode(runtimeNode);
        }

        return runtime;
    }

    private static final List<RuntimeNodeFactory> factories = List.of(
            new SourceNodeFactory<>(ImageMeta.class),
            new MapNodeFactory<>(ImageMeta.class, JpgImage.class),
            new SinkNodeFactory<>(ImageMeta.class)
    );
}


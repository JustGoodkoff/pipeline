package com.nsu.runtime.model.factory;

import com.nsu.preprocessing.model.ExecutionGraph.ExecNode;
import com.nsu.runtime.model.Channel;
import com.nsu.runtime.model.node.RuntimeNode;
import java.util.List;
import java.util.Map;

public interface RuntimeNodeFactory {

    String kind();

    boolean supports(
            Map<String, Class<?>> inputTypes,
            Map<String, Class<?>> outputTypes
    );

    RuntimeNode<?, ?> create(
            ExecNode node,
            Map<String, Channel<?>> inputs,
            Map<String, Channel<?>> outputs,
            List<Object> sourceData
    );
}

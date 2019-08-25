package com.sunny.commom.utils;

import com.sunny.source.bean.Node;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

public class NodeUtils {

    @SuppressWarnings("unchecked")
    public static void merge(Map<String, Object> res, Map<String, Object> source,
                             boolean isCover) {
        if (Objects.isNull(res) || CollectionUtils.isEmpty(source)) {
            return;
        }
        if (CollectionUtils.isEmpty(res)) {
            res.putAll(source);
            return;
        }
        LinkedBlockingQueue<Node> queue = new LinkedBlockingQueue<>();
        queue.offer(new Node(res, source));

        while (!queue.isEmpty()) {
            Node node = queue.poll();
            Map<String, Object> nodeRes = node.getRes();
            Map<String, Object> nodeSource = node.getSource();
            nodeSource.forEach((key, value) -> {
                if (nodeRes.containsKey(key)) {
                    if (value instanceof String
                            || value instanceof Integer
                            || value instanceof Float
                            || value instanceof Double
                            || value instanceof Boolean) {
                        // 需要判断是否覆盖的情况
                        if (!isCover) {
                            // donnot cover
                            nodeRes.putIfAbsent(key, value);
                        } else {
                            // cover
                            nodeRes.put(key, value);
                        }
                    } else {
                        if (!(nodeRes.get(key) instanceof String
                                || nodeRes.get(key) instanceof Integer
                                || nodeRes.get(key) instanceof Double
                                || nodeRes.get(key) instanceof Float
                                || nodeRes.get(key) instanceof Boolean)) {
                            queue.offer(new Node((Map<String, Object>) nodeRes.get(key),
                                    (Map<String, Object>) nodeSource.get(key)));
                        }
                    }
                } else {
                    nodeRes.put(key, value);
                }
            });
        }

    }

}

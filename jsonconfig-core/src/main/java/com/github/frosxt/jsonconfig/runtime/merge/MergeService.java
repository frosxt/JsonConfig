package com.github.frosxt.jsonconfig.runtime.merge;

import com.github.frosxt.jsonconfig.api.merge.MergeStrategy;
import com.github.frosxt.jsonconfig.runtime.copy.NodeCloner;
import com.github.frosxt.jsonconfig.tree.JsonNode;
import com.github.frosxt.jsonconfig.tree.container.JsonArray;
import com.github.frosxt.jsonconfig.tree.container.JsonObject;

import java.util.Map;

/**
 * Service for merging JsonObject trees with configurable strategies.
 */
public class MergeService {

    public void merge(final JsonObject target, final JsonObject source, final MergeStrategy strategy) {
        if (target == null || source == null) {
            return;
        }

        mergeObjects(target, source, strategy);
    }

    private void mergeObjects(final JsonObject target, final JsonObject source, final MergeStrategy strategy) {
        for (final Map.Entry<String, JsonNode> entry : source.entrySet()) {
            final String key = entry.getKey();
            final JsonNode sourceVal = entry.getValue();
            final JsonNode targetVal = target.get(key);

            if (targetVal == null) {
                target.add(key, NodeCloner.cloneNode(sourceVal));
            } else {
                switch (strategy) {
                    case OVERWRITE -> target.add(key, NodeCloner.cloneNode(sourceVal));
                    case DEEP_MERGE_OBJECTS -> {
                        if (targetVal instanceof JsonObject && sourceVal instanceof JsonObject) {
                            mergeObjects((JsonObject) targetVal, (JsonObject) sourceVal, strategy);
                        } else {
                            target.add(key, NodeCloner.cloneNode(sourceVal));
                        }
                    }
                    case CONCAT_ARRAYS -> {
                        if (targetVal instanceof JsonArray && sourceVal instanceof JsonArray) {
                            final JsonArray targetArr = (JsonArray) targetVal;
                            for (final JsonNode node : (JsonArray) sourceVal) {
                                targetArr.add(NodeCloner.cloneNode(node));
                            }
                        } else if (targetVal instanceof JsonObject && sourceVal instanceof JsonObject) {
                            mergeObjects((JsonObject) targetVal, (JsonObject) sourceVal, strategy);
                        } else {
                            target.add(key, NodeCloner.cloneNode(sourceVal));
                        }
                    }
                }
            }
        }
    }
}

/*
 * Copyright (c) 2021 BarrelMC Team
 * This project is licensed under the MIT License
 */

package org.barrelmc.barrel.network.converter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.barrelmc.barrel.utils.FileManager;

import java.util.HashMap;
import java.util.Map;

public class BlockConverter {

    public static final HashMap<Integer, Integer> BEDROCK_BLOCK_RUNTIME_TO_CLASSIC_BLOCK_STATE = new HashMap<>();
    public static final HashMap<Integer, Integer> BEDROCK_BLOCK_RUNTIME_TO_CLASSIC1_BLOCK_STATE = new HashMap<>();

    public static void init() {
        JsonObject jsonObject = FileManager.getJsonObjectFromResource("runtime_blocks.json");

        assert jsonObject != null;

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            Integer bedrockRuntimeId = Integer.valueOf(entry.getKey());
            JsonObject blockEntry = entry.getValue().getAsJsonObject();
            if(!blockEntry.has("classic_default_state")){
                continue;
            }
            Integer classicStateId = blockEntry.get("classic_default_state").getAsInt();

            if(blockEntry.has("classic_default_state1")){
                Integer classicStateId1 = blockEntry.get("classic_default_state1").getAsInt();
                BEDROCK_BLOCK_RUNTIME_TO_CLASSIC1_BLOCK_STATE.put(bedrockRuntimeId, classicStateId1);
            }

            BEDROCK_BLOCK_RUNTIME_TO_CLASSIC_BLOCK_STATE.put(bedrockRuntimeId, classicStateId);
        }
    }

    // Convert mc bedrock runtime block id to classic block state id
    public static int bedrockRuntimeToClassicStateId(int bedrockBlockId) {
        return BEDROCK_BLOCK_RUNTIME_TO_CLASSIC_BLOCK_STATE.getOrDefault(bedrockBlockId, 1);
    }
    public static int bedrockRuntimeToClassicStateId1(int bedrockBlockId) {
        if(BEDROCK_BLOCK_RUNTIME_TO_CLASSIC1_BLOCK_STATE.containsKey(bedrockBlockId)){
            return BEDROCK_BLOCK_RUNTIME_TO_CLASSIC1_BLOCK_STATE.get(bedrockBlockId);
        }
        return bedrockRuntimeToClassicStateId(bedrockBlockId);
    }
    public static int bedrockRuntimeToClassicStateId(int bedrockBlockId, int level) {
        if(level < 1){
            return bedrockRuntimeToClassicStateId(bedrockBlockId);
        }
        return bedrockRuntimeToClassicStateId1(bedrockBlockId);
    }
}

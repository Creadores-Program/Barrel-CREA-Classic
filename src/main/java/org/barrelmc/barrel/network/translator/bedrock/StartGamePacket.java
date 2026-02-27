package org.barrelmc.barrel.network.translator.bedrock;

import com.github.steveice10.mc.classic.protocol.packet.server.ServerLevelInitializePacket;
import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.server.ProxyServer;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.definitions.ItemDefinition;
import org.cloudburstmc.protocol.bedrock.data.definitions.SimpleItemDefinition;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.common.SimpleDefinitionRegistry;

public class StartGamePacket implements BedrockPacketTranslator {

    @Override
    public void translate(BedrockPacket pk, Player player) {
        org.cloudburstmc.protocol.bedrock.packet.StartGamePacket packet = (org.cloudburstmc.protocol.bedrock.packet.StartGamePacket) pk;

        player.setRuntimeEntityId(packet.getRuntimeEntityId());
        player.setOldPosition(packet.getPlayerPosition());
        player.setPosition(packet.getPlayerPosition());
        player.setLastServerPosition(packet.getPlayerPosition());
        player.setLastServerRotation(packet.getRotation());
        player.setMaxPosBedrock(new Vector3i(((int) Math.round(packet.getPlayerPosition().getX() + 127)), 255, ((int) Math.round(packet.getPlayerPosition().getZ() + 127))));
        player.setMinPosBedrock(new Vector3i(((int) Math.round(packet.getPlayerPosition().getX() + -128)), 0, ((int) Math.round(packet.getPlayerPosition().getZ() + -128))));

        player.setStartGamePacketCache(packet);

        player.setGameMode(packet.getPlayerGameType());

        SimpleDefinitionRegistry<ItemDefinition> itemDefinitions = SimpleDefinitionRegistry.<ItemDefinition>builder()
                .addAll(packet.getItemDefinitions())
                .add(new SimpleItemDefinition("minecraft:empty", 0, false))
                .build();
        player.getBedrockClientSession().getPeer().getCodecHelper().setItemDefinitions(itemDefinitions);

        if (!packet.isBlockNetworkIdsHashed()) {
            player.getBedrockClientSession().getPeer().getCodecHelper().setBlockDefinitions(ProxyServer.getInstance().getBlockDefinitions());
        }

        player.getClassicSession().send(new ServerLevelInitializePacket());
    }

    @Override
    public boolean immediate() {
        return true;
    }
}

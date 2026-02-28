package org.barrelmc.barrel.network.translator.bedrock;

import com.github.steveice10.mc.classic.protocol.packet.server.ServerDespawnPlayerPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerExtAddEntity2Packet;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerSpawnPlayerPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerPositionRotationPacket;
import com.github.steveice10.mc.classic.protocol.data.game.PlayerIds;
import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.player.StatusWorld;
import org.barrelmc.barrel.server.ProxyServer;
import org.barrelmc.barrel.utils.Utils;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.AddPlayerPacket;
import java.util.Optional;

public class MovePlayerPacket implements BedrockPacketTranslator {

    @Override
    public void translate(BedrockPacket pk, Player player) {
        org.cloudburstmc.protocol.bedrock.packet.MovePlayerPacket packet = (org.cloudburstmc.protocol.bedrock.packet.MovePlayerPacket) pk;
        Vector3f position = packet.getPosition(), rotation = packet.getRotation();

        if (packet.getRuntimeEntityId() == player.getRuntimeEntityId()) {
            if(position.getX() > player.getMaxPosBedrock().getX() || position.getX() < player.getMinPosBedrock().getX() || position.getZ() > player.getMaxPosBedrock().getZ() || position.getZ() < player.getMinPosBedrock().getZ()){
                player.getClassicSession().send(new ServerLevelInitializePacket());
                player.setStatusWorld(StatusWorld.BUILD_WORLD);
                player.setMaxPosBedrock(Vector3i.from(((int) Math.round(packet.getPlayerPosition().getX() + 127)), 255, ((int) Math.round(packet.getPlayerPosition().getZ() + 127))));
                player.setMinPosBedrock(Vector3i.from(((int) Math.round(packet.getPlayerPosition().getX() + -128)), 0, ((int) Math.round(packet.getPlayerPosition().getZ() + -128))));
            }
            player.getClassicSession().send(new ServerPositionRotationPacket(PlayerIds.SELF, Utils.mapCoords(position.getX(), ((float) player.getMinPosBedrock().getX()), ((float) player.getMaxPosBedrock().getX()), ((float) player.getMinPosClassic().getX()), ((float) player.getMaxPosClassic().getX())), pos.getY(), Utils.mapCoords(position.getZ(), ((float) player.getMinPosBedrock().getZ()), ((float) player.getMaxPosBedrock().getZ()), ((float) player.getMinPosClassic().getZ()), ((float) player.getMaxPosClassic().getZ())), rotation.getX(), rotation.getY()));
            player.setPosition(position.getX(), position.getY() - 1.62, position.getZ());
            player.setLastServerPosition(position);
            player.setLastServerRotation(rotation.toVector2());
        } else {
            if(position.getX() > player.getMaxPosBedrock().getX() || position.getX() < player.getMinPosBedrock().getX() || position.getZ() > player.getMaxPosBedrock().getZ() || position.getZ() < player.getMinPosBedrock().getZ()){
                Optional<AddPlayerPacket> entity = player.getPlayersSpawned().stream().filter(entity -> entity.getUniqueEntityId() == packet.getUniqueEntityId()).findFirst();
                entity.ifPresent(entity -> {
                    player.getClassicSession().send(new ServerDespawnPlayerPacket((int) packet.getRuntimeEntityId()));
                    player.getPlayersSpawned().remove(entity);
                    player.getPlayersUnspawn().add(entity);
                });
                return;
            }
            Optional<AddPlayerPacket> entity = player.getPlayersUnspawn().stream().filter(entity -> entity.getUniqueEntityId() == packet.getUniqueEntityId()).findFirst();
            entity.ifPresent(entity -> {
                player.getPlayersUnspawn().remove(entity);
                player.getPlayersSpawned().add(entity);
                if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(2), player.getExtensionsClassic())){
                    player.getClassicSession().send(new ServerExtAddEntity2Packet(((int) packet.getRuntimeEntityId()), entity.getUsername(), entity.getUsername(), Utils.mapCoords(((short) position.getX()), ((short) player.getMinPosBedrock().getX()), ((short) player.getMaxPosBedrock().getX()), ((short) player.getMinPosClassic().getX()), ((short) player.getMaxPosClassic().getX())), ((short) pos.getY()), Utils.mapCoords(((short) position.getZ()), ((short) player.getMinPosBedrock().getZ()), ((short) player.getMaxPosBedrock().getZ()), ((short) player.getMinPosClassic().getZ()), ((short) player.getMaxPosClassic().getZ())), ((int) rotation.getX()), ((int) rotation.getY())));
                    return;
                }
                player.getClassicSession().send(new ServerSpawnPlayerPacket(((int) packet.getRuntimeEntityId()), entity.getUsername(), Utils.mapCoords(position.getX(), ((float) player.getMinPosBedrock().getX()), ((float) player.getMaxPosBedrock().getX()), ((float) player.getMinPosClassic().getX()), ((float) player.getMaxPosClassic().getX())), pos.getY(), Utils.mapCoords(position.getZ(), ((float) player.getMinPosBedrock().getZ()), ((float) player.getMaxPosBedrock().getZ()), ((float) player.getMinPosClassic().getZ()), ((float) player.getMaxPosClassic().getZ())), rotation.getX(), rotation.getY()));
            });
            player.getClassicSession().send(new ServerPositionRotationPacket(((int) packet.getRuntimeEntityId()), Utils.mapCoords(position.getX(), ((float) player.getMinPosBedrock().getX()), ((float) player.getMaxPosBedrock().getX()), ((float) player.getMinPosClassic().getX()), ((float) player.getMaxPosClassic().getX())), pos.getY(), Utils.mapCoords(position.getZ(), ((float) player.getMinPosBedrock().getZ()), ((float) player.getMaxPosBedrock().getZ()), ((float) player.getMinPosClassic().getZ()), ((float) player.getMaxPosClassic().getZ())), rotation.getX(), rotation.getY()));
        }
    }
}

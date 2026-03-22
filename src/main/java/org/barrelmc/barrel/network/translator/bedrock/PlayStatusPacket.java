package org.barrelmc.barrel.network.translator.bedrock;

import com.github.steveice10.mc.classic.protocol.packet.server.ServerPositionRotationPacket;
import com.github.steveice10.mc.classic.protocol.data.game.PlayerIds;
import com.github.steveice10.packetlib.packet.Packet;
import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.player.StatusWorld;
import org.barrelmc.barrel.server.ProxyServer;
import org.barrelmc.barrel.utils.Utils;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.AuthoritativeMovementMode;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.SetLocalPlayerAsInitializedPacket;
import org.cloudburstmc.protocol.bedrock.packet.TickSyncPacket;

public class PlayStatusPacket implements BedrockPacketTranslator {

    @Override
    public boolean immediate() {
        return true;
    }

    @Override
    public void translate(BedrockPacket pk, Player player) {
        org.cloudburstmc.protocol.bedrock.packet.PlayStatusPacket packet = (org.cloudburstmc.protocol.bedrock.packet.PlayStatusPacket) pk;

        if (packet.getStatus() == org.cloudburstmc.protocol.bedrock.packet.PlayStatusPacket.Status.PLAYER_SPAWN) {
            TickSyncPacket tickSyncPacket = new TickSyncPacket();
            tickSyncPacket.setRequestTimestamp(0);
            tickSyncPacket.setResponseTimestamp(0);
            player.getBedrockClientSession().sendPacketImmediately(tickSyncPacket);

            if (player.getStartGamePacketCache().getAuthoritativeMovementMode() == AuthoritativeMovementMode.SERVER) {
                player.startSendingPlayerInput();
            }
            new Thread(player::sendWorld).start();
            player.setStatusWorld(StatusWorld.PLAYING);
            SetLocalPlayerAsInitializedPacket setLocalPlayerAsInitializedPacket = new SetLocalPlayerAsInitializedPacket();
            setLocalPlayerAsInitializedPacket.setRuntimeEntityId(player.getRuntimeEntityId());
            player.getBedrockClientSession().sendPacket(setLocalPlayerAsInitializedPacket);

            Vector3f pos = player.getLastServerPosition();
            Vector2f rotation = player.getLastServerRotation();
            float classicX = Utils.mapCoords(pos.getX(), ((float) player.getMinPosBedrock().getX()), ((float) player.getMaxPosBedrock().getX()), ((float) player.getMinPosClassic().getX()), ((float) player.getMaxPosClassic().getX()));
            float classicY = pos.getY();
            float classicZ = Utils.mapCoords(pos.getZ(), ((float) player.getMinPosBedrock().getZ()), ((float) player.getMaxPosBedrock().getZ()), ((float) player.getMinPosClassic().getZ()), ((float) player.getMaxPosClassic().getZ()));
            player.getClassicSession().send(new ServerPositionRotationPacket(PlayerIds.SELF, classicX, classicY, classicZ, rotation.getX(), rotation.getY()));
            if(player.getCpePacketsQueue().size() > 0){
                for(Packet pkq : player.getCpePacketsQueue()){
                    player.getClassicSession().send(pkq);
                }
            }
            if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(8), player.getExtensionsClassic())){
                player.sendMessage("GameMod: " + player.getGameMode().name().substring(0, 1).toUpperCase() + player.getGameMode().name().substring(1).toLowerCase(), PlayerIds.BOTTOMRIGHT1);
            }
            player.startForceSpawn();
        }
    }
}

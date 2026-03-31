package org.barrelmc.barrel.player;

import org.barrelmc.barrel.utils.Utils;
import org.barrelmc.barrel.server.ProxyServer;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.packet.PlayerActionPacket;
import org.cloudburstmc.protocol.bedrock.packet.SetLocalPlayerAsInitializedPacket;
import org.cloudburstmc.protocol.bedrock.packet.TickSyncPacket;
import org.cloudburstmc.protocol.bedrock.data.AuthoritativeMovementMode;
import org.cloudburstmc.protocol.bedrock.data.PlayerActionType;
import com.github.steveice10.mc.classic.protocol.data.game.PlayerIds;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerPositionRotationPacket;

public class PlayerForceSpawnThread implements Runnable{
    public Player player;
    public boolean forceSpawn = false;
    public void run(){
        if(player.getStatusWorld() == StatusWorld.PLAYING){
            return;
        }
        if(!forceSpawn){
            forceSpawn = true;
            return;
        }
        player.getWorldThread().submit(player::sendWorld);
        if(player.getStatusWorld() == StatusWorld.CHANGE_DIMENSION){
            PlayerActionPacket playerActionPacket = new PlayerActionPacket();
            playerActionPacket.setAction(PlayerActionType.DIMENSION_CHANGE_SUCCESS);
            playerActionPacket.setBlockPosition(Vector3i.ZERO);
            playerActionPacket.setResultPosition(Vector3i.ZERO);
            playerActionPacket.setFace(0);
            playerActionPacket.setRuntimeEntityId(player.getRuntimeEntityId());
            player.getBedrockClientSession().sendPacket(playerActionPacket);
        }
        if(player.getStatusWorld() == StatusWorld.PREPARING){
            TickSyncPacket tickSyncPacket = new TickSyncPacket();
            tickSyncPacket.setRequestTimestamp(0);
            tickSyncPacket.setResponseTimestamp(0);
            player.getBedrockClientSession().sendPacketImmediately(tickSyncPacket);
            if (player.getStartGamePacketCache().getAuthoritativeMovementMode() == AuthoritativeMovementMode.SERVER) {
                player.startSendingPlayerInput();
            }
            SetLocalPlayerAsInitializedPacket setLocalPlayerAsInitializedPacket = new SetLocalPlayerAsInitializedPacket();
            setLocalPlayerAsInitializedPacket.setRuntimeEntityId(player.getRuntimeEntityId());
            player.getBedrockClientSession().sendPacket(setLocalPlayerAsInitializedPacket);
            player.startLevelChunkProcess();
        }
        player.setStatusWorld(StatusWorld.PLAYING);
        Vector3f pos = player.getLastServerPosition();
        Vector2f rotation = player.getLastServerRotation();
        float classicX = Utils.mapCoords(pos.getX(), ((float) player.getMinPosBedrock().getX()), ((float) player.getMaxPosBedrock().getX()), ((float) player.getMinPosClassic().getX()), ((float) player.getMaxPosClassic().getX()));
        float classicY = pos.getY();
        float classicZ = Utils.mapCoords(pos.getZ(), ((float) player.getMinPosBedrock().getZ()), ((float) player.getMaxPosBedrock().getZ()), ((float) player.getMinPosClassic().getZ()), ((float) player.getMaxPosClassic().getZ()));
        player.getClassicSession().send(new ServerPositionRotationPacket(PlayerIds.SELF, classicX, classicY, classicZ, rotation.getX(), rotation.getY()));
        player.getEnvCpe().updateAll();
        if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(8), player.getExtensionsClassic())){
            player.sendMessage("GameMod: " + player.getGameMode().name().substring(0, 1).toUpperCase() + player.getGameMode().name().substring(1).toLowerCase(), PlayerIds.BOTTOMRIGHT1);
        }
    }
}

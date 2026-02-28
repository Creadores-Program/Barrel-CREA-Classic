package org.barrelmc.barrel.player;

import org.barrelmc.barrel.utils.Utils;
import org.barrelmc.barrel.server.ProxyServer;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.packet.PlayerActionPacket;
import org.cloudburstmc.protocol.bedrock.data.PlayerActionType;
import com.github.steveice10.mc.classic.protocol.data.game.PlayerIds;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerLevelDataPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerLevelFinalizePacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerPositionRotationPacket;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;
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
        byte[] compressedMap;
        try{
            compressedMap = compressMap(player.mapClassic);
        }catch(IOException ex){
            ex.printStackTrace();
            return;
        }
        int offset = 0;
        while(offset < compressedMap.length){
            int length = Math.min(1024, compressedMap.length - offset);
            byte[] chunk = new byte[length];
            System.arraycopy(compressedMap, offset, chunk, 0, length);
            offset += length;
            int percent = (int) ((100L * offset) / compressedMap.length);
            player.getClassicSession().send(new ServerLevelDataPacket(chunk, percent));
        }
        player.getClassicSession().send(new ServerLevelFinalizePacket(256, 256, 256));
        if(player.getStatusWorld() == StatusWorld.CHANGE_DIMENSION){
            PlayerActionPacket playerActionPacket = new PlayerActionPacket();
            playerActionPacket.setAction(PlayerActionType.DIMENSION_CHANGE_SUCCESS);
            playerActionPacket.setBlockPosition(Vector3i.ZERO);
            playerActionPacket.setResultPosition(Vector3i.ZERO);
            playerActionPacket.setFace(0);
            playerActionPacket.setRuntimeEntityId(player.getRuntimeEntityId());
            player.getBedrockClientSession().sendPacket(playerActionPacket);
        }
        player.setStatusWorld(StatusWorld.PLAYING);
        Vector3f pos = player.getLastServerPosition();
        Vector2f rotation = player.getLastServerRotation();
        float classicX = Utils.mapCoords(pos.getX(), ((float) player.getMinPosBedrock().getX()), ((float) player.getMaxPosBedrock().getX()), ((float) player.getMinPosClassic().getX()), ((float) player.getMaxPosClassic().getX()));
        float classicY = pos.getY();
        float classicZ = Utils.mapCoords(pos.getZ(), ((float) player.getMinPosBedrock().getZ()), ((float) player.getMaxPosBedrock().getZ()), ((float) player.getMinPosClassic().getZ()), ((float) player.getMaxPosClassic().getZ()));
        player.getClassicSession().send(new ServerPositionRotationPacket(PlayerIds.SELF, classicX, classicY, classicZ, rotation.getX(), rotation.getY()));
        if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(8), player.getExtensionsClassic())){
            player.sendMessage("GameMod: " + player.getGameMode().name().substring(0, 1).toUpperCase() + player.getGameMode().name().substring(1).toLowerCase(), PlayerIds.BOTTOMRIGHT1);
        }
    }
    private byte[] compressMap(byte[] mapData) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(mapData.length);
        dos.flush();
        ByteArrayOutputStream gzippedBase = new ByteArrayOutputStream();
        try (GZIPOutputStream gos = new GZIPOutputStream(gzippedBase)) {
            gos.write(mapData);
            gos.finish();
        }
        return baos.toByteArray();
    }
}
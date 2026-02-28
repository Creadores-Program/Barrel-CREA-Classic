package org.barrelmc.barrel.network.translator.bedrock;

import com.github.steveice10.mc.classic.protocol.packet.server.ServerLevelDataPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerLevelFinalizePacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerPositionRotationPacket;
import com.github.steveice10.mc.classic.protocol.data.game.PlayerIds;
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
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

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

            byte[] compressedMap = new byte[0];
            try{
                compressedMap = compressMap(player.mapClassic);
            }catch(IOException ex){
                ex.printStackTrace();
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
            if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(8), player.getExtensionsClassic())){
                player.sendMessage("GameMod: " + player.getGameMode().name().substring(0, 1).toUpperCase() + player.getGameMode().name().substring(1).toLowerCase(), PlayerIds.BOTTOMRIGHT1);
            }
            player.startForceSpawn();
        }
    }
    public byte[] compressMap(byte[] mapData) throws IOException {
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

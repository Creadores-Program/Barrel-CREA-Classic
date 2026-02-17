package org.barrelmc.barrel.network.translator.bedrock;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import org.barrelmc.barrel.network.converter.BlockConverter;
import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.utils.Utils;
import org.barrelmc.barrel.utils.nukkit.BitArray;
import org.barrelmc.barrel.utils.nukkit.BitArrayVersion;
import org.barrelmc.barrel.utils.Logger;
import org.barrelmc.barrel.server.ProxyServer;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.util.stream.NetworkDataInputStream;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.common.util.VarInts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;

public class LevelChunkPacket implements BedrockPacketTranslator {

    private static final byte[] FULL_LIGHT = new byte[2048];

    static {
        Arrays.fill(FULL_LIGHT, (byte) 0xff);
    }

    @Override
    public void translate(BedrockPacket pk, Player player) {
        org.cloudburstmc.protocol.bedrock.packet.LevelChunkPacket packet = (org.cloudburstmc.protocol.bedrock.packet.LevelChunkPacket) pk;

        int subChunksLength = packet.getSubChunksLength();
        int chunkX = packet.getChunkX();
        int chunkZ = packet.getChunkZ();

        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeBytes(packet.getData());

        ByteBuf chunkByteBuf = Unpooled.buffer();
        for (int sectionIndex = 0; sectionIndex < subChunksLength; sectionIndex++) {
            int chunkVersion = byteBuf.readByte();

            if (chunkVersion != 1 && chunkVersion != 8 && chunkVersion != 9) {
                // TODO: Support chunk version 0 (pm 3.0.0 legacy chunk)
                continue;
            }

            if (chunkVersion == 1) {
                networkDecodeVersionOne(byteBuf, chunkX, chunkZ);
                continue;
            }

            if (chunkVersion == 9) {
                networkDecodeVersionNine(byteBuf, chunkX, chunkZ, sectionIndex);
                continue;
            }

            networkDecodeVersionEight(byteBuf, chunkX, chunkZ, sectionIndex, byteBuf.readByte());
        }

        byteBuf.release();
        chunkByteBuf.release();
    }

    public void networkDecodeVersionNine(ByteBuf byteBuf, int chunkX, int chunkZ, int sectionIndex) {
        byte storageSize = byteBuf.readByte();
        byteBuf.readByte(); // height
        networkDecodeVersionEight(byteBuf, chunkX, chunkZ, sectionIndex, storageSize);
    }

    public void networkDecodeVersionEight(ByteBuf byteBuf, int chunkX, int chunkZ, int sectionIndex, byte storageSize) {
        for (int storageReadIndex = 0; storageReadIndex < storageSize; storageReadIndex++) {
            if (storageReadIndex > 1) {
                return;
            }
            byte paletteHeader = byteBuf.readByte();
            boolean isRuntime = (paletteHeader & 1) == 1;
            int paletteVersion = (paletteHeader | 1) >> 1;

            BitArrayVersion bitArrayVersion = BitArrayVersion.get(paletteVersion, true);

            int maxBlocksInSection = 4096;
            BitArray bitArray = bitArrayVersion.createPalette(maxBlocksInSection);
            int wordsSize = bitArrayVersion.getWordsForSize(maxBlocksInSection);

            for (int wordIterationIndex = 0; wordIterationIndex < wordsSize; wordIterationIndex++) {
                int word = byteBuf.readIntLE();
                bitArray.getWords()[wordIterationIndex] = word;
            }

            int paletteSize = VarInts.readInt(byteBuf);
            int[] sectionPalette = new int[paletteSize];
            NBTInputStream nbtStream = isRuntime ? null : new NBTInputStream(new NetworkDataInputStream(new ByteBufInputStream(byteBuf)));
            for (int i = 0; i < paletteSize; i++) {
                if (isRuntime) {
                    sectionPalette[i] = VarInts.readInt(byteBuf);
                } else {
                    try {
                        NbtMapBuilder map = ((NbtMap) nbtStream.readTag()).toBuilder();
                        map.replace("name", "minecraft:" + map.get("name").toString());
                        Logger logger = ProxyServer.getInstance().getLogger();
                        logger.info(map.build().toString());
                        //sectionPalette[i] = BlockPaletteTranslator.getBedrockBlockId(BlockPaletteTranslator.bedrockStateFromNBTMap(map.build()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            int index = 0;
            for (int x = 0; x < 16; x++) {
                int worldX = (chunkX << 4) + x;
                if(player.getMaxPosBedrock().getX() < worldX || player.getMinPosBedrock().getX() > worldX){
                    continue;
                }
                for (int z = 0; z < 16; z++) {
                    int worldZ = (chunkZ << 4) + z;
                    if(player.getMaxPosBedrock().getZ() < worldZ || player.getMinPosBedrock().getZ() > worldZ){
                        continue;
                    }
                    for (int y = 0; y < 16; y++) {
                        int worldY = (sectionIndex << 4) + y;
                        if(player.getMaxPosBedrock().getY() < worldY || player.getMinPosBedrock().getY() > worldY){
                            continue;
                        }
                        int paletteIndex = bitArray.get(index);
                        int mcbeBlockId = sectionPalette[paletteIndex];
                        int classicStateId = BlockConverter.bedrockRuntimeToClassicStateId(mcbeBlockId);
                        int classicX = Utils.mapCoords(worldX, player.getMinPosBedrock().getX(), player.getMaxPosBedrock().getX(), player.getMinPosClassic().getX(), player.getMaxPosClassic().getX());
                        int classicY = worldY;
                        int classicZ = Utils.mapCoords(worldZ, player.getMinPosBedrock().getZ(), player.getMaxPosBedrock().getZ(), player.getMinPosClassic().getZ(), player.getMaxPosClassic().getZ());

                        if (storageReadIndex == 0) {
                            player.mapClassic[(classicY * 256 + classicZ) * 256 + classicX] = (byte) classicStateId;
                        } else if (classicStateId == 8 || classicStateId == 9) { // water
                            int layer0 = (int) player.mapClassic[(classicY * 256 + classicZ) * 256 + classicX];
                            if (layer0 == 0) {
                                player.mapClassic[(classicY * 256 + classicZ) * 256 + classicX] = (byte) classicStateId;
                            }
                            
                        }

                        index++;
                    }
                }
            }
        }
    }

    public void networkDecodeVersionOne(ByteBuf byteBuf, int chunkX, int chunkZ) {
        networkDecodeVersionEight(byteBuf, chunkX, chunkZ, 0, (byte) 1);
    }

    @Override
    public boolean immediate() {
        return true;
    }
}

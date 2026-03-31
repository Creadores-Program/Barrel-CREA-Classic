package org.barrelmc.barrel.player;

import java.util.ArrayList;
import java.util.List;

import org.barrelmc.barrel.server.ProxyServer;

import com.github.steveice10.mc.classic.protocol.packet.server.ServerBulkBlockUpdatePacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerSetBlockPacket;

public class LevelChunkProcess implements Runnable {

    private byte[] blocks = new byte[Player.WORLDLEN];
    private int[] indices = new int[Player.WORLDLEN];
    private List<Data> blocksL = new ArrayList<>();
    private List<Data> blocksLP = new ArrayList<>();
    private boolean change = true;
    public Player player;
    public boolean supportBU = false;

    public void run(){
        try {
            if(player.getStatusWorld() != StatusWorld.PLAYING || blocksL.size() < 1){
                return;
            }
            if(change){
                change = false;
                return;
            }
            if(blocksL.size() == 1){
                synchronized(blocksL){
                    Data block = blocksL.get(0);
                    player.getClassicSession().send(new ServerSetBlockPacket(block.x, block.y, block.z, (int) block.block));
                }
                return;
            }
            send();
        } catch (Exception e) {
            ProxyServer.getInstance().getLogger().error("Error in update blocks!", e);
        }
    }
    public void add(int b, int x, int y, int z, int in){
        if(!change){
            change = true;
        }
        synchronized(blocksL){
            blocksL.add(new Data(b, x, y, z, in));
            if(blocksL.size() >= Player.WORLDLEN){
                send();
            }
        }
    }
    private void send(){
        List<Data> blocksLT = this.blocksL;
        this.blocksL = this.blocksLP;
        this.blocksLP = blocksLT;
        int count = blocksLT.size();
        for(int n = 0; n < count; n++){
            Data d = blocksLT.get(n);
            if(supportBU){
                blocks[n] = d.block;
                indices[n] = d.indice;
            }else{
                player.getClassicSession().send(new ServerSetBlockPacket(d.x, d.y, d.z, (int) d.block));
            }
        }
        blocksLT.clear();
        if(supportBU){
            byte[] blocksTemp = new byte[count];
            System.arraycopy(blocks, 0, blocksTemp, 0, Math.min(blocksTemp.length, blocks.length));
            int[] indicesTemp = new int[count];
            System.arraycopy(indices, 0, indicesTemp, 0, Math.min(indicesTemp.length, indices.length));
            player.getClassicSession().send(new ServerBulkBlockUpdatePacket(indicesTemp, blocksTemp));
        }
    }
}
class Data{
    byte block;
    int x;
    int y;
    int z;
    int indice;
    Data(int b, int x, int y, int z, int in){
        this.block = (byte) b;
        this.x = x;
        this.y = y;
        this.z = z;
        this.indice = in;
    }
}
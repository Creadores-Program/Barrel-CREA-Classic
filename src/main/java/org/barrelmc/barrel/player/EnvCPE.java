package org.barrelmc.barrel.player;

import com.github.steveice10.mc.classic.protocol.packet.server.ServerEnvColorsPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerEnvSetWeatherTypePacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerSetSpawnpointPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerHackControlPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.barrelmc.barrel.utils.Utils;
import org.barrelmc.barrel.server.ProxyServer;

public class EnvCPE {
    private ServerEnvColorsPacket color1;
    private ServerEnvColorsPacket color2;
    private ServerEnvColorsPacket color3;
    private ServerEnvColorsPacket color4;
    private ServerEnvColorsPacket color5;
    private ServerEnvColorsPacket color6;

    private ServerEnvSetWeatherTypePacket weather;

    private ServerSetSpawnpointPacket spawnPoint;

    private ServerHackControlPacket hackControl;
    
    private Player player;

    public EnvCPE(Player player){
        this.player = player;
    }

    public void updateAmbient(ServerEnvColorsPacket color1, ServerEnvColorsPacket color2, ServerEnvColorsPacket color3, ServerEnvColorsPacket color4){
        this.color1 = color1;
        this.color2 = color2;
        this.color3 = color3;
        this.color4 = color4;
        this.sendPacket(color1);
        this.sendPacket(color2);
        this.sendPacket(color3);
        this.sendPacket(color4);
    }
    public void updateDimention(ServerEnvColorsPacket color5, ServerEnvColorsPacket color6){
        this.color5 = color5;
        this.color6 = color6;
        this.sendPacket(color5);
        this.sendPacket(color6);
    }
    public void setSpawn(ServerSetSpawnpointPacket spawnPoint){
        this.spawnPoint = spawnPoint;
        this.sendPacket(spawnPoint);
    }
    public void setWeather(ServerEnvSetWeatherTypePacket weather){
        this.weather = weather;
        this.sendPacket(weather);
    }
    public void setHacks(ServerHackControlPacket hackControl){
        this.hackControl = hackControl;
        this.sendPacket(hackControl);
    }
    public void updateAll(){
        if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(3), player.getExtensionsClassic()) && this.color1 != null && this.color2 != null && this.color3 != null && this.color4 != null){
            this.sendPacket(this.color1);
            this.sendPacket(this.color2);
            this.sendPacket(this.color3);
            this.sendPacket(this.color4);
            if(this.color5 != null && this.color6 != null){
                this.sendPacket(this.color5);
                this.sendPacket(this.color6);
            }
        }
        if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(13), player.getExtensionsClassic()) && this.spawnPoint != null){
            this.sendPacket(this.spawnPoint);
        }
        if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(5), player.getExtensionsClassic()) && this.weather != null){
            this.sendPacket(this.weather);
        }
        if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(6), player.getExtensionsClassic()) && this.hackControl != null){
            this.sendPacket(this.hackControl);
        }
    }
    private void sendPacket(Packet pk){
        if(player.getStatusWorld() == StatusWorld.LOGIN){
            player.getCpePacketsQueue().add(pk);
            return;
        }
        player.getClassicSession().send(pk);
    }
}

/*
 * Copyright (c) 2021 BarrelMC Team
 * This project is licensed under the MIT License
 */

package org.barrelmc.barrel.server;

import com.github.steveice10.mc.classic.protocol.data.heartbeat.ServerInfo;
import com.github.steveice10.mc.classic.protocol.data.heartbeat.ServerInfoBuilder;
import com.github.steveice10.mc.classic.protocol.ClassicConstants;
import com.github.steveice10.mc.classic.protocol.ClassicProtocol;
import com.github.steveice10.mc.classic.protocol.VerifyUsersListener;
import com.github.steveice10.mc.classic.protocol.data.game.ExtNames;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerExtEntryPacket;
import com.github.steveice10.packetlib.Server;
import com.github.steveice10.packetlib.event.server.ServerAdapter;
import com.github.steveice10.packetlib.event.server.ServerClosedEvent;
import com.github.steveice10.packetlib.event.server.SessionAddedEvent;
import com.github.steveice10.packetlib.event.server.SessionRemovedEvent;
import com.github.steveice10.packetlib.tcp.TcpServer;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.barrelmc.barrel.auth.AuthManager;
import org.barrelmc.barrel.auth.server.AuthServer;
import org.barrelmc.barrel.config.Config;
import org.barrelmc.barrel.network.JavaPacketHandler;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.utils.FileManager;
import org.barrelmc.barrel.utils.NbtBlockDefinitionRegistry;
import org.cloudburstmc.protocol.bedrock.data.
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.nbt.NbtUtils;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.v662.Bedrock_v662;
import org.yaml.snakeyaml.Yaml;
import org.barrelmc.barrel.utils.Logger;
import org.barrelmc.barrel.utils.nukkit.TextFormat;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;
import java.net.URL;

public class ProxyServer {

    @Getter
    private static ProxyServer instance = null;
    @Getter
    private final Map<String, Player> bedrockPlayers = new ConcurrentHashMap<>();
    @Getter
    private final BedrockCodec bedrockPacketCodec = Bedrock_v662.CODEC;

    @Getter
    private final Path dataPath;

    @Getter
    private Config config;

    @Getter
    private String defaultSkinData;
    @Getter
    private String defaultSkinGeometry;

    @Getter
    private CompoundTag registryCodec;

    @Getter
    private NbtBlockDefinitionRegistry blockDefinitions;

    @Getter
    private Logger logger;

    @Getter
    private List<String> extDatapacks = new ObjectArrayList<>(){{
        add(new ServerExtEntryPacket(1, ExtNames.CLICKDISTANCE));
        add(new ServerExtEntryPacket(1, ExtNames.CUSTOMBLOCKS));
        add(new ServerExtEntryPacket(2, ExtNames.EXTPLAYERLIST));
        add(new ServerExtEntryPacket(1, ExtNames.ENVCOLORS));
        add(new ServerExtEntryPacket(1, ExtNames.CHANGEMODEL));
        add(new ServerExtEntryPacket(1, ExtNames.ENVWEATHERTYPE));
        add(new ServerExtEntryPacket(1, ExtNames.HACKCONTROL));
        add(new ServerExtEntryPacket(1, ExtNames.EMOTEFIX));
        add(new ServerExtEntryPacket(2, ExtNames.MESSAGETYPES));
        add(new ServerExtEntryPacket(1, ExtNames.LONGERMESSAGES));
        add(new ServerExtEntryPacket(1, ExtNames.BULKBLOCKUPDATE));
        add(new ServerExtEntryPacket(1, ExtNames.PLAYERCLICK));
        add(new ServerExtEntryPacket(1, ExtNames.EXTENTITYPOSITIONS));
        add(new ServerExtEntryPacket(1, ExtNames.INSTANTMOTD));
        add(new ServerExtEntryPacket(1, ExtNames.SETSPAWNPOINT));
        add(new ServerExtEntryPacket(1, ExtNames.EXTENTITYTELEPORT));
    }};

    public ProxyServer(String dataPath) {
        instance = this;
        this.logger = new Logger(TextFormat.GOLD.getAnsiCode()+"BarrelMC");
        this.getLogger().info("Barrel Classic 0.1.0 Starting...");
        this.dataPath = Paths.get(dataPath);
        if (!initConfig()) {
            this.getLogger().emergency("Config file not found! Terminating...");
            System.exit(1);
        }
        loadRegistryCodec();
        loadBlockDefinitions();
        loadDefaultSkin();
        startServer();
    }

    private boolean initConfig() {
        File configFile = new File(dataPath.toFile(), "config.yml");
        if (!configFile.exists()) {
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.yml")) {
                if (inputStream == null) {
                    return false;
                }
                Files.createDirectories(configFile.getParentFile().toPath());
                Files.copy(inputStream, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                return false;
            }
        }
        try {
            this.config = new Yaml().loadAs(Files.newBufferedReader(configFile.toPath()), Config.class);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private void loadRegistryCodec() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("registry-codec.nbt");
             DataInputStream dataInputStream = new DataInputStream(new GZIPInputStream(Objects.requireNonNull(inputStream)))) {
            registryCodec = (CompoundTag) NBTIO.readTag((InputStream) dataInputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load registry codec", e);
        }
    }

    private void loadBlockDefinitions() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("block_palette.nbt")) {
            assert inputStream != null;
            try (NBTInputStream nbtInputStream = NbtUtils.createGZIPReader(inputStream)) {
                Object object = nbtInputStream.readTag();
                if (object instanceof NbtMap) {
                    NbtMap blocksTag = (NbtMap) object;
                    blockDefinitions = new NbtBlockDefinitionRegistry(blocksTag.getList("blocks", NbtType.COMPOUND));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load block definitions", e);
        }
    }

    private void loadDefaultSkin() {
        try {
            defaultSkinData = FileManager.getFileContents(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("skin/skin_data.txt")));
            defaultSkinGeometry = FileManager.getFileContents(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("skin/skin_geometry.json")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startServer() {
        Server server = new TcpServer(this.config.getBindAddress(), this.config.getPort(), ClassicProtocol::new);
        if (this.config.getPremiumPlayerClassic()) {
            server.addListener(new VerifyUsersListener());
            server.setGlobalFlag(ClassicConstants.SERVER_INFO_BUILDER_KEY, (ServerInfoBuilder) s -> new ServerInfo(this.config.getMotd(), server.getPort(), true, 0, this.config.getMaxplayers()));
        }
        String StopMSG = this.config.getShutdownMessage();
        server.addListener(new ServerAdapter() {
            @Override
            public void serverClosed(ServerClosedEvent event) {
                for (Player player : getAllPlayers()) {
                    player.disconnect(StopMSG);
                }
                getLogger().info("Server closed.");
            }
            @Override
            public void sessionAdded(SessionAddedEvent event) {
                event.getSession().addListener(new ClassicPacketHandler());
            }
            @Override
            public void sessionRemoved(SessionRemovedEvent event) {
                String username = event.getSession().getFlag(ClassicConstants.USERNAME_KEY);
                if (username == null) return;
                    if(isBedrockPlayer(username)){
                        getPlayerByName(username).disconnect("logged out");
                    }
                    AuthManager.getInstance().getLoginPlayers().remove(username);
                    if (AuthManager.getInstance().getTimers().containsKey(username)) {
                        AuthManager.getInstance().getTimers().get(username).cancel();
                        AuthManager.getInstance().getTimers().remove(username);
                    }
                    getLogger().info(username + " logged out");
            }
        });
        getLogger().info("Binding to " + this.config.getBindAddress() + " on port " + this.config.getPort());
        server.bind();
        getLogger().info("BarrelProxy " + TextFormat.GREEN.getAnsiCode() + "CREA " + TextFormat.AQUA.getAnsiCode() + "Classic" + TextFormat.RESET.getAnsiCode() + " is running on [" + this.config.getBindAddress() + ":" + this.config.getPort() + "]");
        getLogger().info("Done!");
    }

    public Player getPlayerByName(String username) {
        return this.bedrockPlayers.get(username);
    }

    public void addBedrockPlayer(Player player) {
        this.bedrockPlayers.put(player.getClassicUsername(), player);
    }

    public void removeBedrockPlayer(String classicUsername) {
        this.bedrockPlayers.remove(classicUsername);
    }

    public boolean isBedrockPlayer(String username) {
        return this.bedrockPlayers.containsKey(username);
    }
}

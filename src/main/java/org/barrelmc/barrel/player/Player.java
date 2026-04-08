/*
 * Copyright (c) 2021 BarrelMC Team
 * This project is licensed under the MIT License
 */

package org.barrelmc.barrel.player;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.github.steveice10.mc.classic.protocol.data.game.PlayerIds;
import com.github.steveice10.mc.classic.protocol.packet.client.ClientExtEntryPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerChatPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerPingPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerSetClickDistancePacket;
import com.github.steveice10.mc.classic.protocol.packet.client.ClientIdentificationPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerLevelDataPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerLevelFinalizePacket;
//import com.github.steveice10.mc.protocol.packet.ingame.clientbound.level.ClientboundSetChunkCacheCenterPacket;
import com.github.steveice10.packetlib.Session;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
import org.barrelmc.barrel.config.Config;
import org.barrelmc.barrel.math.Vector3;
import org.barrelmc.barrel.network.BedrockBatchHandler;
import org.barrelmc.barrel.network.translator.PacketTranslatorManager;
import org.barrelmc.barrel.server.ProxyServer;
import org.barrelmc.barrel.utils.Utils;
import org.barrelmc.barrel.utils.nukkit.TextFormat;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.netty.channel.raknet.RakChannelFactory;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.cloudburstmc.protocol.bedrock.BedrockClientSession;
import org.cloudburstmc.protocol.bedrock.data.*;
import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.ItemUseTransaction;
import org.cloudburstmc.protocol.bedrock.netty.initializer.BedrockClientInitializer;
import org.cloudburstmc.protocol.bedrock.packet.LoginPacket;
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket;
import org.cloudburstmc.protocol.bedrock.packet.RequestNetworkSettingsPacket;
import org.cloudburstmc.protocol.bedrock.packet.StartGamePacket;
import org.cloudburstmc.protocol.bedrock.util.EncryptionUtils;

import java.net.InetSocketAddress;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

public class Player extends Vector3 {

    @Getter
    private final Session classicSession;
    @Getter
    private BedrockClientSession bedrockClientSession;
    @Getter
    private Channel channel;
    @Getter
    private final PacketTranslatorManager packetTranslatorManager;

    @Getter
    private ECPublicKey publicKey;
    @Getter
    private ECPrivateKey privateKey;

    @Setter
    @Getter
    private long runtimeEntityId;
    @Getter
    private String username;
    @Getter
    private String classicUsername;
    @Getter
    private String xuid;
    @Getter
    private String UUID;

    @Setter
    @Getter
    private int scoreSortorder;

    @Setter
    @Getter
    private StartGamePacket startGamePacketCache;

    @Setter
    @Getter
    private boolean traslateAd = false;

    @Getter
    private EnvCPE envCpe = null;

    private boolean tickPlayerInputStarted = false;
    private final ScheduledExecutorService playerInputExecutor = Executors.newScheduledThreadPool(4);
    
    @Getter
    private final ExecutorService worldThread = Executors.newCachedThreadPool();

    @Setter
    @Getter
    private Vector3f oldPosition;

    @Setter
    @Getter
    private Vector3f lastServerPosition;

    @Setter
    @Getter
    private Vector2f lastServerRotation;

    @Setter
    @Getter
    private boolean isImmobile = false;

    @Setter
    @Getter
    private boolean isSneaking = false;
    @Setter
    @Getter
    private boolean isSprinting = false;
    @Setter
    @Getter
    private PlayerActionType diggingStatus;
    @Setter
    @Getter
    private Vector3i diggingPosition;

    @Setter
    @Getter
    private GameType gameMode = GameType.ADVENTURE;

    @Getter
    private final Set<PlayerAuthInputData> playerAuthInputData = EnumSet.noneOf(PlayerAuthInputData.class);
    @Getter
    private final List<PlayerBlockActionData> playerAuthInputActions = new ObjectArrayList<>();
    @Setter
    @Getter
    private ItemUseTransaction playerAuthInputItemUseTransaction = null;

    @Getter
    @Setter
    private int hotbarSlot = 0;

    @Getter
    @Setter
    private String language = "en-US";

    @Getter
    private List<ClientExtEntryPacket> extensionsClassic = new ObjectArrayList<>();

    public static final int WORLDLEN = 256;

    private static final int WORLDTOTALLEN = WORLDLEN * WORLDLEN * WORLDLEN;

    public static final String GAMEMODE_STR = TextFormat.GREEN_CC+"GameMode: ";

    private static final ServerLevelFinalizePacket FINALIZE_WORLD_PK = new ServerLevelFinalizePacket(256, 256, 256);

    public static final ServerSetClickDistancePacket REACH_SURVIVAL = new ServerSetClickDistancePacket((short) 160);

    public static final ServerSetClickDistancePacket REACH_CREATIVE = new ServerSetClickDistancePacket((short) 224);

    private static final String disconTransDerect = "disconnectionScreen.";

    @Getter
    private ByteBuffer mapClassic = ByteBuffer.allocateDirect(WORLDTOTALLEN);

    @Getter
    @Setter
    private Vector3i minPosClassic = Vector3i.from(0, 0, 0);

    @Getter
    @Setter
    private Vector3i maxPosClassic = Vector3i.from(255, 255, 255);

    @Getter
    @Setter
    private Vector3i maxPosBedrock = Vector3i.from(127, 255, 127);

    @Getter
    @Setter
    private Vector3i minPosBedrock = Vector3i.from(-128, 0, -128);

    @Getter
    @Setter
    private StatusWorld statusWorld = StatusWorld.LOGIN;

    @Getter
    private PlayerForceSpawnThread playerForceSpawnThread;

    public String msgPlayer = TextFormat.VOID_STR;

    @Getter
    private LevelChunkProcess levelChunkProcess;

    @Getter
    @Setter
    private int dimension = 0;

    @Getter
    @Setter
    private int customBlocksLevel = 0;

    @Getter
    @Setter
    private int extSize = 0;

    @Getter
    private Map<Long, Entity> entitysUnspawn = new ConcurrentHashMap<>();

    @Getter
    private Map<Long, Entity> entitysSpawned = new ConcurrentHashMap<>();

    @Getter
    private Map<Long, Long> entitysIndex = new ConcurrentHashMap<>();

    private static final int MAXLENMSG = 64;

    public Player(ClientIdentificationPacket loginPacket, Session classicSession) {
        this.envCpe = new EnvCPE(this);
        this.packetTranslatorManager = new PacketTranslatorManager(this);
        this.classicSession = classicSession;
        this.offlineLogin(loginPacket);
    }

    public void startSendingPing(){
        PlayerPingThread playerPingThread = new PlayerPingThread();
        playerPingThread.player = this;
        playerInputExecutor.scheduleAtFixedRate(playerPingThread, 0, 4, TimeUnit.SECONDS);
    }

    public void startForceSpawn(){
        this.playerForceSpawnThread = new PlayerForceSpawnThread();
        playerForceSpawnThread.player = this;
        playerInputExecutor.scheduleAtFixedRate(playerForceSpawnThread, 0, 800, TimeUnit.MILLISECONDS);
    }
    public void startLevelChunkProcess(){
        this.levelChunkProcess = new LevelChunkProcess();
        levelChunkProcess.player = this;
        if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(10), extensionsClassic)){
            levelChunkProcess.supportBU = true;
        }
        playerInputExecutor.scheduleAtFixedRate(levelChunkProcess, 0, 800, TimeUnit.MILLISECONDS);
    }

    public void startSendingPlayerInput() {
        if (!tickPlayerInputStarted) {
            tickPlayerInputStarted = true;

            PlayerAuthInputThread playerAuthInputThread = new PlayerAuthInputThread();
            playerAuthInputThread.player = this;
            playerAuthInputThread.tick = getStartGamePacketCache().getCurrentTick();

            playerInputExecutor.scheduleAtFixedRate(playerAuthInputThread, 0, 50, TimeUnit.MILLISECONDS);
        }
    }
    
    private void offlineLogin(ClientIdentificationPacket classicLoginPacket) {
        this.xuid = TextFormat.VOID_STR;
        this.username = this.classicUsername = classicLoginPacket.getUsername();
        this.UUID = java.util.UUID.nameUUIDFromBytes(("CC"+this.classicUsername).getBytes(StandardCharsets.UTF_8)).toString();
        Config config = ProxyServer.getInstance().getConfig();
        InetSocketAddress bedrockAddress = new InetSocketAddress(config.getBedrockAddress(), config.getBedrockPort());
        try {
            channel = new Bootstrap().channelFactory(RakChannelFactory.client(NioDatagramChannel.class))
                    .group(new NioEventLoopGroup())
                    .option(RakChannelOption.RAK_PROTOCOL_VERSION, ProxyServer.getInstance().getBedrockPacketCodec().getRaknetProtocolVersion())
                    .handler(new BedrockClientInitializer() {
                        @Override
                        protected void initSession(BedrockClientSession session) {
                            bedrockClientSession = session;
                            session.setCodec(ProxyServer.getInstance().getBedrockPacketCodec());
                            session.setPacketHandler(new BedrockBatchHandler(Player.this));

                            RequestNetworkSettingsPacket requestNetworkSettingsPacket = new RequestNetworkSettingsPacket();
                            requestNetworkSettingsPacket.setProtocolVersion(ProxyServer.getInstance().getBedrockPacketCodec().getProtocolVersion());
                            session.sendPacketImmediately(requestNetworkSettingsPacket);
                        }
                    })
                    .connect(bedrockAddress)
                    .awaitUninterruptibly().channel();
            ProxyServer.getInstance().addBedrockPlayer(this);
        } catch (Exception exception) {
            classicSession.disconnect("Failed to connect: " + exception);
        }
    }

    public LoginPacket getLoginPacket() {
        LoginPacket loginPacket = new LoginPacket();

        KeyPair ecdsa384KeyPair = EncryptionUtils.createKeyPair();
        this.publicKey = (ECPublicKey) ecdsa384KeyPair.getPublic();
        this.privateKey = (ECPrivateKey) ecdsa384KeyPair.getPrivate();

        String publicKeyBase64 = Base64.getEncoder().encodeToString(this.publicKey.getEncoded());

        JSONObject chain = new JSONObject();
        chain.put("exp", Instant.now().getEpochSecond() + TimeUnit.HOURS.toSeconds(6));
        chain.put("identityPublicKey", publicKeyBase64);
        chain.put("nbf", Instant.now().getEpochSecond() - TimeUnit.HOURS.toSeconds(6));

        JSONObject extraData = new JSONObject();
        extraData.put("identity", this.UUID);
        extraData.put("XUID", this.xuid);
        extraData.put("displayName", this.username);
        chain.put("extraData", extraData);

        JSONObject jwtHeader = new JSONObject();
        jwtHeader.put("alg", "ES384");
        jwtHeader.put("x5u", publicKeyBase64);

        String jwt = generateJwt(jwtHeader, chain);

        JSONArray chainDataJsonArray = new JSONArray();
        chainDataJsonArray.add(jwt);

        for (Object o : chainDataJsonArray) {
            loginPacket.getChain().add((String) o);
        }

        loginPacket.setExtra(this.getSkinData());
        loginPacket.setProtocolVersion(ProxyServer.getInstance().getBedrockPacketCodec().getProtocolVersion());
        return loginPacket;
    }

    private String getSkinData() {
        String publicKeyBase64 = Base64.getEncoder().encodeToString(this.publicKey.getEncoded());

        JSONObject jwtHeader = new JSONObject();
        jwtHeader.put("alg", "ES384");
        jwtHeader.put("x5u", publicKeyBase64);

        JSONObject skinData = new JSONObject();

        skinData.put("AnimatedImageData", new JSONArray());
        skinData.put("ArmSize", TextFormat.VOID_STR);
        skinData.put("CapeData", TextFormat.VOID_STR);
        skinData.put("CapeId", TextFormat.VOID_STR);
        skinData.put("PlayFabId", java.util.UUID.randomUUID().toString());
        skinData.put("CapeImageHeight", 0);
        skinData.put("CapeImageWidth", 0);
        skinData.put("CapeOnClassicSkin", false);
        skinData.put("ClientRandomId", new Random().nextLong());
        skinData.put("CompatibleWithClientSideChunkGen", false);
        skinData.put("CurrentInputMode", 1);
        skinData.put("DefaultInputMode", 1);
        skinData.put("DeviceId", java.util.UUID.randomUUID().toString());
        skinData.put("DeviceModel", "Barrel CREA Classic");
        skinData.put("DeviceOS", 7);
        skinData.put("GameVersion", ProxyServer.getInstance().getBedrockPacketCodec().getMinecraftVersion());
        skinData.put("GuiScale", 0);
        skinData.put("LanguageCode", this.language);
        skinData.put("PersonaPieces", new JSONArray());
        skinData.put("PersonaSkin", false);
        skinData.put("PieceTintColors", new JSONArray());
        skinData.put("PlatformOfflineId", TextFormat.VOID_STR);
        skinData.put("PlatformOnlineId", TextFormat.VOID_STR);
        skinData.put("PremiumSkin", false);
        skinData.put("SelfSignedId", this.UUID);
        skinData.put("ServerAddress", ProxyServer.getInstance().getConfig().getBedrockAddress() + ":" + ProxyServer.getInstance().getConfig().getBedrockPort());
        skinData.put("SkinAnimationData", TextFormat.VOID_STR);
        skinData.put("SkinColor", "#0");
        skinData.put("SkinData", Utils.usernameToSkinData(this.classicUsername));
        skinData.put("SkinGeometryData", Base64.getEncoder().encodeToString(ProxyServer.getInstance().getDefaultSkinGeometry().getBytes()));
        skinData.put("SkinId", this.UUID + ".Custom");
        skinData.put("SkinImageHeight", 64);
        skinData.put("SkinImageWidth", 64);
        skinData.put("SkinResourcePatch", "ewogICAiZ2VvbWV0cnkiIDogewogICAgICAiZGVmYXVsdCIgOiAiZ2VvbWV0cnkuaHVtYW5vaWQuY3VzdG9tIgogICB9Cn0K");
        skinData.put("ThirdPartyName", this.username);
        skinData.put("ThirdPartyNameOnly", false);
        skinData.put("UIProfile", 0);
        skinData.put("IsEditorMode", 0);
        skinData.put("TrustedSkin", 1);
        skinData.put("SkinGeometryDataEngineVersion", Base64.getEncoder().encodeToString(ProxyServer.getInstance().getBedrockPacketCodec().getMinecraftVersion().getBytes()));
        skinData.put("OverrideSkin", false);

        return generateJwt(jwtHeader, skinData);
    }

    private String generateJwt(JSONObject jwtHeader, JSONObject chain) {
        String header = Base64.getUrlEncoder().withoutPadding().encodeToString(jwtHeader.toJSONString().getBytes());
        String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(chain.toJSONString().getBytes());

        byte[] dataToSign = (header + "." + payload).getBytes();
        byte[] signatureBytes = null;
        try {
            Signature signature = Signature.getInstance("SHA384withECDSA");
            signature.initSign(this.privateKey);
            signature.update(dataToSign);
            signatureBytes = Utils.DERToJOSE(signature.sign(), Utils.AlgorithmType.ECDSA384);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException ignored) {
        }
        String signatureString = Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes);

        return header + "." + payload + "." + signatureString;
    }

    public void sendMessage(String message) {
        if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(8), this.extensionsClassic)){
            this.sendMessage(message, PlayerIds.CHAT);
        }else{
            this.sendMessage(message, PlayerIds.CONSOLE);
        }
    }

    public void sendMessage(String message, int playerId){
        if(!Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(15), this.extensionsClassic)){
            message = Utils.sanitizeText(message);
        }
        String[] messagesClassic = Utils.splitStringL(TextFormat.colorizeToCc(message), MAXLENMSG);
        if(messagesClassic.length < 2){
            this.classicSession.send(new ServerChatPacket(playerId, messagesClassic[0]));
            return;
        }
        for (String msg : messagesClassic) {
            this.classicSession.send(new ServerChatPacket(playerId, msg));
        }
    }
    
    public void sendTip(String message) {
        if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(8), this.extensionsClassic)){
            this.sendMessage(message, PlayerIds.BOTTOMRIGHT3);
        }else{
            this.sendMessage(message, PlayerIds.CONSOLE);
        }
    }

    public void disconnect(String reason) {
        playerInputExecutor.shutdown();
        worldThread.shutdown();
        try {
            this.bedrockClientSession.disconnect();
        } catch (Throwable ignored) {
        }
        if (this.channel.isOpen()) {
            this.channel.disconnect();
            this.channel.parent().disconnect();
        }
        if(reason.contains(disconTransDerect)){
            reason = ProxyServer.getInstance().getLangManager().translate(reason, null);
        }
        this.classicSession.disconnect(TextFormat.colorizeToCc(reason));
        ProxyServer.getInstance().removeBedrockPlayer(classicUsername);
        this.mapClassic = null;
        ProxyServer.getInstance().getLogger().info(classicUsername + " disconnected: " + reason);
    }

    @Override
    public void setPosition(Vector3f vector3f) {
        super.setPosition(vector3f);
    }

    @Override
    public void setPosition(double x, double y, double z) {
        super.setPosition(x, y, z);
    }

    public void sendWorld(){
        byte[] compressedMap = new byte[0];
        try{
            ByteBuffer copyW = this.mapClassic.duplicate();
            copyW.clear();
            compressedMap = compressMap(copyW);
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
            this.getClassicSession().send(new ServerLevelDataPacket(chunk, percent));
            try{
                Thread.sleep(10);
            }catch(InterruptedException ex){
                Thread.currentThread().interrupt();
            }
        }
        this.getClassicSession().send(FINALIZE_WORLD_PK);
    }

    private byte[] compressMap(ByteBuffer mapData) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GZIPOutputStream gos = new GZIPOutputStream(baos)) {
            DataOutputStream dos = new DataOutputStream(gos);
            dos.writeInt(WORLDTOTALLEN);
            dos.flush();
            WritableByteChannel channel = Channels.newChannel(dos);
            channel.write(mapData);
            dos.flush();
            gos.finish();
        }
        return baos.toByteArray();
    }
}

class PlayerPingThread implements Runnable{
    public Player player;
    public void run(){
        if(player.getClassicSession().isConnected()){
            player.getClassicSession().send(new ServerPingPacket());
        }
    }
}

class PlayerAuthInputThread implements Runnable {
    public Player player;
    public long tick;

    public void run() {
        try {
            if (player.getBedrockClientSession().isConnected()) {
                ++tick;

                PlayerAuthInputPacket pk = new PlayerAuthInputPacket();

                pk.setPosition(player.getVector3f());
                pk.setRotation(Vector3f.from(player.getPitch(), player.getYaw(), player.getYaw()));
                pk.setMotion(Vector2f.ZERO);
                pk.setInputInteractionModel(InputInteractionModel.CROSSHAIR);
                pk.setInputMode(InputMode.MOUSE);
                pk.setPlayMode(ClientPlayMode.SCREEN);
                pk.setVrGazeDirection(null);
                pk.setTick(tick);
                pk.setDelta(Vector3f.from(player.getVector3f().getX() - player.getOldPosition().getX(), player.getVector3f().getY() - player.getOldPosition().getY(), player.getVector3f().getZ() - player.getOldPosition().getZ()));
                pk.setItemStackRequest(null);
                pk.setItemUseTransaction(player.getPlayerAuthInputItemUseTransaction());
                pk.setAnalogMoveVector(Vector2f.ZERO);

                pk.getInputData().addAll(player.getPlayerAuthInputData());
                pk.getPlayerActions().addAll(player.getPlayerAuthInputActions());

                if (player.isSneaking()) {
                    pk.getInputData().add(PlayerAuthInputData.SNEAKING);
                }
                if (player.isSprinting()) {
                    pk.getInputData().add(PlayerAuthInputData.SPRINTING);
                }
                /*if (player.getDiggingStatus() == PlayerActionType.START_BREAK) {
                    pk.getInputData().add(PlayerAuthInputData.PERFORM_BLOCK_ACTIONS);

                    PlayerBlockActionData blockActionData = new PlayerBlockActionData();
                    blockActionData.setAction(PlayerActionType.CONTINUE_BREAK);
                    blockActionData.setBlockPosition(player.getDiggingPosition());
                    blockActionData.setFace(player.getDiggingFace().ordinal());
                    pk.getPlayerActions().add(blockActionData);
                }*/

                player.getBedrockClientSession().sendPacketImmediately(pk);

                player.getPlayerAuthInputData().removeAll(player.getPlayerAuthInputData());
                player.getPlayerAuthInputActions().removeAll(player.getPlayerAuthInputActions());
                player.setPlayerAuthInputItemUseTransaction(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

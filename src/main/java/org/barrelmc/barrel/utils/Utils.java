package org.barrelmc.barrel.utils;

import java.security.SignatureException;
import java.util.List;
import java.util.Base64;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.text.Normalizer;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import com.github.steveice10.mc.classic.protocol.packet.client.ClientExtEntryPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerExtEntryPacket;
import org.barrelmc.barrel.server.ProxyServer;

public class Utils {
    private static final OkHttpClient CLIENT = new OkHttpClient();

    public static byte[] toByteArray(long value) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (int) (value & 0xFFL);
            value >>= 8L;
        }

        return result;
    }

    public static String usernameToSkinData(String username){
        String url = "https://cdn.classicube.net/skin/"+username+".png";
        Request request = new Request.Builder()
            .url(url)
            .build();
        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                if (body != null) {
                    byte[] imageBytes = body.bytes();
                    BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
                    int targetWidth = 64;
                    int targetHeight = 64;
                    byte[] rgba = new byte[targetWidth * targetHeight * 4];
                    int width = image.getWidth();
                    int height = image.getHeight();
                    int[] pixels = new int[width * height];
                    image.getRGB(0, 0, width, height, pixels, 0, width);
                    for(int y = 0; y < height; y++){
                        for(int x = 0; x < width; x++){
                            int p = pixels[y * width + x];
                            int index = (y * targetWidth + x) * 4;
                            rgba[index] = (byte) ((p >> 16) & 0xFF);
                            rgba[index + 1] = (byte) ((p >> 8) & 0xFF);
                            rgba[index + 2] = (byte) (p & 0xFF);
                            rgba[index + 3] = (byte) ((p >> 24) & 0xFF);
                        }
                    }
                    return Base64.getEncoder().encodeToString(rgba);
                }
            }
            return ProxyServer.getInstance().getDefaultSkinData();
        } catch (IOException | IllegalArgumentException e) {
            return ProxyServer.getInstance().getDefaultSkinData();
        }
    }

    public static String sanitizeText(String text){
        if(text == null){
            return "";
        }
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < normalized.length(); i++) {
            char c = normalized.charAt(i);
            if (Character.getType(c) != Character.NON_SPACING_MARK) {
                if (c >= 32 && c <= 126) {
                    sb.append(c);
                } else if (c == 'ñ' || c == 'Ñ') {
                    sb.append(c == 'ñ' ? 'n' : 'N');
                }
            }
        }
        return sb.toString();
    }

    public static int mapCoords(int val, int minOrig, int maxOrig, int minDest, int maxDest){
        if(minOrig == minDest){
            return val;
        }
        return (int) Math.round((double) (minDest + ((double) (val - minOrig)) * (maxDest - minDest) / (maxOrig - minOrig)));
    }
    public static float mapCoords(float val, float minOrig, float maxOrig, float minDest, float maxDest) {
        if(minOrig == minDest){
            return val;
        }
        return minDest + (val - minOrig) * (maxDest - minDest) / (maxOrig - minOrig);
    }
    public static short mapCoords(short val, short minOrig, short maxOrig, short minDest, short maxDest) {
        if(minOrig == minDest){
            return val;
        }
        return (short) (minDest + (val - minOrig) * (maxDest - minDest) / (maxOrig - minOrig));
    }
    public static double mapCoords(double val, double minOrig, double maxOrig, double minDest, double maxDest) {
        if(minOrig == minDest){
            return val;
        }
        return minDest + (val - minOrig) * (maxDest - minDest) / (maxOrig - minOrig);
    }

    public static String lengthCutter(String bedrockName, int length) {
        if (bedrockName == null) {
            return "null";
        }

        if (bedrockName.length() > length) {
            return bedrockName.substring(0, length);
        } else {
            return bedrockName;
        }
    }

    public static boolean equalsExt(ServerExtEntryPacket extS, ClientExtEntryPacket extC){
        return Utils.equalsExtIgnoreVersion(extS, extC) && extS.getVersion() <= extC.getVersion();
    }

    public static boolean equalsExtIgnoreVersion(ServerExtEntryPacket extS, ClientExtEntryPacket extC){
        return extS.getExtName().equals(extC.getExtName());
    }

    public static boolean containsExt(ServerExtEntryPacket extS, List<ClientExtEntryPacket> extsC){
        for(ClientExtEntryPacket extC : extsC){
            if(Utils.equalsExt(extS, extC)){
                return true;
            }
        }
        return false;
    }
    public static boolean containsExt(ClientExtEntryPacket extC, List<ServerExtEntryPacket> extsS){
        for(ServerExtEntryPacket extS : extsS){
            if(Utils.equalsExtIgnoreVersion(extS, extC)){
                return true;
            }
        }
        return false;
    }

    public static ClientExtEntryPacket getExt(ServerExtEntryPacket extS, List<ClientExtEntryPacket> extsC){
        for(ClientExtEntryPacket extC : extsC){
            if(Utils.equalsExtIgnoreVersion(extS, extC)){
                return extC;
            }
        }
        return null;
    }

    public static ServerExtEntryPacket getExt(ClientExtEntryPacket extC, List<ServerExtEntryPacket> extsS){
        for(ServerExtEntryPacket extS : extsS){
            if(Utils.equalsExtIgnoreVersion(extS, extC)){
                return extS;
            }
        }
        return null;
    }

    public static String[] splitStringL(String str, int size){
        return str.split("(?<=\\G.{" + size + "})");
    }

    public static byte[] DERToJOSE(byte[] derSignature, Utils.AlgorithmType algorithmType) throws SignatureException {
        // DER Structure: http://crypto.stackexchange.com/a/1797
        boolean derEncoded = derSignature[0] == 0x30 && derSignature.length != algorithmType.ecNumberSize * 2;
        if (!derEncoded) {
            throw new SignatureException("Invalid DER signature format.");
        }

        final byte[] joseSignature = new byte[algorithmType.ecNumberSize * 2];

        //Skip 0x30
        int offset = 1;
        if (derSignature[1] == (byte) 0x81) {
            //Skip sign
            offset++;
        }

        //Convert to unsigned. Should match DER length - offset
        int encodedLength = derSignature[offset++] & 0xff;
        if (encodedLength != derSignature.length - offset) {
            throw new SignatureException("Invalid DER signature format.");
        }

        //Skip 0x02
        offset++;

        //Obtain R number length (Includes padding) and skip it
        int rLength = derSignature[offset++];
        if (rLength > algorithmType.ecNumberSize + 1) {
            throw new SignatureException("Invalid DER signature format.");
        }
        int rPadding = algorithmType.ecNumberSize - rLength;
        //Retrieve R number
        System.arraycopy(derSignature, offset + Math.max(-rPadding, 0), joseSignature, Math.max(rPadding, 0), rLength + Math.min(rPadding, 0));

        //Skip R number and 0x02
        offset += rLength + 1;

        //Obtain S number length. (Includes padding)
        int sLength = derSignature[offset++];
        if (sLength > algorithmType.ecNumberSize + 1) {
            throw new SignatureException("Invalid DER signature format.");
        }
        int sPadding = algorithmType.ecNumberSize - sLength;
        //Retrieve R number
        System.arraycopy(derSignature, offset + Math.max(-sPadding, 0), joseSignature, algorithmType.ecNumberSize + Math.max(sPadding, 0), sLength + Math.min(sPadding, 0));

        return joseSignature;
    }

    public enum AlgorithmType {
        ECDSA256(32), ECDSA384(48);

        public int ecNumberSize;

        AlgorithmType(int ecNumberSize) {
            this.ecNumberSize = ecNumberSize;
        }
    }
}

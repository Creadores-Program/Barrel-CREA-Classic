package org.barrelmc.barrel.utils.nukkit;

/**
 * All supported formatting values for chat and console.
 */
public enum TextFormat {
    // Colores de Minecraft
    BLACK("\u001B[30m"),
    DARK_BLUE("\u001B[34m"),
    DARK_GREEN("\u001B[32m"),
    DARK_AQUA("\u001B[36m"),
    DARK_RED("\u001B[31m"),
    DARK_PURPLE("\u001B[35m"),
    GOLD("\u001B[33m"),
    GRAY("\u001B[37m"),
    DARK_GRAY("\u001B[90m"),
    BLUE("\u001B[94m"),
    GREEN("\u001B[92m"),
    AQUA("\u001B[96m"),
    RED("\u001B[91m"),
    LIGHT_PURPLE("\u001B[95m"),
    YELLOW("\u001B[93m"),
    WHITE("\u001B[97m"),
    
    // Formatos de texto de Minecraft
    OBFUSCATED(""), // No tiene equivalente directo en la consola
    BOLD("\u001B[1m"),
    STRIKETHROUGH(""), // No tiene equivalente directo en la consola
    UNDERLINE("\u001B[4m"),
    ITALIC("\u001B[3m"),
    RESET("\u001B[0m"),
    
    // Colores de materiales de Minecraft (estos son ejemplos y pueden no ser precisos)
    MATERIAL_QUARTZ("\u001B[97m"),
    MATERIAL_IRON("\u001B[37m"),
    MATERIAL_NETHERITE("\u001B[90m"),
    MATERIAL_REDSTONE("\u001B[91m"),
    MATERIAL_COPPER("\u001B[33m"),
    MATERIAL_GOLD("\u001B[93m"),
    MATERIAL_EMERALD("\u001B[92m"),
    MATERIAL_DIAMOND("\u001B[96m"),
    MATERIAL_LAPIS("\u001B[94m"),
    MATERIAL_AMETHYST("\u001B[95m");

    private final String ansiCode;

    public static final char ESCAPE = '\u00A7';
    public static final char ESCAPE_CLASSIC_SERVER = '&';
    public static final char ESCAPE_CLASSIC_CLIENT = '%';
    public static final String MATERIAL_QUARTZ_MC = ESCAPE + "h";
    public static final String WHITE_CC = ESCAPE_CLASSIC_SERVER + "f";
    public static final String MATERIAL_IRON_MC = ESCAPE + "i";
    public static final String MATERIAL_NETHERITE_MC = ESCAPE + "j";
    public static final String GRAY_CC = ESCAPE_CLASSIC_SERVER + "7";
    public static final String MATERIAL_REDSTONE_MC = ESCAPE + "m";
    public static final String DARK_RED_CC = ESCAPE_CLASSIC_SERVER + "4";
    public static final String MATERIAL_COPPER_MC = ESCAPE + "n";
    public static final String GOLD_CC = ESCAPE_CLASSIC_SERVER + "6";
    public static final String MATERIAL_GOLD_MC = ESCAPE + "p";
    public static final String YELLOW_CC = ESCAPE_CLASSIC_SERVER + "e";
    public static final String MATERIAL_EMERALD_MC = ESCAPE + "q";
    public static final String DARK_GREEN_CC = ESCAPE_CLASSIC_SERVER + "2";
    public static final String MATERIAL_DIAMOND_MC = ESCAPE + "s";
    public static final String AQUA_CC = ESCAPE_CLASSIC_SERVER + "b";
    public static final String MATERIAL_LAPIS_MC = ESCAPE + "t";
    public static final String BLUE_CC = ESCAPE_CLASSIC_SERVER + "9";
    public static final String MATERIAL_AMETHYST_MC = ESCAPE + "u";
    public static final String LIGHT_PURPLE_CC = ESCAPE_CLASSIC_SERVER + "d";
    public static final String MATERIAL_RESIN_MC = ESCAPE + "v";
    public static final String MINECOIN_GOLD = ESCAPE + "g";
    public static final String GREEN_CC = ESCAPE_CLASSIC_SERVER + "a";
    public static final String VOID_STR = "";
    public static final String COMMAND_CLIENT = "/";
    public static final String COMMAND_SERVER = "?";
    private static final String colorsMc = "0123456789AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVv";

    TextFormat(String ansiCode) {
        this.ansiCode = ansiCode;
    }

    public String getAnsiCode() {
        return ansiCode;
    }
    public static String colorizeMc(char altFormatChar, String textToTranslate) {
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            int x = i + 1;
            if (b[i] == altFormatChar && colorsMc.indexOf(b[x]) > -1) {
                b[i] = TextFormat.ESCAPE;
                b[x] = Character.toLowerCase(b[x]);
            }
        }
        return new String(b);
    }
    public static String colorizeToCc(String textToTranslate){
        char[] b = textToTranslate
            .replace(MATERIAL_QUARTZ_MC, WHITE_CC)
            .replace(MATERIAL_IRON_MC, WHITE_CC)
            .replace(MATERIAL_NETHERITE_MC, GRAY_CC)
            .replace(MATERIAL_REDSTONE_MC, DARK_RED_CC)
            .replace(MATERIAL_COPPER_MC, GOLD_CC)
            .replace(MATERIAL_GOLD_MC, YELLOW_CC)
            .replace(MATERIAL_EMERALD_MC, DARK_GREEN_CC)
            .replace(MATERIAL_DIAMOND_MC, AQUA_CC)
            .replace(MATERIAL_LAPIS_MC, BLUE_CC)
            .replace(MATERIAL_AMETHYST_MC, LIGHT_PURPLE_CC)
            .replace(MATERIAL_RESIN_MC, GOLD_CC)
            .replace(MINECOIN_GOLD, YELLOW_CC)
            .toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            int x = i + 1;
            if (b[i] == ESCAPE && colorsMc.indexOf(b[x]) > -1) {
                b[i] = TextFormat.ESCAPE_CLASSIC_SERVER;
                b[x] = Character.toLowerCase(b[x]);
            }
        }
        return new String(b);
    }
    public static String colorizeToMc(String textToTranslate){
        return colorizeMc(ESCAPE_CLASSIC_SERVER, colorizeMc(ESCAPE_CLASSIC_CLIENT, textToTranslate));
    }
}

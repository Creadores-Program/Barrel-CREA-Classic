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
    public static final char MATERIAL_QUARTZ_MC = 'h';
    public static final char WHITE_CC = 'f';
    public static final char MATERIAL_IRON_MC = 'i';
    public static final char MATERIAL_NETHERITE_MC = 'j';
    public static final char GRAY_CC = '7';
    public static final char MATERIAL_REDSTONE_MC = 'm';
    public static final char DARK_RED_CC = '4';
    public static final char MATERIAL_COPPER_MC = 'n';
    public static final char GOLD_CC = '6';
    public static final char MATERIAL_GOLD_MC = 'p';
    public static final char YELLOW_CC = 'e';
    public static final char MATERIAL_EMERALD_MC = 'q';
    public static final char DARK_GREEN_CC = '2';
    public static final char MATERIAL_DIAMOND_MC = 's';
    public static final char AQUA_CC = 'b';
    public static final char MATERIAL_LAPIS_MC = 't';
    public static final char BLUE_CC = '9';
    public static final char MATERIAL_AMETHYST_MC = 'u';
    public static final char LIGHT_PURPLE_CC = 'd';
    public static final char MATERIAL_RESIN_MC = 'v';
    public static final char MINECOIN_GOLD = 'g';
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
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            int x = i + 1;
            if (b[i] == ESCAPE && colorsMc.indexOf(b[x]) > -1) {
                b[i] = TextFormat.ESCAPE_CLASSIC_SERVER;
                char color = Character.toLowerCase(b[x]);
                switch(color){
                    case MATERIAL_QUARTZ_MC:
                    case MATERIAL_IRON_MC:
                        color = WHITE_CC;
                        break;
                    case MATERIAL_NETHERITE_MC:
                        color = GRAY_CC;
                        break;
                    case MATERIAL_REDSTONE_MC:
                        color = DARK_RED_CC;
                        break;
                    case MATERIAL_COPPER_MC:
                    case MATERIAL_RESIN_MC:
                        color = GOLD_CC;
                        break;
                    case MATERIAL_GOLD_MC:
                    case MINECOIN_GOLD:
                        color = YELLOW_CC;
                        break;
                    case MATERIAL_EMERALD_MC:
                        color = DARK_GREEN_CC;
                        break;
                    case MATERIAL_DIAMOND_MC:
                        color = AQUA_CC;
                        break;
                    case MATERIAL_LAPIS_MC:
                        color = BLUE_CC;
                        break;
                    case MATERIAL_AMETHYST_MC:
                        color = LIGHT_PURPLE_CC;
                        break;
                    default:
                        break;
                }
                b[x] = color;
            }
        }
        return new String(b);
    }
    public static String colorizeToMc(String textToTranslate){
        return colorizeMc(ESCAPE_CLASSIC_SERVER, colorizeMc(ESCAPE_CLASSIC_CLIENT, textToTranslate));
    }
}

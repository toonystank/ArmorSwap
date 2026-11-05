package com.toonystank.armorswap.enums;

import com.toonystank.armorswap.ArmorSwap;
import com.toonystank.armorswap.utils.ConfigData;

public enum ConfigDataType {

    ARMOR_STAND_SWAP("Armor_stand_swap", true),
    ARMOR_STAND_SWAP_PERMISSION("Armor_Stand_Swap_Permission"),
    ITEM_FRAME_SWAP("Item_frame_swap", true),
    ITEM_FRAME_SWAP_PERMISSION("Item_Frame_Swap_Permission"),
    MAIN_HAND_SWAP("Main_hand_swap", true),
    MAIN_HAND_SWAP_PERMISSION("Main_Hand_Swap_Permission"),
    SOUND("Sound"),
    ENABLE("Enable"),
    DISABLE("Disable"),
    WRONG_USAGE("usageError"),
    BYPASS_PERMISSION("Bypass_permission"),
    BYPASS_ENCHANTMENT("Bypass_enchantment", true),
    VERSION("Version"),
    PREFIX("Prefix");

    private final String name;
    private final boolean booleanType;
    private String cachedString;
    private boolean cachedBoolean;

    ConfigDataType(String name) {
        this(name, false);
    }

    ConfigDataType(String name, boolean booleanType) {
        this.name = name;
        this.booleanType = booleanType;
    }

    public static void refreshCache() {
        ArmorSwap plugin = ArmorSwap.getPlugin();
        if (plugin == null || plugin.getConfigData() == null) {
            return;
        }
        ConfigData configData = plugin.getConfigData();
        for (ConfigDataType type : values()) {
            if (type.booleanType) {
                type.cachedBoolean = configData.getBoolean(type.name);
            } else {
                type.cachedString = configData.getMessage(type.name);
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getString() {
        if (cachedString == null) {
            ArmorSwap plugin = ArmorSwap.getPlugin();
            if (plugin != null && plugin.getConfigData() != null) {
                cachedString = plugin.getConfigData().getMessage(name);
            }
        }
        return cachedString != null ? cachedString : "";
    }

    public boolean getBoolean() {
        return booleanType && cachedBoolean;
    }
}

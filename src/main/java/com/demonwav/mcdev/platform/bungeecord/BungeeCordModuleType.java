package com.demonwav.mcdev.platform.bungeecord;

import com.demonwav.mcdev.asset.PlatformAssets;
import com.demonwav.mcdev.platform.AbstractModuleType;
import com.demonwav.mcdev.platform.PlatformType;
import com.demonwav.mcdev.util.CommonColors;

import com.google.common.collect.ImmutableList;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.swing.Icon;

public class BungeeCordModuleType extends AbstractModuleType<BungeeCordModule> {

    private static final String ID = "BUNGEECORD_MODULE_TYPE";
    private static final BungeeCordModuleType instance = new BungeeCordModuleType();

    private BungeeCordModuleType() {
        super("net.md-5", "bungeecord-api");
        CommonColors.applyStandardColors(this.colorMap, "net.md_5.bungee.api.ChatColor");
    }

    @NotNull
    public static BungeeCordModuleType getInstance() {
        return instance;
    }

    @Override
    public PlatformType getPlatformType() {
        return PlatformType.BUNGEECORD;
    }

    @Override
    public Icon getIcon() {
        return PlatformAssets.BUNGEECORD_ICON;
    }

    @Override
    public String getId() {
        return ID;
    }

    @NotNull
    @Override
    public List<String> getIgnoredAnnotations() {
        return ImmutableList.of("net.md_5.bungee.event.EventHandler");
    }

    @NotNull
    @Override
    public List<String> getListenerAnnotations() {
        return ImmutableList.of("net.md_5.bungee.event.EventHandler");
    }

    @NotNull
    @Override
    public BungeeCordModule generateModule(Module module) {
        return new BungeeCordModule(module);
    }
}

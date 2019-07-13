package de.exceptionflug.mccommons.inventories.spigot.utils;

import de.exceptionflug.mccommons.core.utils.ProtocolVersions;

public class ServerVersionProvider {

    private int protocolVersion = -1;

    public int getProtocolVersion() {
        if(protocolVersion == -1) {
            String version = ReflectionUtil.getVersion();
            if(version.startsWith("v1_12_R1")) {
                protocolVersion = ProtocolVersions.MINECRAFT_1_12;
            } else {
                throw new UnsupportedOperationException("Version "+version+" not supported yet.");
            }
        }
        return protocolVersion;
    }
}

package de.exceptionflug.mccommons.inventories.spigot.utils;

public class ServerVersionProvider {

    private int protocolVersion = -1;

    public int getProtocolVersion() {
        if(protocolVersion == -1) {
            String version = ReflectionUtil.getVersion();
            if(version.startsWith("v1_8")) {
                protocolVersion = 47;
            } else {
                throw new UnsupportedOperationException("Version "+version+" not supported yet.");
            }
        }
        return protocolVersion;
    }
}

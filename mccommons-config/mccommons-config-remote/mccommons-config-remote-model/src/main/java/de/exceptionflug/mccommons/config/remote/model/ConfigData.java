package de.exceptionflug.mccommons.config.remote.model;

public class ConfigData {

    private String configData, remotePath;
    private long lastUpdate;

    public long getLastUpdate() {
        return lastUpdate;
    }

    public String getConfigData() {
        return configData;
    }

    public void setConfigData(String configData) {
        this.configData = configData;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }
}

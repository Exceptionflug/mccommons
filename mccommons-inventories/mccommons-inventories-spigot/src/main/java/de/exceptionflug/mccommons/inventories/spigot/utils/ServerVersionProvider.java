package de.exceptionflug.mccommons.inventories.spigot.utils;

import de.exceptionflug.mccommons.core.utils.ProtocolVersions;

public class ServerVersionProvider {

	private int protocolVersion = -1;

	public int getProtocolVersion() {
		if (this.protocolVersion == -1) {
			String version = ReflectionUtil.getVersion();
			if (version.startsWith("v1_8")) {
				this.protocolVersion = ProtocolVersions.MINECRAFT_1_8;
			} else if (version.startsWith("v1_9_R1")) {
				this.protocolVersion = ProtocolVersions.MINECRAFT_1_9;
			} else if (version.startsWith("v1_9_R2")) {
				this.protocolVersion = ProtocolVersions.MINECRAFT_1_9_4;
			} else if (version.startsWith("v1_10_R1")) {
				this.protocolVersion = ProtocolVersions.MINECRAFT_1_10;
			} else if (version.startsWith("v1_11_R1")) {
				this.protocolVersion = ProtocolVersions.MINECRAFT_1_11;
			} else if (version.startsWith("v1_12_R1")) {
				this.protocolVersion = ProtocolVersions.MINECRAFT_1_12;
			} else if (version.startsWith("v1_13_R1")) {
				this.protocolVersion = ProtocolVersions.MINECRAFT_1_13;
			} else if (version.startsWith("v1_13_R2")) {
				this.protocolVersion = ProtocolVersions.MINECRAFT_1_13_1;
			} else if (version.startsWith("v1_14_R1")) {
				this.protocolVersion = ProtocolVersions.MINECRAFT_1_14;
			} else if (version.startsWith("v1_15_R1")) {
				this.protocolVersion = ProtocolVersions.MINECRAFT_1_15;
			} else {
				throw new UnsupportedOperationException("Version " + version + " not supported yet.");
			}
		}
		return protocolVersion;
	}
}

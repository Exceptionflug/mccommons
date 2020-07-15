package de.exceptionflug.mccommons.config.shared;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConfigItemStack {

	private String type, displayName, skull;
	private List<String> lore = new ArrayList<>();
	private List<String> enchantments = new ArrayList<>();
	private List<String> flags = new ArrayList<>();
	private int amount;
	private short durability;

	public String getType() {
		return type;
	}

	public ConfigItemStack setType(String type) {
		this.type = type;
		return this;
	}

	public String getDisplayName() {
		return displayName;
	}

	public ConfigItemStack setDisplayName(String displayName) {
		this.displayName = displayName;
		return this;
	}

	public String getSkull() {
		return skull;
	}

	public ConfigItemStack setSkull(String skull) {
		this.skull = skull;
		return this;
	}

	public List<String> getLore() {
		return lore;
	}

	public ConfigItemStack setLore(List<String> lore) {
		this.lore = lore;
		return this;
	}

	public List<String> getEnchantments() {
		return enchantments;
	}

	public ConfigItemStack setEnchantments(List<String> enchantments) {
		this.enchantments = enchantments;
		return this;
	}

	public List<String> getFlags() {
		return flags;
	}

	public ConfigItemStack setFlags(List<String> flags) {
		this.flags = flags;
		return this;
	}

	public int getAmount() {
		return amount;
	}

	public ConfigItemStack setAmount(int amount) {
		this.amount = amount;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ConfigItemStack that = (ConfigItemStack) o;
		return amount == that.amount &&
			Objects.equals(type, that.type) &&
			Objects.equals(displayName, that.displayName) &&
			Objects.equals(skull, that.skull) &&
			Objects.equals(lore, that.lore) &&
			Objects.equals(enchantments, that.enchantments) &&
			Objects.equals(flags, that.flags);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, displayName, skull, lore, enchantments, flags, amount);
	}

	@Override
	public String toString() {
		return "ConfigItemStack{" +
			"type='" + type + '\'' +
			", displayName='" + displayName + '\'' +
			", skull='" + skull + '\'' +
			", lore=" + lore +
			", enchantments=" + enchantments +
			", flags=" + flags +
			", amount=" + amount +
			'}';
	}

	public short getDurability() {
		return durability;
	}

	public void setDurability(short durability) {
		this.durability = durability;
	}
}

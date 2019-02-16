package de.exceptionflug.mccommons.config.spigot;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundData {

    private Sound sound;
    private float volume;
    private float pitch;

    public SoundData(final Sound sound, final float volume, final float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public Sound getSound() {
        return sound;
    }

    public void setSound(final Sound sound) {
        this.sound = sound;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(final float volume) {
        this.volume = volume;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(final float pitch) {
        this.pitch = pitch;
    }

    public void play(final Player p) {
        p.playSound(p.getLocation(), sound, volume, pitch);
    }

}

package com.wolfeiii.emeraldplinko.game.options;

import com.wolfeiii.emeraldplinko.EmeraldPlinko;
import com.wolfeiii.emeraldplinko.game.PlinkoGame;
import com.wolfeiii.emeraldplinko.utils.DoubleInteger;
import com.wolfeiii.emeraldplinko.utils.StringUtils;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class SideOption implements Cloneable {

    private PlinkoGame plinkoGame;

    private final String title;
    private final List<String> description;
    private final SideOptionType type;
    private final int distance;

    private double yawDifference;
    private double yawRange;

    private int sideOptionIndex;

    public SideOption(SideOptionType type, String titleRaw, List<String> descriptionRaw, int distance, int sideOptionIndex) {
        this.type = type;
        this.title = StringUtils.translate(titleRaw);

        // Colorize every String in list.
        this.description = descriptionRaw.stream()
                .map(StringUtils::translate)
                .collect(Collectors.toList());

        this.sideOptionIndex = sideOptionIndex;
        this.distance = distance;
    }

    public SideOption setYawOptions(double yawDifference, double yawRange) {
        this.yawDifference = yawDifference;
        this.yawRange = yawRange;
        return this;
    }


    public Runnable onClick(Player player, PlinkoGame plinkoGame) {
        switch (type) {
            case QUIT -> {
                return () -> {
                    if (!EmeraldPlinko.getInstance().getGameHandler().isPlayerInGame(player.getUniqueId())) {
                        return;
                    }

                    EmeraldPlinko.getInstance().getGameHandler().clearGame(player.getUniqueId());
                    player.teleport(new Location(Bukkit.getWorlds().get(0), 0, 0, 0));
                };
            }
            case OPTIONS -> {
                return () -> {
                    if (!EmeraldPlinko.getInstance().getGameHandler().isPlayerInGame(player.getUniqueId())) {
                        return;
                    }

                    plinkoGame.getSettings().openSettings(player);
                };
            }
        }

        return null;
    }

    @Override
    public SideOption clone() {
        try {
            return (SideOption) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public enum SideOptionType {
        QUIT,
        OPTIONS
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SideOption that = (SideOption) o;

        if (distance != that.distance) return false;
        if (Double.compare(that.yawDifference, yawDifference) != 0) return false;
        if (Double.compare(that.yawRange, yawRange) != 0) return false;
        if (!Objects.equals(title, that.title)) return false;
        if (!Objects.equals(description, that.description)) return false;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = title != null ? title.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + distance;
        temp = Double.doubleToLongBits(yawDifference);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(yawRange);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}

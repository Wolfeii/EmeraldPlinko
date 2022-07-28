package com.wolfeiii.emeraldplinko.game.objects;

import com.wolfeiii.emeraldplinko.game.options.SideOption;
import com.wolfeiii.emeraldplinko.game.options.SideOptionLoader;
import com.wolfeiii.emeraldplinko.game.options.objects.ActiveSideOption;
import com.wolfeiii.emeraldplinko.map.MapAxis;
import com.wolfeiii.emeraldplinko.utils.Cuboid;
import com.wolfeiii.emeraldplinko.utils.DoubleInteger;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class PlinkoMap {

    private final Location playerLocation;
    private final Cuboid mapCuboid;
    private final MapAxis axis;

    private final Map<Location, ActiveSideOption> sideOptions = new HashMap<>();

    public PlinkoMap(Location spawnLocation, Cuboid mapCuboid, MapAxis axis) {
        this.playerLocation = spawnLocation;
        this.mapCuboid = mapCuboid;
        this.axis = axis;
    }

    public void addSideOption(ActiveSideOption sideOption, Location location) {
        sideOptions.put(location, sideOption);
    }

    public List<ActiveSideOption> getSideOptions() {
        return new ArrayList<>(sideOptions.values());
    }

}

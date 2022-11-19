package me.pafias.ender.game;

import org.bukkit.Location;

public class TriggerPlace {

    private final Location minLoc;
    private final Location maxLoc;
    private final Location centerLoc;

    public TriggerPlace(Location min, Location max, Location center) {
        this.minLoc = min;
        this.maxLoc = max;
        this.centerLoc = center;
    }

    public TriggerPlace(Location min, Location max) {
        this.minLoc = min;
        this.maxLoc = max;
        centerLoc = new Location(minLoc.getWorld(), (minLoc.getX() + maxLoc.getX()) / 2, (minLoc.getY() + maxLoc.getY()) / 2, (minLoc.getZ() + maxLoc.getZ()) / 2);
    }

    public boolean isInside(Location loc) {
        return loc.getX() > minLoc.getX() && loc.getY() > minLoc.getY() && loc.getZ() > minLoc.getZ()
                && loc.getX() < maxLoc.getX() && loc.getY() < maxLoc.getY() && loc.getZ() < maxLoc.getZ();
    }

    public Location getMinLoc() {
        return minLoc;
    }

    public Location getMaxLoc() {
        return maxLoc;
    }

    public Location getSourceLocation() {
        return centerLoc;
    }

}

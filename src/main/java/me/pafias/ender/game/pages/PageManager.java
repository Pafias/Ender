package me.pafias.ender.game.pages;

import me.pafias.ender.Ender;
import me.pafias.ender.game.Game;
import me.pafias.ender.util.RandomUtils;

import java.util.HashSet;
import java.util.Set;

public class PageManager {

    private final Ender plugin;
    private final Game game;

    public PageManager(Ender plugin, Game game) {
        this.plugin = plugin;
        this.game = game;
        // TODO everything still :sob:
    }

    private Set<Page> pages = new HashSet<>();

    public Set<Page> getPages() {
        return pages;
    }

    public Page getRandomPage() {
        return RandomUtils.getRandom(pages);
    }

    public int getPagesFound() {
        return 1;
    }

    public int getTotalPages() {
        return 1;
    }

}

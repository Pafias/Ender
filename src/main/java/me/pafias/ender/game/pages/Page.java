package me.pafias.ender.game.pages;

import org.bukkit.entity.ItemFrame;
import org.bukkit.map.MapView;

import java.io.File;

public class Page {

    private File file;
    private ItemFrame frame;
    private MapView map;

    public Page(File file){
        this.file = file;
    }

}

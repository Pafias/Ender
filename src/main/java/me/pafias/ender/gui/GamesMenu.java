package me.pafias.ender.gui;

import me.pafias.ender.game.Game;
import me.pafias.ender.game.GameManager;
import me.pafias.ender.game.GameState;
import me.pafias.ender.util.CC;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class GamesMenu extends GuiMenu {

    public GamesMenu(Player player) {
        super(player, CC.t("&c&lSpooky game selection"), 9);
        plugin.getSM().getGameManager().getGames().forEach(game -> {
            int size = game.getPlayers().size();
            GameState state = game.getState();
            ItemStack is = new ItemStack(Material.PUMPKIN, size == 0 ? 1 : size);
            ItemMeta meta = is.getItemMeta();
            meta.setDisplayName(CC.tf("&6Game ID: &7&o%s", game.getUUID().toString()));
            meta.setLore(Arrays.asList("", CC.t("&6Map: &bMansion"), CC.tf("&6Players: &b%d", size), CC.tf("&6State: %s", state.getName()), ""));
            if (state.equals(GameState.LOBBY))
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
            is.setItemMeta(meta);
            getInventory().addItem(is);
        });
    }

    @Override
    public void clickHandler(ItemStack item, int slot) {
        GameManager manager = plugin.getSM().getGameManager();
        Game game = manager.getGame(item.getItemMeta().getDisplayName());
        if (game == null) {
            setCloseOnClick(false);
            return;
        }
        manager.addPlayer(player, game);
    }

}

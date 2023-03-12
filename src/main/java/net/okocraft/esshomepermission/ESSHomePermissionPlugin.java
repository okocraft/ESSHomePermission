package net.okocraft.esshomepermission;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.I18n;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.StringUtil;
import java.util.List;
import java.util.Set;
import net.ess3.api.ISettings;
import net.ess3.api.events.UserTeleportHomeEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ESSHomePermissionPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    private void onUserTeleportHome(UserTeleportHomeEvent event) {
        if (event.getHomeType() != UserTeleportHomeEvent.HomeType.HOME) {
            return;
        }
        if (!(event.getUser() instanceof User user)) {
            return;
        }

        List<String> homes = user.getHomes();
        int index = homes.indexOf(event.getHomeName());
        int availableIndex = getAvailableHomeLimit(user) - 1;
        if (index <= availableIndex) {
            return;
        }

        event.setCancelled(true);

        for (int i = 0; i < homes.size(); i++) {
            if (i > availableIndex) {
                homes.set(i, ChatColor.STRIKETHROUGH + homes.get(i));
            }
        }
        user.sendMessage(I18n.tl("homes", StringUtil.joinList(homes), homes.size(), availableIndex + 1));
    }

    private int getAvailableHomeLimit(final User user) {
        ISettings settings = getPlugin(Essentials.class).getSettings();
        int limit = 1;
        if (user.isAuthorized("essentials.home.multiple")) {
            limit = settings.getHomeLimit("default");
        }

        @SuppressWarnings("unchecked")
        Set<String> homeList = settings.getMultipleHomes();
        if (homeList != null) {
            for (String set : homeList) {
                if (user.isAuthorized("essentials.home.multiple." + set) && limit < settings.getHomeLimit(set)) {
                    limit = settings.getHomeLimit(set);
                }
            }
        }

        return limit;
    }
}

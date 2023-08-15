package minealex.tviewtps;

import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public class TpsCommandExecutor implements org.bukkit.command.CommandExecutor {
    private final TViewTPS plugin;

    public TpsCommandExecutor(TViewTPS plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("tps")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (player.hasPermission(plugin.getConfig().getString("viewtps-permission", "tviewtps.viewtps"))) {
                    double tps = plugin.getCalculatedTps();
                    String tpsMessage = plugin.getTpsMessage();
                    player.sendMessage(String.format(tpsMessage, tps));
                } else {
                    player.sendMessage(plugin.getNoPermissionMessage());
                }
            } else {
                sender.sendMessage(plugin.getPlayerOnlyMessage());
            }
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("viewtps") && args.length > 0 && args[0].equalsIgnoreCase("bossbar")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (player.hasPermission(plugin.getConfig().getString("viewtps-permission", "tviewtps.viewtps"))) {
                    BossBar bossBar = plugin.getBossBar();
                    if (bossBar != null) {
                        bossBar.addPlayer(player);
                        player.sendMessage(plugin.getBossBarShownMessage());
                    } else {
                        player.sendMessage(plugin.getBossBarNotConfiguredMessage());
                    }
                } else {
                    player.sendMessage(plugin.getNoPermissionMessage());
                }
            } else {
                sender.sendMessage(plugin.getPlayerOnlyMessage());
            }
            return true;
        }

        return false;
    }
}

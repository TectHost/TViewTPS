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

        if (cmd.getName().equalsIgnoreCase("viewtps")) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("bossbar")) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (player.hasPermission("viewtps.bossbar")) {
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
                } else if (args[0].equalsIgnoreCase("version")) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (player.hasPermission("viewtps.version")) {
                            String pluginVersion = plugin.getDescription().getVersion();
                            String versionMessage = plugin.getConfig().getString("version-message", "&aPlugin version: %s");
                            player.sendMessage(String.format(versionMessage, pluginVersion));
                        } else {
                            player.sendMessage(plugin.getNoPermissionMessage());
                        }
                    } else {
                        sender.sendMessage(plugin.getPlayerOnlyMessage());
                    }
                    return true;
                }
            }
        }

        return false;
    }
}

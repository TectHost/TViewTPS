package minealex.tviewtps;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public class TViewTPS extends JavaPlugin {

    private BossBar bossBar;
    private long lastUpdateTime;
    private double tps;
    private String noPermissionMessage;
    private String playerOnlyMessage;
    private String bossBarShownMessage;
    private String bossBarNotConfiguredMessage;
    private String tpsMessage;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();

        noPermissionMessage = getConfig().getString("messages.no-permission", "&cNo tienes permiso para ejecutar este comando.");
        playerOnlyMessage = getConfig().getString("messages.player-only", "&cEste comando solo puede ser ejecutado por un jugador.");
        bossBarShownMessage = getConfig().getString("messages.bossbar-shown", "&aBossBar mostrado con los TPS en tiempo real.");
        bossBarNotConfiguredMessage = getConfig().getString("messages.bossbar-not-configured", "&cLa BossBar no está configurada correctamente.");
        tpsMessage = getConfig().getString("messages.tps-message", "&aTPS del servidor: %.2f");

        String tpsColorString = getConfig().getString("tps-color", "RED").toUpperCase();
        BarColor tpsColor;
        try {
            tpsColor = BarColor.valueOf(tpsColorString);
        } catch (IllegalArgumentException e) {
            getLogger().warning("Color inválido en tps-color en el archivo config.yml. Usando color por defecto RED.");
            tpsColor = BarColor.RED;
        }

        bossBar = Bukkit.createBossBar("", tpsColor,
                BarStyle.valueOf(getConfig().getString("tps-style", "SOLID")));

        for (Player player : Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(player);
        }

        lastUpdateTime = System.currentTimeMillis();

        TpsCommandExecutor tpsCommandExecutor = new TpsCommandExecutor(this);
        getCommand("tps").setExecutor(tpsCommandExecutor);
        getCommand("viewtps").setExecutor(tpsCommandExecutor);

        getServer().getScheduler().scheduleSyncRepeatingTask(this, this::updateTps, 0, 20);
    }

    @Override
    public void onDisable() {
        bossBar.removeAll();
    }

    private void updateTps() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastUpdateTime;
        lastUpdateTime = currentTime;

        double tps = 20.0 / (elapsedTime / 1000.0);
        this.tps = tps;

        String tpsText = getConfig().getString("tps-text", "&aTPS: %.2f");
        tpsText = tpsText.replace("%tps%", String.format("%.2f", tps));
        tpsText = translateColorCodes(tpsText);

        bossBar.setTitle(tpsText);
    }

    public double getCalculatedTps() {
        return tps;
    }

    private String translateColorCodes(String message) {
        return message.replace("&", "\u00A7");
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public String getNoPermissionMessage() {
        return translateColorCodes(noPermissionMessage);
    }

    public String getPlayerOnlyMessage() {
        return translateColorCodes(playerOnlyMessage);
    }

    public String getBossBarShownMessage() {
        return translateColorCodes(bossBarShownMessage);
    }

    public String getBossBarNotConfiguredMessage() {
        return translateColorCodes(bossBarNotConfiguredMessage);
    }

    public String getTpsMessage() {
        return translateColorCodes(tpsMessage);
    }
}

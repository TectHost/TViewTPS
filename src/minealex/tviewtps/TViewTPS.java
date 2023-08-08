package minealex.tviewtps;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TViewTPS extends JavaPlugin {

    private BossBar bossBar;
    private long lastUpdateTime;
    @SuppressWarnings("unused")
	private int ticks;
    private double tps;

    @Override
    public void onEnable() {
        // Cargar la configuración desde config.yml o crearla si no existe
        saveDefaultConfig();
        reloadConfig();

        // Convertir el valor del color en minúsculas a mayúsculas para evitar errores
        String tpsColorString = getConfig().getString("tps-color", "RED").toUpperCase();

        // Verificar si el valor del color es válido, de lo contrario, usar color por defecto "RED"
        BarColor tpsColor;
        try {
            tpsColor = BarColor.valueOf(tpsColorString);
        } catch (IllegalArgumentException e) {
            getLogger().warning("Color inválido en tps-color en el archivo config.yml. Usando color por defecto RED.");
            tpsColor = BarColor.RED;
        }

        // Inicializar el BossBar
        bossBar = Bukkit.createBossBar("", tpsColor,
                BarStyle.valueOf(getConfig().getString("tps-style", "SOLID")));

        // Añadir el BossBar a todos los jugadores en línea
        for (Player player : Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(player);
        }

        lastUpdateTime = System.currentTimeMillis();
        ticks = 0;

        // Programar la actualización del BossBar cada 20 ticks (1 segundo)
        getServer().getScheduler().scheduleSyncRepeatingTask(this, this::updateBossBar, 0, 20);
    }

    @Override
    public void onDisable() {
        // Quitar el BossBar al deshabilitar el plugin
        bossBar.removeAll();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Verificar si el comando es "/viewtps bossbar"
        if (cmd.getName().equalsIgnoreCase("viewtps") && args.length > 0 && args[0].equalsIgnoreCase("bossbar")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                // Verificar si el jugador tiene el permiso tviewtps.bossbar
                if (player.hasPermission("tviewtps.bossbar")) {
                    // Añadir el BossBar al jugador que ejecutó el comando
                    bossBar.addPlayer(player);

                    // Obtener el mensaje personalizado desde el archivo de configuración y traducir los colores
                    String bossbarMessage = getConfig().getString("bossbar-message", "&aBossBar mostrado con los TPS en tiempo real.");
                    bossbarMessage = bossbarMessage.replace("%tps%", String.format("%.2f", tps));
                    bossbarMessage = translateColorCodes(bossbarMessage);
                    player.sendMessage(bossbarMessage);
                } else {
                    // Obtener el mensaje personalizado desde el archivo de configuración y traducir los colores
                    String noPermissionMessage = getConfig().getString("no-permission-message", "&cNo tienes permiso para ejecutar este comando.");
                    noPermissionMessage = translateColorCodes(noPermissionMessage);
                    player.sendMessage(noPermissionMessage);
                }
            } else {
                // Obtener el mensaje personalizado desde el archivo de configuración y traducir los colores
                String playerOnlyCommandMessage = getConfig().getString("player-only-command", "&cEste comando solo puede ser ejecutado por un jugador.");
                playerOnlyCommandMessage = translateColorCodes(playerOnlyCommandMessage);
                sender.sendMessage(playerOnlyCommandMessage);
            }
            return true;
        }
        return false;
    }

    private void updateBossBar() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastUpdateTime;
        lastUpdateTime = currentTime;

        // Calcular el TPS asumiendo que hay 20 ticks por segundo
        double tps = 20.0 / (elapsedTime / 1000.0);
        this.tps = tps;

        // Obtener el texto del TPS desde el archivo de configuración
        String tpsText = getConfig().getString("tps-text", "&aTPS: %.2f"); // Por defecto, color verde
        tpsText = tpsText.replace("%tps%", String.format("%.2f", tps));

        // Traducir las secuencias de escape "&" en colores
        tpsText = translateColorCodes(tpsText);

        // Actualizar el BossBar con el nuevo valor de TPS y estilo
        bossBar.setTitle(tpsText);
        bossBar.setColor(bossBar.getColor());
        bossBar.setStyle(BarStyle.valueOf(getConfig().getString("tps-style", "SOLID")));
    }

    private String translateColorCodes(String message) {
        return message.replace("&", "\u00A7");
    }
}

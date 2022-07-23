package ga.windpvp.windspigot.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

@SuppressWarnings("unused")
public class WindSpigotWorldConfig {

    private final String worldName;
    private final YamlConfiguration config;
    private boolean verbose;

    public WindSpigotWorldConfig(String worldName) {
        this.worldName = worldName;
        this.config = WindSpigotConfig.config;
        init();
    }

    public void init() {
        this.verbose = getBoolean("verbose", false);

        log("-------- World Settings For [\" + worldName + \"] --------");
        WindSpigotConfig.readConfig(WindSpigotWorldConfig.class, this);
    }

    private void log(String s) {
        if (verbose) {
            Bukkit.getLogger().info(s);
        }
    }

    private void set(String path, Object val) {
        config.set("world-settings.default." + path, val);
    }

    private void setComment(String path, String comment) {
        WindSpigotConfig.setComment("world-settings.default." + path, comment);
    }

    private boolean getBoolean(String path, boolean def) {
        config.addDefault("world-settings.default." + path, def);
        return config.getBoolean("world-settings." + worldName + "." + path,
                config.getBoolean("world-settings.default." + path));
    }

    private double getDouble(String path, double def) {
        config.addDefault("world-settings.default." + path, def);
        return config.getDouble("world-settings." + worldName + "." + path,
                config.getDouble("world-settings.default." + path));
    }

    private int getInt(String path, int def) {
        config.addDefault("world-settings.default." + path, def);
        return config.getInt("world-settings." + worldName + "." + path,
                config.getInt("world-settings.default." + path));
    }

    private float getFloat(String path, float def) {
        config.addDefault("world-settings.default." + path, def);
        return config.getFloat("world-settings." + worldName + "." + path,
                config.getFloat("world-settings.default." + path));
    }

    private <T> List getList(String path, T def) {
        config.addDefault("world-settings.default." + path, def);
        return config.getList("world-settings." + worldName + "." + path,
                config.getList("world-settings.default." + path));
    }

    private String getString(String path, String def) {
        config.addDefault("world-settings.default." + path, def);
        return config.getString("world-settings." + worldName + "." + path,
                config.getString("world-settings.default." + path));
    }

    public boolean disableSpongeAbsorption;

    private void disableSpongeAbsorption() {
        disableSpongeAbsorption = getBoolean("disable-sponge-absorption", false);
        setComment("disable-sponge-absorption", "Disables sponge absorption");
    }

    public boolean doChunkUnload;

    private void doChunkUnload() {
        doChunkUnload = getBoolean("unload-chunks", true);
        setComment("unload-chunks", "Enable unloading chunks");
    }

    public boolean doBlocksOperations;

    private void doBlocksOperations() {
        doBlocksOperations = getBoolean("block-operations", true);
        setComment("block-operations", "Enable block operations");
    }

    public boolean disablePhysicsPlace;
    public boolean disablePhysicsUpdate;

    private void physics() {
        disablePhysicsPlace = getBoolean("physics.disable-place", false);
        setComment("physics.disable-place", "Disables physics place");
        disablePhysicsUpdate = getBoolean("settings.physics.disable-update", false);
        setComment("physics.disable-update", "Disables physics update");
    }

    public boolean enableLavaToCobblestone;

    private void setEnableLavaToCobblestone() {
        enableLavaToCobblestone = getBoolean("enable-lava-to-cobblestone", true);
        setComment("enable-lava-to-cobblestone", "Enables lava converting to cobblestone.");
    }

    public boolean enableMobAI;
    public boolean enableMobSound;
    public boolean enableEntityActivation;
    public boolean endermiteSpawning;

    private void entity() {
        enableMobAI = getBoolean("entity.mob-ai", true);
        setComment("entity.mob-ai", "Enables mob AI");
        enableMobSound = getBoolean("entity.mob-sound", true);
        setComment("entity.mob-sound", "Enables mob sound");
        enableEntityActivation = getBoolean("entity.entity-activation", true);
        setComment("entity.entity-activation", "Enables active ticks for entities");
        endermiteSpawning = getBoolean("entity.endermite-spawning", true);
        setComment("entity.endermite-spawning", "Enables endermite spawning.");
    }

    public boolean infiniteWaterSources;

    private void infiniteWaterSources() {
        infiniteWaterSources = getBoolean("infinite-water-sources", true);
        setComment("infinite-water-sources", "Enables infinite water sources");
    }

    public boolean constantExplosions;
    public boolean reducedDensityRays;

    private void explosions() {
        constantExplosions = getBoolean("explosions.constant-radius", false);
        setComment("explosions.constant-explosions", "Changes the radius of explosions to be constant.");
        reducedDensityRays = getBoolean("explosions.reduced-density-rays", true);
        setComment("explosions.reduced-density-rays",
                "Toggles whether the server should use reduced rays when calculating density");
    }

    public boolean shouldTickEnchantmentTables;

    private void shouldTickEnchantmentTables() {
        shouldTickEnchantmentTables = getBoolean("tick-enchantment-tables", true);
        setComment("tick-enchantment-tables", "Toggles whether enchantment tables should be ticked");
    }
}

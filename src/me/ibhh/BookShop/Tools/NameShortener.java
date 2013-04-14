package me.ibhh.BookShop.Tools;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class NameShortener
{
    /**
     * This Class was written by Brokkonaut, so many thanks to him.
     */
    private final JavaPlugin plugin;

    private final File databaseFile;

    private final YamlConfiguration namesConfig;

    private final HashMap<String, String> realToShortNames;

    private final HashMap<String, String> shortToRealNames;

    public final static int NAME_LENGTH_MAX = 15;

    /**
     * Erzeugt einen neuen Namenskürzer, der die Namen in der Datei <b>shortnames.yaml</b> im Pluginverzeichnis verwendet. Achtung: Es darf nur eine Instanz pro Plugin hiervon erstellt werden.
     * @param plugin das Plugin, das diesen Namenskürzer verwendet
     */
    public NameShortener(JavaPlugin plugin)
    {
        this(plugin, new File(plugin.getDataFolder(), "shortnames.yaml"));
    }

    /**
     * Erzeugt einen neuen Namenskürzer. Achtung: Es darf nur eine Instanz pro Plugin hiervon erstellt werden.
     * @param plugin das Plugin, das diesen Namenskürzer verwendet
     * @param databaseFile eine Datenbankdatei mit den Kurznamen
     */
    public NameShortener(JavaPlugin plugin, File databaseFile)
    {
        if (plugin.getServer() == null)
        {
            throw new IllegalArgumentException("plugin has no server");
        }
        this.plugin = plugin;
        this.databaseFile = databaseFile;
        realToShortNames = new HashMap<String, String>();
        shortToRealNames = new HashMap<String, String>();
        namesConfig = new YamlConfiguration();

        //namen laden wenn möglich
        if (databaseFile.exists())
        {
            try
            {
                namesConfig.load(databaseFile);
            }
            catch (Exception e)
            {
                plugin.getLogger().log(Level.SEVERE, "Could not load short names database " + databaseFile, e);
            }

            for (String realName : namesConfig.getKeys(false))
            {
                String shortName = namesConfig.getString(realName);
                realToShortNames.put(realName.toLowerCase(), shortName);
                shortToRealNames.put(shortName.toLowerCase(), realName);
            }
        }
    }

    /**
     * Gibt für einen echten Minecraftnamen einen Namen zurück, der auf ein Schild passt. Bei neuen angepassten Namen wird der Name in die Datenbank eingetragen
     * @param realName der echte Name des Spielers. Darf nicht null sein.
     * @return der Name, der auf dem Schild erscheinen soll. Ist niemals null.
     */
    public synchronized String getShortName(String realName)
    {
        realName = realName.trim();
        //prüfen, ob bereits ein shortname existiert
        String shortName = realToShortNames.get(realName.toLowerCase());
        if (shortName != null)
        {
            return shortName;
        }
        //ansonsten prüfen, ob der name zu lang ist, oder ob es einen gleichen shortnamen gibt (dann muss er angepasst werden)
        if (realName.length() <= NAME_LENGTH_MAX && !shortToRealNames.containsKey(realName.toLowerCase()))
        {
            return realName;
        }
        //der name ist ungültig, also müssen wir einen passenden namen suchen
        int counter = 0;
        while (true)
        {
            String countString = counter == 0 ? "" : Integer.toString(counter);
            shortName = realName.substring(0, NAME_LENGTH_MAX - countString.length()) + countString;
            String shortNameLower = shortName.toLowerCase();
            //namen von vorhandenen spielern sind immer ungültig
            //namen, die schon kurznamen sind, sind auch ungültig
            if (plugin.getServer().getOfflinePlayer(shortName).hasPlayedBefore() || realToShortNames.containsKey(shortNameLower) || shortToRealNames.containsKey(shortNameLower))
            {
                counter += 1;
                continue;
            }
            //der name ist gültig: eintragen und speichern
            realToShortNames.put(realName.toLowerCase(), shortName);
            shortToRealNames.put(shortNameLower, realName);
            namesConfig.set(realName, shortName);
            try
            {
                namesConfig.save(databaseFile);
            }
            catch (Exception e)
            {
                plugin.getLogger().log(Level.SEVERE, "Could not save short names database " + databaseFile, e);
            }
            return shortName;
        }
    }

    /**
     * Gibt für einen Namen auf einem Schild den echten Namen des SPielers zurück.
     * 
     * @param shortName der Name auf dem Schild. Darf nicht null sein.
     * @return der echte Name. Ist niemals null.
     */
    public synchronized String getRealName(String shortName)
    {
        shortName = shortName.trim();
        String realName = shortToRealNames.get(shortName.toLowerCase());
        if (realName != null)
        {
            return realName;
        }
        //wenn kein mapping existiert, ist das ein echter name
        return shortName;
    }
}

package me.ibhh.BookShop;

import net.minecraft.server.v1_4_5.EntityPlayer;
import net.minecraft.server.v1_4_5.ItemInWorldManager;
import net.minecraft.server.v1_4_5.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_4_5.CraftServer;
import org.bukkit.entity.Player;


public class Tools
{
	public static boolean isInteger(String input)
	{
		try
		{
			Integer.parseInt(input);
			return true;
		} catch (Exception e) {
		}
		return false;
	}
        public static boolean isFloat(String input)
	{
		try
		{
			Float.parseFloat(input);
			return true;
		} catch (Exception e) {
		}
		return false;
	}
	public static String[] stringtoArray( String s, String sep ) {
		// convert a String s to an Array, the elements
		// are delimited by sep
		// NOTE : for old JDK only (<1.4).
		//        for JDK 1.4 +, use String.split() instead
		StringBuffer buf = new StringBuffer(s);
		int arraysize = 1;
		for ( int i = 0; i < buf.length(); i++ ) {
			if ( sep.indexOf(buf.charAt(i) ) != -1 )
				arraysize++;
		}
		String [] elements  = new String [arraysize];
		int y,z = 0;
		if ( buf.toString().indexOf(sep) != -1 ) {
			while (  buf.length() > 0 ) {
				if ( buf.toString().indexOf(sep) != -1 ) {
					y =  buf.toString().indexOf(sep);
					if ( y != buf.toString().lastIndexOf(sep) ) {
						elements[z] = buf.toString().substring(0, y ); 
						z++;
						buf.delete(0, y + 1);
					}
					else if ( buf.toString().lastIndexOf(sep) == y ) {
						elements[z] = buf.toString().substring
								(0, buf.toString().indexOf(sep));
						z++;
						buf.delete(0, buf.toString().indexOf(sep) + 1);
						elements[z] = buf.toString();z++;
						buf.delete(0, buf.length() );
					}
				}
			}
		}
		else {
			elements[0] = buf.toString(); 
		}
		buf = null;
		return elements;
	}
        
        public static Player getmyOfflinePlayer(BookShop plugin, String[] args, int index) {
        String playername = args[index];
        plugin.Logger("Empfaenger: " + playername, "Debug");
        Player player = plugin.getServer().getPlayerExact(playername);
        try {
            if (player == null) {
                player = plugin.getServer().getPlayer(playername);
            }
            if (player == null) {
                for (OfflinePlayer p : Bukkit.getServer().getOfflinePlayers()) {
                    OfflinePlayer offp = p;
                    if (offp.getName().toLowerCase().equals(playername.toLowerCase())) {
                        plugin.Logger("Player has same name: " + offp.getName(), "Debug");
                        if (offp != null) {
                            if (offp.hasPlayedBefore()) {
                                player = (Player) offp.getPlayer();
                                plugin.Logger("Player has Played before: " + offp.getName(), "Debug");
                            }
                            break;
                        }
                    }
                }
            }
            if (player == null) {
                MinecraftServer server = ((CraftServer) plugin.getServer()).getServer();
                EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), args[index], new ItemInWorldManager(server.getWorldServer(0)));
                player = entity == null ? null : (Player) entity.getBukkitEntity();
                if (player != null) {
                    player.loadData();
                    return player;
                }
            }
            if (player != null) {
                plugin.Logger("Empfaengername after getting Player: " + player.getName(), "Debug");
            }
        } catch (Exception e) {
            e.printStackTrace();
            plugin.Logger("Uncatched Exeption!", "Error");
            plugin.report.report(3312, "Uncatched Exeption on getting offlineplayer", e.getMessage(), "BookShop", e);
            try {
                plugin.metricshandler.Error++;
            } catch (Exception e1) {
            }
        }
        return player;
    }

    public static Player getmyOfflinePlayer(BookShop plugin, String playername) {
        plugin.Logger("Empfaenger: " + playername, "Debug");
        Player player = plugin.getServer().getPlayerExact(playername);
        try {
            if (player == null) {
                player = plugin.getServer().getPlayer(playername);
            }
            if (player == null) {
                for (OfflinePlayer p : Bukkit.getServer().getOfflinePlayers()) {
                    OfflinePlayer offp = p;
                    if (offp.getName().toLowerCase().equals(playername.toLowerCase())) {
                        plugin.Logger("Player has same name: " + offp.getName(), "Debug");
                        if (offp != null) {
                            if (offp.hasPlayedBefore()) {
                                player = (Player) offp.getPlayer();
                                plugin.Logger("Player has Played before: " + offp.getName(), "Debug");
                            }
                            break;
                        }
                    }
                }
            }
            if (player == null) {
                MinecraftServer server = ((CraftServer) plugin.getServer()).getServer();
                EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), playername, new ItemInWorldManager(server.getWorldServer(0)));
                player = entity == null ? null : (Player) entity.getBukkitEntity();
                if (player != null) {
                    player.loadData();
                    return player;
                }
            }
            if (player != null) {
                plugin.Logger("Empfaengername after getting Player: " + player.getName(), "Debug");
            }
        } catch (Exception e) {
            e.printStackTrace();
            plugin.Logger("Uncatched Exeption!", "Error");
            plugin.report.report(3312, "Uncatched Exeption on getting offlineplayer", e.getMessage(), "BookShop", e);
            try {
                plugin.metricshandler.Error++;
            } catch (Exception e1) {
            }
        }
        return player;
    }
}
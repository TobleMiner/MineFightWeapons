package TobleMiner.MineFightWeapons.Config;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import TobleMiner.MineFight.Debug.Debugger;
import TobleMiner.MineFight.Util.IO.File.FileUtil;
import TobleMiner.MineFightWeapons.Main;

public abstract class ConfWorld 
{
	private final HashMap<World, YamlConfiguration> configs = new HashMap<>();
	
	public ConfWorld(File confdir, boolean forceReset)
	{
		for(World w : Bukkit.getServer().getWorlds())
		{
			File worlddir = new File(confdir, w.getName());
			if(!worlddir.exists())
				worlddir.mkdirs();
			File confFile = new File(worlddir, this.getFilename());
			if(!confFile.exists())
			{
				Debugger.writeDebugOut(String.format("Config '%s' doesn't exist. Creating...", confFile.getAbsoluteFile()));
				this.initConfig(confFile);
			}
			YamlConfiguration worldConf = new YamlConfiguration();
			try 
			{
				worldConf.load(confFile);
				if(worldConf.getBoolean("reset", true) || forceReset)
				{
					confFile.delete();
					this.initConfig(confFile);
					worldConf.load(confFile);
				}
				configs.put(w, worldConf);
			}
			catch(Exception ex)
			{
				if(ex instanceof InvalidConfigurationException)
				{
					Main.logger.log(Level.SEVERE, String.format("Oops, looks like the configuration '%s' contains errors:", confFile.getAbsolutePath()));
					ex.printStackTrace();
				}
			}
		}
	}
	
	private void initConfig(File confFile)
	{
		InputStream is = this.getClass().getResourceAsStream(this.getFilename());
		try
		{
			FileUtil.copyFromInputStreamToFileUtf8(confFile, is);
		}
		catch(Exception ex)
		{
			Main.logger.log(Level.SEVERE, "Failed writing default config files. Make sure that the server has write permissions for the plugin folder.");
		}
	}
	
	protected YamlConfiguration getConfig(World w)
	{
		YamlConfiguration conf = this.configs.get(w);
		if(conf == null)
			Main.logger.log(Level.SEVERE, String.format("Unknown world: '%s'", w.getName()));
		return conf;
	}
	
	protected abstract String getFilename();
}

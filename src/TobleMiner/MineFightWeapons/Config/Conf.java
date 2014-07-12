package TobleMiner.MineFightWeapons.Config;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Level;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import TobleMiner.MineFight.Util.IO.File.FileUtil;
import TobleMiner.MineFightWeapons.Main;
import TobleMiner.MineFightWeapons.Config.Weapon.Explosive.ConfC4;
import TobleMiner.MineFightWeapons.Config.Weapon.Explosive.ConfClaymore;
import TobleMiner.MineFightWeapons.Config.Weapon.Explosive.ConfFrag;
import TobleMiner.MineFightWeapons.Config.Weapon.Explosive.Missile.ConfRPG;
import TobleMiner.MineFightWeapons.Config.Weapon.Stationary.ConfTurret;

public class Conf
{
	
	private YamlConfiguration conf;
	public ConfClaymore claymore;
	public ConfC4 c4;
	public ConfTurret turret;
	public ConfFrag frag;
	public ConfRPG rpg;
	
	public Conf(File plugindir)
	{
		File confdir = new File(plugindir, "config");
		File confFile = new File(plugindir, "config.conf");
		if(!confFile.exists())
		{
			this.initConfig(confFile);
		}
		this.conf= new YamlConfiguration();
		try 
		{
			this.conf.load(confFile);
			if(this.conf.getBoolean("reset", true))
			{
				confFile.delete();
				this.initConfig(confFile);
				this.conf.load(confFile);
			}
			boolean resetAll = this.resetAll();
			if(resetAll)
			{
				this.conf.set("resetAll", false);
				this.conf.save(confFile);
			}
			this.claymore = new ConfClaymore(confdir, resetAll);
			this.c4 = new ConfC4(confdir, resetAll);
			this.turret = new ConfTurret(confdir, resetAll);
			this.frag = new ConfFrag(confdir, resetAll);
			this.rpg = new ConfRPG(confdir, resetAll);
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
	
	private boolean resetAll()
	{
		return this.conf.getBoolean("resetAll", false);
	}
	
	private void initConfig(File confFile)
	{
		InputStream is = this.getClass().getResourceAsStream("conf.conf");
		try
		{
			FileUtil.copyFromInputStreamToFileUtf8(confFile, is);
		}
		catch(Exception ex)
		{
			Main.logger.log(Level.SEVERE, "Failed writing default config file. Make sure that the server has write permissions for the plugin folder.");
		}
	}
	
	public String getLang()
	{
		return this.conf.getString("langfile", "EN_us.lang");
	}
}

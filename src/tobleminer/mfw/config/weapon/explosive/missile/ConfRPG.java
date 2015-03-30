package tobleminer.mfw.config.weapon.explosive.missile;

import java.io.File;

import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import tobleminer.mfw.config.weapon.explosive.ConfExplosive;

public class ConfRPG extends ConfExplosive
{
	public ConfRPG(File confdir, boolean forceReset) 
	{
		super(confdir, forceReset);
	}

	@Override
	protected String getFilename() 
	{
		return "rpg.conf";
	}
	
	public double getMaxSpeed(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if(conf != null)
		{
			return conf.getDouble("missile.maxSpeed", 3d);
		}
		return 3d;
	}

	public double getAcceleration(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if(conf != null)
		{
			return conf.getDouble("missile.acceleration", 3d);
		}
		return 3d;
	}

	public double getMaxLifetime(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if(conf != null)
		{
			return conf.getDouble("missile.maxLifetime", 30d);
		}
		return 30d;
	}
	
	public double getThrottle(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if(conf != null)
		{
			return conf.getDouble("missile.throttle", 0.01d);
		}
		return 0.01d;
	}

	public boolean despawnOnDeath(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if(conf != null)
		{
			return conf.getBoolean("despawnOnDeath", false);
		}
		return false;
	}

	public boolean ignoreProtection(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if(conf != null)
		{
			return conf.getBoolean("ignoreProtection", false);
		}
		return false;
	}
}

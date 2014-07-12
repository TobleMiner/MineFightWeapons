package TobleMiner.MineFightWeapons.Config.Weapon.Explosive;

import java.io.File;

import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfFrag extends ConfExplosive
{
	public ConfFrag(File confdir, boolean forceReset) 
	{
		super(confdir, forceReset);
	}

	@Override
	protected String getFilename() 
	{
		return "frag.conf";
	}
	
	public double getThrowSpeed(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if(conf != null)
		{
			return conf.getDouble("throw.speed", 1.5d);
		}
		return 1.5d;
	}

	public double getThrowSpeedSneak(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if(conf != null)
		{
			return conf.getDouble("throw.sneak", 0.75d);
		}
		return 0.75d;
	}

	public double getThrowSpeedSprint(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if(conf != null)
		{
			return conf.getDouble("throw.run", 3d);
		}
		return 3d;
	}
	
	public double getFuseTime(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if(conf != null)
		{
			return conf.getDouble("fuse", 3d);
		}
		return 3d;
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

package TobleMiner.MineFightWeapons.Config.Weapon.Explosive;

import java.io.File;

import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfClaymore extends ConfExplosive
{

	public ConfClaymore(File confdir, boolean forceReset) 
	{
		super(confdir, forceReset);
	}

	@Override
	protected String getFilename() 
	{
		return "claymore.conf";
	}
	
	public boolean canOwnerPickup(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if(conf != null)
		{
			return conf.getBoolean("canOwnerPickup", true);
		}
		return true;
	}
	
	public boolean canEnemyPickup(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if(conf != null)
		{
			return conf.getBoolean("canEnemyPickup", false);
		}
		return false;
	}
	
	public int getLimit(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if(conf != null)
		{
			return conf.getInt("claymorelimit", 4);
		}
		return 4;
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

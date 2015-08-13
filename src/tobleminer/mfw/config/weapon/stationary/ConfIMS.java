package tobleminer.mfw.config.weapon.stationary;

import java.io.File;

import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import tobleminer.mfw.config.weapon.ConfWeapon;

public class ConfIMS extends ConfWeapon
{
	public ConfIMS(File confdir, boolean forceReset)
	{
		super(confdir, forceReset);
	}

	@Override
	protected String getFilename()
	{
		return "ims.conf";
	}

	public double getTriggerDistance(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if (conf != null)
		{
			return conf.getDouble("triggerdist", 12d);
		}
		return 12d;
	}

	public int getGrenadeAmount(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if (conf != null)
		{
			return conf.getInt("grenade.amount", 3);
		}
		return 3;
	}

	public double getGrenadeSpeed(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if (conf != null)
		{
			return conf.getDouble("grenade.speed", 2d);
		}
		return 2d;
	}

	public float getGrenadeBlastpower(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if (conf != null)
		{
			return (float) conf.getDouble("grenade.blastpower", 4);
		}
		return 4f;
	}

	public int getLimit(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if (conf != null)
		{
			return conf.getInt("imslimit", 1);
		}
		return 1;
	}

	public boolean despawnOnDeath(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if (conf != null)
		{
			return conf.getBoolean("despawnOnDeath", false);
		}
		return false;
	}

	public boolean ignoreProtection(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if (conf != null)
		{
			return conf.getBoolean("ignoreProtection", false);
		}
		return false;
	}

	public double getMaxGrenadeLifetime(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if (conf != null)
		{
			return conf.getDouble("grenade.maxLifetime", 30d);
		}
		return 30d;
	}

	public double getGrenadeExploDist(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if (conf != null)
		{
			return conf.getDouble("grenade.exploDist", 2d);
		}
		return 2d;
	}

	public double getArmTime(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if (conf != null)
		{
			return conf.getDouble("armTime", 10d);
		}
		return 10d;
	}

	public boolean canEnemyPickup(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if (conf != null)
		{
			return conf.getBoolean("canEnemyPickup", false);
		}
		return false;
	}
}

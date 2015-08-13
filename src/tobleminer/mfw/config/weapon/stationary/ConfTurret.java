package tobleminer.mfw.config.weapon.stationary;

import java.io.File;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import tobleminer.mfw.config.weapon.ConfWeapon;

public class ConfTurret extends ConfWeapon
{
	public ConfTurret(File confdir, boolean forceReset)
	{
		super(confdir, forceReset);
	}

	@Override
	protected String getFilename()
	{
		return "turret.conf";
	}

	public Material getControllerMaterial(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if (conf != null)
		{
			String matname = conf.getString("controller", "invalid");
			Material mat = Material.getMaterial(matname);
			if (mat != null)
				return mat;

		}
		return Material.WOOD_SWORD;
	}

	public int getControllerSubId(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if (conf != null)
		{
			return conf.getInt("controllerSubId", 0);
		}
		return 0;
	}

	public Material getAmmoMaterial(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if (conf != null)
		{
			String matname = conf.getString("ammo.material", "invalid");
			Material mat = Material.getMaterial(matname);
			if (mat != null)
				return mat;

		}
		return Material.ARROW;
	}

	public int getAmmoSubId(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if (conf != null)
		{
			return conf.getInt("ammo.subId", 0);
		}
		return 0;
	}

	public double getAmmoSpeed(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if (conf != null)
		{
			return conf.getDouble("ammo.speed", 4.5d);
		}
		return 4.5d;
	}

	public Material getMissileMaterial(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if (conf != null)
		{
			String matname = conf.getString("missile.material", "invalid");
			Material mat = Material.getMaterial(matname);
			if (mat != null)
				return mat;

		}
		return Material.SULPHUR;
	}

	public int getMissileSubId(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if (conf != null)
		{
			return conf.getInt("missile.subId", 0);
		}
		return 0;
	}

	public double getMissileSpeed(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if (conf != null)
		{
			return conf.getDouble("missile.speed", 2d);
		}
		return 2d;
	}

	public float getMissileBlastpower(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if (conf != null)
		{
			return (float) conf.getDouble("missile.blastpower", 6);
		}
		return 6f;
	}

	public int getLimit(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if (conf != null)
		{
			return conf.getInt("turretlimit", 1);
		}
		return 1;
	}

	public boolean despawnOnDeath(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if (conf != null)
		{
			return conf.getBoolean("despawnOnDeath", true);
		}
		return true;
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
}

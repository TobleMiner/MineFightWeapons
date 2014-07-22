package TobleMiner.MineFightWeapons.Config.Weapon.Explosive;

import java.io.File;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfC4 extends ConfExplosive
{
	public ConfC4(File confdir, boolean forceReset) 
	{
		super(confdir, forceReset);
	}

	@Override
	protected String getFilename() 
	{
		return "c4.conf";
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
	
	public Material getDetonatorMaterial(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if(conf != null)
		{
			String matname = conf.getString("detonator", "invalid");
			Material mat = Material.getMaterial(matname);
			if(mat != null)
				return mat;
			
		}
		return Material.DIAMOND;
	}
	
	public int getDetonatorSubId(World w)
	{		
		YamlConfiguration conf = this.getConfig(w);
		if(conf != null)
		{
			return conf.getInt("detonatorSubId", 0);
		}
		return 0;
	}
	
	public boolean canEnemyPickup(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if(conf != null)
		{
			return conf.getBoolean("canEnemyPickup", true);
		}
		return true;
	}
	
	public int getLimit(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if(conf != null)
		{
			return conf.getInt("c4limit", 4);
		}
		return 4;
	}
	
	public boolean despawnOnDeath(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if(conf != null)
		{
			return conf.getBoolean("despawnOnDeath", true);
		}
		return true;
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

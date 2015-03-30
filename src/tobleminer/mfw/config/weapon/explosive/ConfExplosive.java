package tobleminer.mfw.config.weapon.explosive;

import java.io.File;

import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import tobleminer.mfw.config.weapon.ConfWeapon;

public abstract class ConfExplosive extends ConfWeapon
{

	public ConfExplosive(File confdir, boolean forceReset) 
	{
		super(confdir, forceReset);
	}
	
	public float getBlastPower(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if(conf != null)
		{
			return (float)conf.getDouble("blastpower");
		}
		return 0;
	}
}

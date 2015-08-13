package tobleminer.mfw.config.weapon;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import tobleminer.mfw.Main;
import tobleminer.mfw.config.ConfWorld;

public abstract class ConfWeapon extends ConfWorld
{
	public ConfWeapon(File confdir, boolean forceReset)
	{
		super(confdir, forceReset);
	}

	public Material getMaterial(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if (conf != null)
		{
			Material mat = Material.getMaterial(conf.getString("material"));
			if (mat != null)
				return mat;
			else
				Main.logger.log(Level.SEVERE,
						String.format("The configuration '%s' for world '%s' specifies an unknown material.",
								this.getFilename(), w.getName()));
		}
		return Material.BEDROCK;
	}

	public short getSubid(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if (conf != null)
		{
			return (short) conf.getInt("subid");
		}
		return 0;
	}

	public boolean isEnabled(World w)
	{
		YamlConfiguration conf = this.getConfig(w);
		if (conf != null)
		{
			return conf.getBoolean("enabled", true);
		}
		return false;
	}
}

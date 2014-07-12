package TobleMiner.MineFightWeapons;

import java.util.logging.Level;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import TobleMiner.MineFight.API.MineFightAPI;
import TobleMiner.MineFight.ErrorHandling.Logger;
import TobleMiner.MineFightWeapons.Config.Conf;
import TobleMiner.MineFightWeapons.Language.Langfile;
import TobleMiner.MineFightWeapons.Weapons.Definitions.C4;
import TobleMiner.MineFightWeapons.Weapons.Definitions.Claymore;
import TobleMiner.MineFightWeapons.Weapons.Definitions.Frag;
import TobleMiner.MineFightWeapons.Weapons.Definitions.Turret;

public class Main extends JavaPlugin
{
	public static MineFightAPI api;
	public static Logger logger;
	public static Conf config;
		
	@Override
	public void onEnable()
	{
		api = MineFightAPI.instance;
		logger = api.getLogger(this);
		logger.log(Level.INFO, "MineFightWeapons enabled");
		config = new Conf(this.getDataFolder());
		Langfile lf = new Langfile(this.getDataFolder());
		api.addTranslations(lf.getLangMisc());
		api.addTranslations(lf.getLangWeapons());
		Claymore clay = new Claymore();
		C4 c4 = new C4();
		Turret turret = new Turret();
		Frag frag = new Frag();
		for(World w : api.getKnownWorlds())
		{
			api.registerWeapon(clay, w);
			api.registerWeapon(c4, w);
			api.registerWeapon(turret, w);
			api.registerWeapon(frag, w);
		}
	}
	
	@Override
	public void onDisable()
	{
		logger.log(Level.INFO, "MineFightWeapons disabled");
	}

}

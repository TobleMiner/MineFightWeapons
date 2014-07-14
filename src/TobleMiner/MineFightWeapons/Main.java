package TobleMiner.MineFightWeapons;

import java.util.logging.Level;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import TobleMiner.MineFight.API.MineFightWeaponAPI;
import TobleMiner.MineFight.ErrorHandling.Logger;
import TobleMiner.MineFightWeapons.Config.Conf;
import TobleMiner.MineFightWeapons.Language.Langfile;
import TobleMiner.MineFightWeapons.Weapons.Definitions.C4;
import TobleMiner.MineFightWeapons.Weapons.Definitions.Claymore;
import TobleMiner.MineFightWeapons.Weapons.Definitions.Frag;
import TobleMiner.MineFightWeapons.Weapons.Definitions.RPG;
import TobleMiner.MineFightWeapons.Weapons.Definitions.Turret;

public class Main extends JavaPlugin
{
	public static MineFightWeaponAPI api;
	public static Logger logger;
	public static Conf config;
		
	@Override
	public void onEnable()
	{
		api = MineFightWeaponAPI.instance;
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
		RPG rpg = new RPG();
		for(World w : api.getKnownWorlds())
		{
			if(config.claymore.isEnabled(w)) api.registerWeapon(clay, w);
			if(config.c4.isEnabled(w)) api.registerWeapon(c4, w);
			if(config.turret.isEnabled(w)) api.registerWeapon(turret, w);
			if(config.frag.isEnabled(w)) api.registerWeapon(frag, w);
			if(config.rpg.isEnabled(w)) api.registerWeapon(rpg, w);
		}
	}
	
	@Override
	public void onDisable()
	{
		logger.log(Level.INFO, "MineFightWeapons disabled");
	}

}

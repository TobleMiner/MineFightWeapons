package TobleMiner.MineFightWeapons;

import java.util.logging.Level;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import TobleMiner.MineFight.API.MineFightCommandAPI;
import TobleMiner.MineFight.API.MineFightWeaponAPI;
import TobleMiner.MineFight.ErrorHandling.Logger;
import TobleMiner.MineFightWeapons.Commands.ModuleMFW;
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
	public static MineFightCommandAPI cmdapi;
	public static Logger logger;
	public static Conf config;

	private Claymore claymore = new Claymore();
	private C4 c4 = new C4();
	private Turret turret = new Turret();
	private Frag frag = new Frag();
	private RPG rpg = new RPG();
	
	@Override
	public void onEnable()
	{
		cmdapi = MineFightCommandAPI.instance;
		api = MineFightWeaponAPI.instance;
		logger = api.getLogger(this);
		logger.log(Level.INFO, "MineFightWeapons enabled");
		cmdapi.registerCommandModule(new ModuleMFW(this));
		reload();
	}
	
	@Override
	public void onDisable()
	{
		logger.log(Level.INFO, "MineFightWeapons disabled");
	}
	
	public void reload()
	{
		if(config != null)
		{
			for(World w : api.getKnownWorlds())
			{
				if(config.claymore.isEnabled(w)) api.unregisterWeapon(claymore, w);
				if(config.c4.isEnabled(w)) api.unregisterWeapon(c4, w);
				if(config.turret.isEnabled(w)) api.unregisterWeapon(turret, w);
				if(config.frag.isEnabled(w)) api.unregisterWeapon(frag, w);
				if(config.rpg.isEnabled(w)) api.unregisterWeapon(rpg, w);
			}
		}
		config = new Conf(this.getDataFolder());
		Langfile lf = new Langfile(this.getDataFolder());
		api.addTranslations(lf.getLangMisc());
		api.addTranslations(lf.getLangWeapons());
		for(World w : api.getKnownWorlds())
		{
			if(config.claymore.isEnabled(w)) api.registerWeapon(claymore, w);
			if(config.c4.isEnabled(w)) api.registerWeapon(c4, w);
			if(config.turret.isEnabled(w)) api.registerWeapon(turret, w);
			if(config.frag.isEnabled(w)) api.registerWeapon(frag, w);
			if(config.rpg.isEnabled(w)) api.registerWeapon(rpg, w);
		}
	}

}

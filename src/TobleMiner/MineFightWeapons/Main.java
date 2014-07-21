package TobleMiner.MineFightWeapons;

import java.util.logging.Level;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import TobleMiner.MineFight.API.MineFightAPI;
import TobleMiner.MineFight.API.MineFightCommandAPI;
import TobleMiner.MineFight.API.MineFightLangAPI;
import TobleMiner.MineFight.API.MineFightProtectionAPI;
import TobleMiner.MineFight.API.MineFightWeaponAPI;
import TobleMiner.MineFight.API.MineFightWorldAPI;
import TobleMiner.MineFight.ErrorHandling.Logger;
import TobleMiner.MineFightWeapons.Commands.ModuleMFW;
import TobleMiner.MineFightWeapons.Config.Conf;
import TobleMiner.MineFightWeapons.Language.Langfile;
import TobleMiner.MineFightWeapons.Weapons.Definitions.C4;
import TobleMiner.MineFightWeapons.Weapons.Definitions.Claymore;
import TobleMiner.MineFightWeapons.Weapons.Definitions.Frag;
import TobleMiner.MineFightWeapons.Weapons.Definitions.IMS;
import TobleMiner.MineFightWeapons.Weapons.Definitions.RPG;
import TobleMiner.MineFightWeapons.Weapons.Definitions.Turret;

public class Main extends JavaPlugin
{
	public static MineFightWeaponAPI wpapi;
	public static MineFightCommandAPI cmdapi;
	public static MineFightAPI mfapi;
	public static MineFightWorldAPI worldapi;
	public static MineFightLangAPI langapi;
	public static MineFightProtectionAPI papi;
	public static Logger logger;
	public static Conf config;

	private Claymore claymore = new Claymore();
	private C4 c4 = new C4();
	private Turret turret = new Turret();
	private Frag frag = new Frag();
	private RPG rpg = new RPG();
	private IMS ims = new IMS();
	
	@Override
	public void onEnable()
	{
		cmdapi = MineFightCommandAPI.instance;
		wpapi = MineFightWeaponAPI.instance;
		mfapi = MineFightAPI.instance;
		langapi = MineFightLangAPI.instance;
		worldapi = MineFightWorldAPI.instance;
		papi = MineFightProtectionAPI.instance;
		logger = mfapi.getLogger(this);
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
			for(World w : worldapi.getKnownWorlds())
			{
				if(config.claymore.isEnabled(w)) wpapi.unregisterWeapon(claymore, w);
				if(config.c4.isEnabled(w)) wpapi.unregisterWeapon(c4, w);
				if(config.turret.isEnabled(w)) wpapi.unregisterWeapon(turret, w);
				if(config.frag.isEnabled(w)) wpapi.unregisterWeapon(frag, w);
				if(config.rpg.isEnabled(w)) wpapi.unregisterWeapon(rpg, w);
				if(config.ims.isEnabled(w)) wpapi.unregisterWeapon(ims, w);
			}
		}
		config = new Conf(this.getDataFolder());
		Langfile lf = new Langfile(this.getDataFolder());
		langapi.addTranslations(lf.getLangMisc());
		langapi.addTranslations(lf.getLangWeapons());
		for(World w : worldapi.getKnownWorlds())
		{
			if(config.claymore.isEnabled(w)) wpapi.registerWeapon(claymore, w);
			if(config.c4.isEnabled(w)) wpapi.registerWeapon(c4, w);
			if(config.turret.isEnabled(w)) wpapi.registerWeapon(turret, w);
			if(config.frag.isEnabled(w)) wpapi.registerWeapon(frag, w);
			if(config.rpg.isEnabled(w)) wpapi.registerWeapon(rpg, w);
			if(config.ims.isEnabled(w)) wpapi.registerWeapon(ims, w);
		}
	}

}

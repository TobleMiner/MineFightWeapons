package tobleminer.mfw;

import java.util.logging.Level;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import tobleminer.mfw.command.ModuleMFW;
import tobleminer.mfw.config.Conf;
import tobleminer.mfw.language.Langfile;
import tobleminer.mfw.weapon.definition.C4;
import tobleminer.mfw.weapon.definition.Claymore;
import tobleminer.mfw.weapon.definition.Frag;
import tobleminer.mfw.weapon.definition.IMS;
import tobleminer.mfw.weapon.definition.RPG;
import tobleminer.mfw.weapon.definition.Turret;
import TobleMiner.MineFight.API.MineFightAPI;
import TobleMiner.MineFight.API.MineFightCommandAPI;
import TobleMiner.MineFight.API.MineFightEventAPI;
import TobleMiner.MineFight.API.MineFightLangAPI;
import TobleMiner.MineFight.API.MineFightProtectionAPI;
import TobleMiner.MineFight.API.MineFightWeaponAPI;
import TobleMiner.MineFight.API.MineFightWorldAPI;
import TobleMiner.MineFight.ErrorHandling.Logger;

public class Main extends JavaPlugin
{
	public static MineFightWeaponAPI wpapi;
	public static MineFightCommandAPI cmdapi;
	public static MineFightAPI mfapi;
	public static MineFightWorldAPI worldapi;
	public static MineFightLangAPI langapi;
	public static MineFightProtectionAPI papi;
	public static MineFightEventAPI evapi;
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
		evapi = MineFightEventAPI.instance;
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
				if(config.claymore.isEnabled(w))
				{
					evapi.unregisterEventListener(claymore, w);
					wpapi.unregisterWeapon(claymore, w);
				}
				if(config.c4.isEnabled(w)) 
				{
					evapi.unregisterEventListener(c4, w);
					wpapi.unregisterWeapon(c4, w);
				}
				if(config.turret.isEnabled(w))
				{
					evapi.unregisterEventListener(turret, w);
					wpapi.unregisterWeapon(turret, w);
				}
				if(config.frag.isEnabled(w))
				{
					evapi.unregisterEventListener(frag, w);
					wpapi.unregisterWeapon(frag, w);
				}
				if(config.rpg.isEnabled(w))
				{
					evapi.unregisterEventListener(rpg, w);
					wpapi.unregisterWeapon(rpg, w);
				}
				if(config.ims.isEnabled(w)) 
				{
					evapi.unregisterEventListener(ims, w);
					wpapi.unregisterWeapon(ims, w);
				}
			}
		}
		config = new Conf(this.getDataFolder());
		Langfile lf = new Langfile(this.getDataFolder());
		langapi.addTranslations(lf.getLangMisc());
		langapi.addTranslations(lf.getLangWeapons());
		for(World w : worldapi.getKnownWorlds())
		{
			if(config.claymore.isEnabled(w))
			{
				evapi.registerEventListener(claymore, w);
				wpapi.registerWeapon(claymore, w);
			}
			if(config.c4.isEnabled(w)) 
			{
				evapi.registerEventListener(c4, w);
				wpapi.registerWeapon(c4, w);
			}
			if(config.turret.isEnabled(w))
			{
				evapi.registerEventListener(turret, w);
				wpapi.registerWeapon(turret, w);
			}
			if(config.frag.isEnabled(w))
			{
				evapi.registerEventListener(frag, w);
				wpapi.registerWeapon(frag, w);
			}
			if(config.rpg.isEnabled(w))
			{
				evapi.registerEventListener(rpg, w);
				wpapi.registerWeapon(rpg, w);
			}
			if(config.ims.isEnabled(w))
			{
				evapi.registerEventListener(ims, w);
				wpapi.registerWeapon(ims, w);
			}
		}
	}

}

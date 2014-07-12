package TobleMiner.MineFightWeapons.Weapons.Explosive;

import org.bukkit.Location;
import org.bukkit.entity.Item;

import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;
import TobleMiner.MineFight.Util.SyncDerp.EntitySyncCalls;
import TobleMiner.MineFightWeapons.Main;

public class WpClaymore 
{
	public final Item claymore;
	public final PVPPlayer owner;
	private final Match match;
	private boolean exploded = false;
	
	public WpClaymore(Item is, PVPPlayer owner, Match match)
	{
		this.claymore = is;
		this.owner = owner;
		this.match = match;
	}
	
	public void explode()
	{
		if(exploded) return;
		this.exploded = true;
		Location loc = this.claymore.getLocation().clone();
		EntitySyncCalls.removeEntity(claymore);
		match.createExplosion(this.owner, loc, Main.config.claymore.getBlastPower(this.match.getWorld()), this.getLocName());
	}
	
	public String getLocName()
	{
		return TobleMiner.MineFight.Main.gameEngine.dict.get("claymore");
	}
}

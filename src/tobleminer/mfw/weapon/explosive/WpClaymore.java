package tobleminer.mfw.weapon.explosive;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

import tobleminer.mfw.Main;
import tobleminer.minefight.engine.match.Match;
import tobleminer.minefight.engine.player.PVPPlayer;
import tobleminer.minefight.util.geometry.Area3D;
import tobleminer.minefight.util.syncderp.EntitySyncCalls;

public class WpClaymore 
{
	public final Item claymore;
	public final PVPPlayer owner;
	private final Match match;
	private boolean exploded = false;
	private final Area3D area;
	
	public WpClaymore(Item is, PVPPlayer owner, Match match)
	{
		this.claymore = is;
		this.owner = owner;
		this.match = match;
		float dist = Main.config.claymore.getBlastPower(this.match.getWorld());
		Vector vec = new Vector(dist, dist, dist);
		this.area = new Area3D(is, vec, vec.clone().multiply(-1d));
		match.registerDangerZone(this.area);
	}
	
	public void explode()
	{
		if(exploded) return;
		this.exploded = true;
		Location loc = this.claymore.getLocation().clone();
		match.createExplosion(this.owner, loc, Main.config.claymore.getBlastPower(this.match.getWorld()), this.getLocName());
	}
	
	public void remove()
	{
		this.match.unregisterDangerZone(this.area);
		EntitySyncCalls.removeEntity(claymore);
	}
	
	public String getLocName()
	{
		return Main.langapi.localize("claymore");
	}
}

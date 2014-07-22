package TobleMiner.MineFightWeapons.Weapons.Stationary.IMS;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

import TobleMiner.MineFight.GameEngine.GameEngine;
import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Match.Spawning.DangerZone;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;
import TobleMiner.MineFight.Util.SyncDerp.EffectSyncCalls;
import TobleMiner.MineFight.Util.SyncDerp.EntitySyncCalls;
import TobleMiner.MineFight.Weapon.TickControlled.TickControlledWeapon;
import TobleMiner.MineFightWeapons.Main;

public class WpIMS extends TickControlledWeapon
{

	public final Item item;
	private final double triggerDist;
	private int projNum;
	public final PVPPlayer owner;
	private int timer = 1;
	private List<PVPPlayer> targeted = new ArrayList<PVPPlayer>();
	private double time = 0d;
	private boolean armed = false;
	private final double armTime;
	private final DangerZone dzone;
	
	public WpIMS(Match match,Item item, PVPPlayer owner)
	{
		super(match);
		this.item = item;
		this.owner = owner;
		World w = match.getWorld();
		this.triggerDist = Main.config.ims.getTriggerDistance(w);
		this.projNum = Main.config.ims.getGrenadeAmount(w);
		this.armTime = Main.config.ims.getArmTime(w);
		Vector vec = new Vector(triggerDist, triggerDist, triggerDist);
		this.dzone = new DangerZone(item.getLocation().clone().add(vec), item.getLocation().clone().add(vec.clone().multiply(-1d)), this.owner.getTeam());
		match.registerDangerZone(dzone);
	}

	@Override
	public void doUpdate() 
	{
		if(timer > GameEngine.tps/10d)
		{
			timer = 0;
			time += 0.1d;
			if(time > this.armTime)
			{
				if(!armed)
				{
					this.owner.thePlayer.sendMessage(ChatColor.GOLD + Main.langapi.localize("imsArm"));
				}
				armed = true;
				List<PVPPlayer> victims = match.getSpawnedPlayersNearLocation(item.getLocation(), triggerDist);
				for(PVPPlayer victim : victims)
				{
					if(owner.getTeam().equals(victim.getTeam()) || this.targeted.contains(victim))
					{
						continue;
					}
					targeted.add(victim);
					Location loc = this.item.getLocation().add(0d,2d,0d);
					Arrow arr = this.item.getWorld().spawnArrow(loc, new Vector(0d,1d,0d),1f,1f);
					new IMSGrenade(arr, this, victim);
					EffectSyncCalls.showEffect(arr.getLocation(), Effect.BLAZE_SHOOT, 1);
					this.projNum--;
					if(projNum <= 0)
					{
						this.remove();
					}
					break;
				}
			}
		}
		timer++;
	}
	
	public PVPPlayer getOwner()
	{
		return this.owner;
	}

	public void release(PVPPlayer target)
	{
		this.targeted.remove(target);
	}

	public void remove() 
	{
		this.unregisterTickControlled();
		EntitySyncCalls.removeEntity(item);
		this.match.unregisterDangerZone(dzone);
	}
	
	public String getLocName()
	{
		return Main.langapi.localize("ims");
	}
}

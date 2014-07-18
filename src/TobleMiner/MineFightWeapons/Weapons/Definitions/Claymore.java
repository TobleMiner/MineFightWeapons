package TobleMiner.MineFightWeapons.Weapons.Definitions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import TobleMiner.MineFight.API.MineFightWeaponAPI;
import TobleMiner.MineFight.Debug.Debugger;
import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;
import TobleMiner.MineFight.Util.SyncDerp.EntitySyncCalls;
import TobleMiner.MineFight.Weapon.Weapon;
import TobleMiner.MineFightWeapons.Main;
import TobleMiner.MineFightWeapons.Weapons.Explosive.WpClaymore;

public class Claymore implements Weapon
{
	private HashMap<Match, List<WpClaymore>> claymorsByMatch = new HashMap<>();
	private HashMap<Item, WpClaymore> claymorsByItem = new HashMap<>();
	private HashMap<PVPPlayer, List<WpClaymore>> claymorsByPlayer = new HashMap<>();
		
	@Override
	public void getRequiredEvents(List<Class<?>> events)
	{
		events.add(PlayerDropItemEvent.class);
		events.add(PlayerPickupItemEvent.class);
		events.add(EntityDamageEvent.class);
		events.add(EntityDamageByBlockEvent.class);
		events.add(EntityDamageByEntityEvent.class);
		events.add(EntityCombustEvent.class);
		events.add(ItemDespawnEvent.class);
	}

	@Override
	public void onEvent(Match m, Event event)
	{
		if(event instanceof PlayerDropItemEvent)
		{
			PlayerDropItemEvent pdie = (PlayerDropItemEvent)event;
			if(pdie.getItemDrop().getItemStack().getType() != this.getMaterial(m.getWorld()) || pdie.getItemDrop().getItemStack().getDurability() != this.getSubId(m.getWorld()))
				return;
			Debugger.writeDebugOut("Clay dropped by " + pdie.getPlayer().getName());
			PVPPlayer player = m.getPlayerExact(pdie.getPlayer());
			if(player == null || !player.isSpawned())
			{
				Debugger.writeDebugOut("Claymore not created: Player not spawned: " + pdie.getPlayer().getName());
				pdie.setCancelled(true);
				return;
			}
			Item item = pdie.getItemDrop();
			if(MineFightWeaponAPI.instance.getProtections().isLocProtected(item.getLocation()) && !Main.config.claymore.ignoreProtection(item.getWorld()))
			{
				Debugger.writeDebugOut("Claymore not created: Area protected: " + pdie.getPlayer().getName());
				pdie.setCancelled(true);
				return;
			}
			Debugger.writeDebugOut("Claymore created: " + pdie.getPlayer().getName());
			WpClaymore clay = new WpClaymore(item, player, m);
			this.claymorsByPlayer.get(player).add(clay);
			this.claymorsByItem.put(item, clay);
			this.claymorsByMatch.get(m).add(clay);
			if(this.claymorsByPlayer.get(player).size() > Main.config.claymore.getLimit(m.getWorld()))
			{
				List<WpClaymore> clays = new ArrayList<>(this.claymorsByPlayer.get(player));
				if(clays.size() > 0)
				{
					WpClaymore tclay = clays.get(0);
					EntitySyncCalls.removeEntity(tclay.claymore);
					this.remove(tclay);
				}
			}
			pdie.setCancelled(false);
		}
		else if(event instanceof PlayerPickupItemEvent)
		{
			PlayerPickupItemEvent ppie = (PlayerPickupItemEvent)event;
			ppie.setCancelled(true);
			if(ppie.getItem().getItemStack().getType() != this.getMaterial(m.getWorld()))
				return;
			PVPPlayer player = m.getPlayerExact(ppie.getPlayer());
			if(player == null || !player.isSpawned())
			{
				Debugger.writeDebugOut(String.format("%s hasn't spawned. No pickup.", ppie.getPlayer().getName()));
				return;
			}
			WpClaymore clay = this.claymorsByItem.get(ppie.getItem());
			if(clay == null)
				return;
			Debugger.writeDebugOut(String.format("%s is trying to pickup a claymore: Owner: %s", ppie.getPlayer().getName(), clay.owner.thePlayer.getName()));
			if(player != clay.owner)
			{
				if(player.getTeam() != clay.owner.getTeam() && Main.config.claymore.canEnemyPickup(m.getWorld()) && player.thePlayer.isSneaking())
				{
					Debugger.writeDebugOut(String.format("%s is picking a hostile claymore.", ppie.getPlayer().getName()));
					this.remove(clay);
					ppie.setCancelled(false);
				}
				else if(m.canKill(clay.owner, player))
				{
					Debugger.writeDebugOut(String.format("%s is near a hostile claymore. That's going to hurt.", ppie.getPlayer().getName()));
					this.remove(clay);
					clay.explode();
				}
			}
			else
			{
				Debugger.writeDebugOut(String.format("%s found his own claymore.", ppie.getPlayer().getName()));
				if(player.thePlayer.isSneaking() && Main.config.claymore.canOwnerPickup(m.getWorld()))
				{
					Debugger.writeDebugOut(String.format("%s is picking up his claymore.", ppie.getPlayer().getName()));
					this.remove(clay);
					ppie.setCancelled(false);
				}
			}
		}
		else if(event instanceof EntityDamageEvent)
		{
			EntityDamageEvent ede = (EntityDamageEvent)event;
			if(ede.getEntity() instanceof Item)
			{
				Item item = (Item)ede.getEntity();
				if(item.getItemStack().getType() != this.getMaterial(m.getWorld()))
					return;
				WpClaymore clay = this.claymorsByItem.get(item);
				if(clay == null)
					return;
				Debugger.writeDebugOut("Claymore damaged");
				ede.setCancelled(true);
				if(ede.getCause() == DamageCause.BLOCK_EXPLOSION || ede.getCause() == DamageCause.ENTITY_EXPLOSION)
				{
					Debugger.writeDebugOut("Claymore damaged by explosion. Exploding.");
					clay.explode();
					this.remove(clay);
					return;
				}
			}
		}
		else if(event instanceof EntityCombustEvent)
		{
			EntityCombustEvent ice = (EntityCombustEvent)event;
			if(ice.getEntity() instanceof Item)
			{
				Item item = (Item)ice.getEntity();
				if(item.getItemStack().getType() != this.getMaterial(m.getWorld()))
					return;
				WpClaymore clay = this.claymorsByItem.get(item);
				if(clay == null)
					return;
				ice.setCancelled(true);
				clay.explode();
				return;
			}
		}
		else if(event instanceof ItemDespawnEvent)
		{
			ItemDespawnEvent ide = (ItemDespawnEvent)event;
			Item item = (Item)ide.getEntity();
			if(item.getItemStack().getType() != this.getMaterial(m.getWorld()))
				return;
			WpClaymore clay = this.claymorsByItem.get(item);
			if(clay == null)
				return;
			ide.setCancelled(true);
		}
	}

	@Override
	public void onKill(Match m, PVPPlayer killer, PVPPlayer killed)
	{
		//nop
	}

	@Override
	public void onDeath(Match m, PVPPlayer killed, PVPPlayer killer) 
	{
		if(Main.config.claymore.despawnOnDeath(m.getWorld()))
		{
			List<WpClaymore> clays = new ArrayList<>(this.claymorsByPlayer.get(killed));
			for(WpClaymore clay : clays)
			{
				this.remove(clay);
				EntitySyncCalls.removeEntity(clay.claymore);
			}
		}
	}

	@Override
	public void onRespawn(Match m, PVPPlayer player) 
	{
		//nop
	}

	@Override
	public void matchCreated(Match m) 
	{
		claymorsByMatch.put(m, new ArrayList<WpClaymore>());
	}

	@Override
	public void matchEnded(Match m) 
	{
		claymorsByMatch.remove(m);
	}

	@Override
	public void onTick()
	{
		//nop
	}

	@Override
	public void onJoin(Match m, PVPPlayer player) 
	{
		Debugger.writeDebugOut("Creating claymore-entry for " + player.thePlayer.getName());
		claymorsByPlayer.put(player, new ArrayList<WpClaymore>());
	}

	@Override
	public void onLeave(Match m, PVPPlayer player) 
	{
		Debugger.writeDebugOut("Removing claymore-entry for " + player.thePlayer.getName());
		List<WpClaymore> clays = new ArrayList<>(this.claymorsByPlayer.get(player));
		for(WpClaymore clay : clays)
		{
			claymorsByItem.remove(clay.claymore);
			claymorsByMatch.get(m).remove(clay);
		}
		claymorsByPlayer.remove(player);
	}

	@Override
	public void onTeamchange(Match m, PVPPlayer player)
	{
		Debugger.writeDebugOut("Removing claymors due to teamchange: " + player.thePlayer.getName());
		List<WpClaymore> clays = new ArrayList<>(this.claymorsByPlayer.get(player));
		for(WpClaymore clay : clays)
		{
			this.remove(clay);
			EntitySyncCalls.removeEntity(clay.claymore);
		}
	}

	@Override
	public Material getMaterial(World w)
	{
		return Main.config.claymore.getMaterial(w);
	}

	@Override
	public short getSubId(World w) 
	{
		return Main.config.claymore.getSubid(w);
	}
	
	private void remove(WpClaymore clay)
	{
		this.claymorsByItem.remove(clay.claymore);
		this.claymorsByMatch.get(clay.owner.getMatch()).remove(clay);
		this.claymorsByPlayer.get(clay.owner).remove(clay);
	}
}
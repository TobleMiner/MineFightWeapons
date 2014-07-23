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

import TobleMiner.MineFight.Debug.Debugger;
import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;
import TobleMiner.MineFight.Util.SyncDerp.EntitySyncCalls;
import TobleMiner.MineFight.Weapon.Weapon;
import TobleMiner.MineFightWeapons.Main;
import TobleMiner.MineFightWeapons.Weapons.Explosive.WpFrag;

public class Frag implements Weapon
{
	private HashMap<Match, List<WpFrag>> fragsByMatch = new HashMap<>();
	private HashMap<Item, WpFrag> fragsByItem = new HashMap<>();
	private HashMap<PVPPlayer, List<WpFrag>> fragsByPlayer = new HashMap<>();
		
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
		World w = m.getWorld();
		if(event instanceof PlayerDropItemEvent)
		{
			PlayerDropItemEvent pdie = (PlayerDropItemEvent)event;
			if(pdie.getItemDrop().getItemStack().getType() != this.getMaterial(m.getWorld()) || pdie.getItemDrop().getItemStack().getDurability() != this.getSubId(m.getWorld()))
				return;
			Debugger.writeDebugOut("frag dropped by " + pdie.getPlayer().getName());
			PVPPlayer player = m.getPlayerExact(pdie.getPlayer());
			if(player == null || !player.isSpawned())
			{
				Debugger.writeDebugOut("frag not created: Player not spawned: " + pdie.getPlayer().getName());
				pdie.setCancelled(true);
				return;
			}
			Item item = pdie.getItemDrop();
			if(Main.papi.getProtections().isLocProtected(item.getLocation()) && !Main.config.frag.ignoreProtection(item.getWorld()))
			{
				Debugger.writeDebugOut("frag not created: Area protected: " + pdie.getPlayer().getName());
				pdie.setCancelled(true);
				return;
			}
			Debugger.writeDebugOut("frag created: " + pdie.getPlayer().getName());
			float speed = (float)Main.config.frag.getThrowSpeed(w);
			if(player.thePlayer.isSneaking())
			{
				speed = (float)Main.config.frag.getThrowSpeedSneak(w);
			}
			else if(player.thePlayer.isSprinting())
			{
				speed = (float)Main.config.frag.getThrowSpeedSprint(w);
			}
			WpFrag frag = new WpFrag(item, player, m, this, speed);
			this.fragsByPlayer.get(player).add(frag);
			this.fragsByItem.put(item, frag);
			this.fragsByMatch.get(m).add(frag);
			pdie.setCancelled(false);
		}
		else if(event instanceof PlayerPickupItemEvent)
		{
			PlayerPickupItemEvent ppie = (PlayerPickupItemEvent)event;
			if(ppie.getItem().getItemStack().getType() != this.getMaterial(m.getWorld()))
				return;
			WpFrag frag = this.fragsByItem.get(ppie.getItem());
			if(frag == null)
				return;
			Debugger.writeDebugOut(String.format("%s is trying to pick up a frag: Owner: %s", ppie.getPlayer().getName(), frag.owner.thePlayer.getName()));
			ppie.setCancelled(true);
		}
		else if(event instanceof EntityDamageEvent)
		{
			EntityDamageEvent ede = (EntityDamageEvent)event;
			if(ede.getEntity() instanceof Item)
			{
				Item item = (Item)ede.getEntity();
				if(item.getItemStack().getType() != this.getMaterial(m.getWorld()))
					return;
				WpFrag frag = this.fragsByItem.get(item);
				if(frag == null)
					return;
				Debugger.writeDebugOut("frag damaged");
				ede.setCancelled(true);
				if(ede.getCause() == DamageCause.BLOCK_EXPLOSION || ede.getCause() == DamageCause.ENTITY_EXPLOSION)
				{
					Debugger.writeDebugOut("frag damaged by explosion. Exploding.");
					frag.explode();
					this.remove(frag);
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
				WpFrag frag = this.fragsByItem.get(item);
				if(frag == null)
					return;
				ice.setCancelled(true);
				frag.explode();
				return;
			}
		}
		else if(event instanceof ItemDespawnEvent)
		{
			ItemDespawnEvent ide = (ItemDespawnEvent)event;
			Item item = (Item)ide.getEntity();
			if(item.getItemStack().getType() != this.getMaterial(m.getWorld()))
				return;
			WpFrag frag = this.fragsByItem.get(item);
			if(frag == null)
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
		if(Main.config.frag.despawnOnDeath(m.getWorld()))
		{
			List<WpFrag> frags = new ArrayList<>(this.fragsByPlayer.get(killed));
			for(WpFrag frag : frags)
			{
				this.remove(frag);
				EntitySyncCalls.removeEntity(frag.item);
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
		fragsByMatch.put(m, new ArrayList<WpFrag>());
	}

	@Override
	public void matchEnded(Match m) 
	{
		fragsByMatch.remove(m);
	}

	@Override
	public void onTick()
	{
		//nop
	}

	@Override
	public void onJoin(Match m, PVPPlayer player) 
	{
		Debugger.writeDebugOut("Creating frag-entry for " + player.thePlayer.getName());
		fragsByPlayer.put(player, new ArrayList<WpFrag>());
	}

	@Override
	public void onLeave(Match m, PVPPlayer player) 
	{
		Debugger.writeDebugOut("Removing frag-entry for " + player.thePlayer.getName());
		List<WpFrag> frags = new ArrayList<>(this.fragsByPlayer.get(player));
		for(WpFrag frag : frags)
		{
			fragsByItem.remove(frag.item);
			fragsByMatch.get(m).remove(frag);
		}
		fragsByPlayer.remove(player);
	}

	@Override
	public void onTeamchange(Match m, PVPPlayer player)
	{
		Debugger.writeDebugOut("Removing fragmors due to teamchange: " + player.thePlayer.getName());
		List<WpFrag> frags = new ArrayList<>(this.fragsByPlayer.get(player));
		for(WpFrag frag : frags)
		{
			this.remove(frag);
			EntitySyncCalls.removeEntity(frag.item);
		}
	}

	@Override
	public Material getMaterial(World w)
	{
		return Main.config.frag.getMaterial(w);
	}

	@Override
	public short getSubId(World w) 
	{
		return Main.config.frag.getSubid(w);
	}
	
	public void remove(WpFrag frag) //Used as callback frow WpFrag to remove references to invalid frag
	{
		this.fragsByItem.remove(frag.item);
		this.fragsByMatch.get(frag.owner.getMatch()).remove(frag);
		this.fragsByPlayer.get(frag.owner).remove(frag);
	}
}

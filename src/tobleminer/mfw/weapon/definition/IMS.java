package tobleminer.mfw.weapon.definition;

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

import tobleminer.mfw.Main;
import tobleminer.mfw.weapon.stationary.ims.WpIMS;
import TobleMiner.MineFight.API.MineFightEventListener;
import TobleMiner.MineFight.Debug.Debugger;
import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;
import TobleMiner.MineFight.Weapon.Weapon;

public class IMS implements Weapon, MineFightEventListener
{
	private HashMap<Match, List<WpIMS>> imssByMatch = new HashMap<>();
	private HashMap<Item, WpIMS> imsByItem = new HashMap<>();
	private HashMap<PVPPlayer, List<WpIMS>> imssByPlayer = new HashMap<>();
		
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
			Debugger.writeDebugOut("ims dropped by " + pdie.getPlayer().getName());
			PVPPlayer player = m.getPlayerExact(pdie.getPlayer());
			if(player == null || !player.isSpawned())
			{
				Debugger.writeDebugOut("ims not created: Player not spawned: " + pdie.getPlayer().getName());
				pdie.setCancelled(true);
				return;
			}
			Item item = pdie.getItemDrop();
			if(Main.papi.getProtections().isLocProtected(item.getLocation()) && !Main.config.ims.ignoreProtection(item.getWorld()))
			{
				Debugger.writeDebugOut("ims not created: Area protected: " + pdie.getPlayer().getName());
				pdie.setCancelled(true);
				return;
			}
			Debugger.writeDebugOut("ims created: " + pdie.getPlayer().getName());
			WpIMS ims = new WpIMS(m, item, player);
			this.imssByPlayer.get(player).add(ims);
			this.imsByItem.put(item, ims);
			this.imssByMatch.get(m).add(ims);
			if(this.imssByPlayer.get(player).size() > Main.config.ims.getLimit(m.getWorld()))
			{
				List<WpIMS> imss = new ArrayList<>(this.imssByPlayer.get(player));
				if(imss.size() > 0)
				{
					WpIMS tims = imss.get(0);
					tims.remove();
					this.remove(tims);
				}
			}
			pdie.setCancelled(false);
		}
		else if(event instanceof PlayerPickupItemEvent)
		{
			PlayerPickupItemEvent ppie = (PlayerPickupItemEvent)event;
			if(ppie.getItem().getItemStack().getType() != this.getMaterial(m.getWorld()))
				return;
			PVPPlayer player = m.getPlayerExact(ppie.getPlayer());
			if(player == null || !player.isSpawned())
			{
				Debugger.writeDebugOut(String.format("%s hasn't spawned. No pickup.", ppie.getPlayer().getName()));
				ppie.setCancelled(true);
				return;
			}
			WpIMS ims = this.imsByItem.get(ppie.getItem());
			if(ims == null)
				return;
			ppie.setCancelled(true);
			Debugger.writeDebugOut(String.format("%s is trying to pickup ims: Owner: %s", ppie.getPlayer().getName(), ims.owner.thePlayer.getName()));
			if(player != ims.owner)
			{
				if(player.getTeam() != ims.owner.getTeam() && Main.config.ims.canEnemyPickup(m.getWorld()) && player.thePlayer.isSneaking())
				{
					Debugger.writeDebugOut(String.format("%s is picking up a hostile ims.", ppie.getPlayer().getName()));
					ppie.setCancelled(false);
					this.remove(ims);
				}
			}
			else
			{
				Debugger.writeDebugOut(String.format("%s found his own ims.", ppie.getPlayer().getName()));
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
				WpIMS ims = this.imsByItem.get(item);
				if(ims == null)
					return;
				Debugger.writeDebugOut("ims damaged");
				ede.setCancelled(true);
				if(ede.getCause() == DamageCause.BLOCK_EXPLOSION || ede.getCause() == DamageCause.ENTITY_EXPLOSION)
				{
					Debugger.writeDebugOut("ims damaged by explosion. Exploding.");
					this.remove(ims);
					ims.remove();
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
				WpIMS ims = this.imsByItem.get(item);
				if(ims == null)
					return;
				ice.setCancelled(true);
				ims.remove();
				return;
			}
		}
		else if(event instanceof ItemDespawnEvent)
		{
			ItemDespawnEvent ide = (ItemDespawnEvent)event;
			Item item = (Item)ide.getEntity();
			if(item.getItemStack().getType() != this.getMaterial(m.getWorld()))
				return;
			WpIMS ims = this.imsByItem.get(item);
			if(ims == null)
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
		if(Main.config.ims.despawnOnDeath(m.getWorld()))
		{
			List<WpIMS> imss = new ArrayList<>(this.imssByPlayer.get(killed));
			for(WpIMS ims : imss)
			{
				this.remove(ims);
				ims.remove();
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
		imssByMatch.put(m, new ArrayList<WpIMS>());
	}

	@Override
	public void matchEnded(Match m) 
	{
		imssByMatch.remove(m);
	}

	@Override
	public void onTick()
	{
		//nop
	}

	@Override
	public void onJoin(Match m, PVPPlayer player) 
	{
		Debugger.writeDebugOut("Creating ims-entry for " + player.thePlayer.getName());
		imssByPlayer.put(player, new ArrayList<WpIMS>());
	}

	@Override
	public void onLeave(Match m, PVPPlayer player) 
	{
		Debugger.writeDebugOut("Removing ims-entry for " + player.thePlayer.getName());
		List<WpIMS> imss = imssByPlayer.get(player);
		for(WpIMS ims : imss)
		{
			imsByItem.remove(ims.item);
			ims.remove();
			imssByMatch.get(m).remove(ims);
		}
		imssByPlayer.remove(player);
	}

	@Override
	public void onTeamchange(Match m, PVPPlayer player)
	{
		Debugger.writeDebugOut("Removing ims due to teamchange: " + player.thePlayer.getName());
		List<WpIMS> imss = new ArrayList<>(this.imssByPlayer.get(player));
		for(WpIMS ims : imss)
		{
			this.remove(ims);
			ims.remove();
		}
	}

	@Override
	public Material getMaterial(World w)
	{
		return Main.config.ims.getMaterial(w);
	}

	@Override
	public short getSubId(World w) 
	{
		return Main.config.ims.getSubid(w);
	}
	
	private void remove(WpIMS ims)
	{
		if(ims.item != null) this.imsByItem.remove(ims.item);
		this.imssByMatch.get(ims.owner.getMatch()).remove(ims);
		this.imssByPlayer.get(ims.owner).remove(ims);
	}
}

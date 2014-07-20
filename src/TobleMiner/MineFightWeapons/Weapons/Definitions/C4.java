package TobleMiner.MineFightWeapons.Weapons.Definitions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import TobleMiner.MineFight.API.MineFightWeaponAPI;
import TobleMiner.MineFight.Debug.Debugger;
import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;
import TobleMiner.MineFight.Util.Util;
import TobleMiner.MineFight.Weapon.Weapon;
import TobleMiner.MineFightWeapons.Main;
import TobleMiner.MineFightWeapons.Weapons.Explosive.WpC4;

public class C4 implements Weapon
{
	private HashMap<Match, List<WpC4>> c4sByMatch = new HashMap<>();
	private HashMap<Item, WpC4> c4sByItem = new HashMap<>();
	private HashMap<PVPPlayer, List<WpC4>> c4sByPlayer = new HashMap<>();
		
	@Override
	public void getRequiredEvents(List<Class<?>> events)
	{
		events.add(PlayerInteractEvent.class);
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
		if(event instanceof PlayerInteractEvent)
		{
			PlayerInteractEvent pie = (PlayerInteractEvent)event;
			PVPPlayer player = m.getPlayerExact(pie.getPlayer());
			if(player == null || !player.isSpawned())
			{
				Debugger.writeDebugOut("c4 not created: Player not spawned: " + pie.getPlayer().getName());
				pie.setCancelled(true);
				return;
			}
			ItemStack inHand = player.thePlayer.getItemInHand();
			if(inHand == null)
				return;
			Action action = pie.getAction();
			if(inHand.getType() == Main.config.c4.getDetonatorMaterial(w) && inHand.getDurability() == Main.config.c4.getDetonatorSubId(w))
			{
				if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
				{
					List<WpC4> c4s = new ArrayList<>(this.c4sByPlayer.get(player));
					for(WpC4 c4 : c4s)
					{
						c4.explode();
						this.remove(c4);
					}
					pie.setCancelled(true);
				}
			}
			else if(inHand.getType() == this.getMaterial(m.getWorld()) && inHand.getDurability() == this.getSubId(m.getWorld()))			
			{
				if(action == Action.RIGHT_CLICK_BLOCK)
				{
					Block b = pie.getClickedBlock();
					if((!Util.protect.isBlockProtected(b)) || Main.config.c4.ignoreProtection(w))
					{
						WpC4 c4 = new WpC4(b, null, player, m);
						this.c4sByPlayer.get(player).add(c4);
						this.c4sByMatch.get(m).add(c4);
						if(this.c4sByPlayer.get(player).size() > Main.config.c4.getLimit(m.getWorld()))
						{
							List<WpC4> c4s = new ArrayList<>(this.c4sByPlayer.get(player));
							if(c4s.size() > 0)
							{
								WpC4 tc4 = c4s.get(0);
								tc4.remove();
								this.remove(tc4);
							}
						}
						pie.setCancelled(true);
					}
				}
			}
		}
		else if(event instanceof PlayerDropItemEvent)
		{
			PlayerDropItemEvent pdie = (PlayerDropItemEvent)event;
			if(pdie.getItemDrop().getItemStack().getType() != this.getMaterial(m.getWorld()) || pdie.getItemDrop().getItemStack().getDurability() != this.getSubId(m.getWorld()))
				return;
			Debugger.writeDebugOut("c4 dropped by " + pdie.getPlayer().getName());
			PVPPlayer player = m.getPlayerExact(pdie.getPlayer());
			if(player == null || !player.isSpawned())
			{
				Debugger.writeDebugOut("c4 not created: Player not spawned: " + pdie.getPlayer().getName());
				pdie.setCancelled(true);
				return;
			}
			Item item = pdie.getItemDrop();
			if(MineFightWeaponAPI.instance.getProtections().isLocProtected(item.getLocation()) && !Main.config.c4.ignoreProtection(item.getWorld()))
			{
				Debugger.writeDebugOut("c4 not created: Area protected: " + pdie.getPlayer().getName());
				pdie.setCancelled(true);
				return;
			}
			Debugger.writeDebugOut("c4 created: " + pdie.getPlayer().getName());
			WpC4 c4 = new WpC4(null, item, player, m);
			this.c4sByPlayer.get(player).add(c4);
			this.c4sByItem.put(item, c4);
			this.c4sByMatch.get(m).add(c4);
			if(this.c4sByPlayer.get(player).size() > Main.config.c4.getLimit(m.getWorld()))
			{
				List<WpC4> c4s = new ArrayList<>(this.c4sByPlayer.get(player));
				if(c4s.size() > 0)
				{
					WpC4 tc4 = c4s.get(0);
					tc4.remove();
					this.remove(tc4);
				}
			}
			pdie.setCancelled(false);
		}
		else if(event instanceof PlayerPickupItemEvent)
		{
			PlayerPickupItemEvent ppie = (PlayerPickupItemEvent)event;
			if(ppie.getItem().getItemStack().getType() != this.getMaterial(m.getWorld()))
				return;
			ppie.setCancelled(true);
			PVPPlayer player = m.getPlayerExact(ppie.getPlayer());
			if(player == null || !player.isSpawned())
			{
				Debugger.writeDebugOut(String.format("%s hasn't spawned. No pickup.", ppie.getPlayer().getName()));
				return;
			}
			WpC4 c4 = this.c4sByItem.get(ppie.getItem());
			if(c4 == null)
				return;
			Debugger.writeDebugOut(String.format("%s is trying to pickup c4: Owner: %s", ppie.getPlayer().getName(), c4.owner.thePlayer.getName()));
			if(player != c4.owner)
			{
				if(player.getTeam() != c4.owner.getTeam() && Main.config.c4.canEnemyPickup(m.getWorld()) && player.thePlayer.isSneaking())
				{
					Debugger.writeDebugOut(String.format("%s is picking up a hostile c4.", ppie.getPlayer().getName()));
					ppie.setCancelled(false);
					this.remove(c4);
				}
			}
			else
			{
				Debugger.writeDebugOut(String.format("%s found his own c4.", ppie.getPlayer().getName()));
				if(player.thePlayer.isSneaking() && Main.config.c4.canOwnerPickup(m.getWorld()))
				{
					Debugger.writeDebugOut(String.format("%s is picking up his c4.", ppie.getPlayer().getName()));
					ppie.setCancelled(false);
					this.remove(c4);
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
				WpC4 c4 = this.c4sByItem.get(item);
				if(c4 == null)
					return;
				Debugger.writeDebugOut("c4 damaged");
				ede.setCancelled(true);
				if(ede.getCause() == DamageCause.BLOCK_EXPLOSION || ede.getCause() == DamageCause.ENTITY_EXPLOSION)
				{
					Debugger.writeDebugOut("c4 damaged by explosion. Exploding.");
					c4.explode();
					this.remove(c4);
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
				WpC4 c4 = this.c4sByItem.get(item);
				if(c4 == null)
					return;
				ice.setCancelled(true);
				c4.explode();
				return;
			}
		}
		else if(event instanceof ItemDespawnEvent)
		{
			ItemDespawnEvent ide = (ItemDespawnEvent)event;
			Item item = (Item)ide.getEntity();
			if(item.getItemStack().getType() != this.getMaterial(m.getWorld()))
				return;
			WpC4 c4 = this.c4sByItem.get(item);
			if(c4 == null)
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
		if(Main.config.c4.despawnOnDeath(m.getWorld()))
		{
			List<WpC4> c4s = new ArrayList<>(this.c4sByPlayer.get(killed));
			for(WpC4 c4 : c4s)
			{
				this.remove(c4);
				c4.remove();
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
		c4sByMatch.put(m, new ArrayList<WpC4>());
	}

	@Override
	public void matchEnded(Match m) 
	{
		c4sByMatch.remove(m);
	}

	@Override
	public void onTick()
	{
		//nop
	}

	@Override
	public void onJoin(Match m, PVPPlayer player) 
	{
		Debugger.writeDebugOut("Creating c4-entry for " + player.thePlayer.getName());
		c4sByPlayer.put(player, new ArrayList<WpC4>());
	}

	@Override
	public void onLeave(Match m, PVPPlayer player) 
	{
		Debugger.writeDebugOut("Removing c4-entry for " + player.thePlayer.getName());
		List<WpC4> c4s = c4sByPlayer.get(player);
		for(WpC4 c4 : c4s)
		{
			if(c4.item != null)
				c4sByItem.remove(c4.item);
			c4.remove();
			c4sByMatch.get(m).remove(c4);
		}
		c4sByPlayer.remove(player);
	}

	@Override
	public void onTeamchange(Match m, PVPPlayer player)
	{
		Debugger.writeDebugOut("Removing c4 due to teamchange: " + player.thePlayer.getName());
		List<WpC4> c4s = new ArrayList<>(this.c4sByPlayer.get(player));
		for(WpC4 c4 : c4s)
		{
			this.remove(c4);
			c4.remove();
		}
	}

	@Override
	public Material getMaterial(World w)
	{
		return Main.config.c4.getMaterial(w);
	}

	@Override
	public short getSubId(World w) 
	{
		return Main.config.c4.getSubid(w);
	}
	
	private void remove(WpC4 c4)
	{
		if(c4.item != null) this.c4sByItem.remove(c4.item);
		this.c4sByMatch.get(c4.owner.getMatch()).remove(c4);
		this.c4sByPlayer.get(c4.owner).remove(c4);
	}
}
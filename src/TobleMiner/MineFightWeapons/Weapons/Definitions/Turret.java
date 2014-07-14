package TobleMiner.MineFightWeapons.Weapons.Definitions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import TobleMiner.MineFight.API.MineFightWeaponAPI;
import TobleMiner.MineFight.Debug.Debugger;
import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;
import TobleMiner.MineFight.Weapon.Weapon;
import TobleMiner.MineFightWeapons.Main;
import TobleMiner.MineFightWeapons.Weapons.Stationary.Turret.TurretMissile;
import TobleMiner.MineFightWeapons.Weapons.Stationary.Turret.WpTurret;

public class Turret implements Weapon
{
	private HashMap<Match, List<WpTurret>> turretsByMatch= new HashMap<>();
	private HashMap<Block, WpTurret> turretsByBlock = new HashMap<>();
	private HashMap<PVPPlayer, List<WpTurret>> turretsByPlayer = new HashMap<>();
	private HashMap<Arrow, WpTurret> turretByProjectile = new HashMap<>();
	private HashMap<Arrow, TurretMissile> missileByProjectile = new HashMap<>();
		
	@Override
	public void getRequiredEvents(List<Class<?>> events)
	{
		events.add(BlockPlaceEvent.class);
		events.add(PlayerInteractEvent.class);
		events.add(EntityDamageByEntityEvent.class);
		events.add(ProjectileHitEvent.class);
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
				Debugger.writeDebugOut("turret not created: Player not spawned: " + pie.getPlayer().getName());
				pie.setCancelled(true);
				return;
			}
			ItemStack inHand = player.thePlayer.getItemInHand();
			if(inHand == null)
				return;
			Action action = pie.getAction();
			Debugger.writeDebugOut(String.format("Hand: %s:%d", inHand.getType().toString(), inHand.getDurability()));
			Debugger.writeDebugOut(String.format("Controller: %s:%d", Main.config.turret.getControllerMaterial(w).toString(), Main.config.turret.getControllerSubId(w)));
			if(inHand.getType() == Main.config.turret.getControllerMaterial(w) && inHand.getDurability() == Main.config.turret.getControllerSubId(w))
			{
				HashSet<Byte> trans = new HashSet<Byte>();
				trans.add((byte)31);
				trans.add((byte)0);
				trans.add((byte)20);
				trans.add((byte)102);
				Block b = player.thePlayer.getTargetBlock(trans, 200);
				if(b != null)
				{
					if(action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)
					{
						List<WpTurret> turrets = new ArrayList<>(this.turretsByPlayer.get(player));
						for(WpTurret turret : turrets)
						{
							Arrow arr = turret.shoot(b.getLocation());
							if(arr != null)
								this.turretByProjectile.put(arr, turret);
							else
								turret.getOwner().thePlayer.sendMessage(ChatColor.RED + TobleMiner.MineFight.Main.gameEngine.dict.get("sentry_ammo"));
						}
					}
					else if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
					{
						List<WpTurret> turrets = new ArrayList<>(this.turretsByPlayer.get(player));
						for(WpTurret turret : turrets)
						{
							TurretMissile missile = turret.shootMissile(b.getLocation());
							if(missile != null)
								this.missileByProjectile.put(missile.getArrow(), missile);
							else
								turret.getOwner().thePlayer.sendMessage(ChatColor.RED + TobleMiner.MineFight.Main.gameEngine.dict.get("sentry_missile"));
						}
					}
					pie.setCancelled(true);
				}
			}
		}
		else if(event instanceof EntityDamageByEntityEvent)
		{
			EntityDamageByEntityEvent edbe = (EntityDamageByEntityEvent)event;
			if(edbe.getEntity() instanceof Player && edbe.getDamager() instanceof Arrow)
			{
				Player p = (Player)edbe.getEntity();
				Debugger.writeDebugOut("Player damaged by arrow: " + p.getName());
				PVPPlayer player = m.getPlayerExact(p);
				if(player == null || !player.isSpawned())
				{
					Debugger.writeDebugOut("Player not damaged: Player not spawned: " + p.getName());
					edbe.setCancelled(true);
					return;
				}
				Arrow arr = (Arrow)edbe.getDamager();
				WpTurret turret = this.turretByProjectile.remove(arr);
				if(turret == null)
					return;
				if(m.canKill(turret.getOwner(), player))
				{
					player.normalDeathBlocked = true;
					player.thePlayer.damage((float)edbe.getDamage());
					if(player.thePlayer.getHealth() <= 0d)
					{
						m.kill(turret.getOwner(), player, turret.getLocName(), false);
					}
					else
					{
						player.addKillhelper(turret.getOwner(), edbe.getDamage());
					}
					player.normalDeathBlocked = false;
					edbe.setCancelled(true);
				}
			}
		}
		else if(event instanceof ProjectileHitEvent)
		{
			ProjectileHitEvent phe = (ProjectileHitEvent)event;
			Projectile proj = phe.getEntity();
			if(!(proj instanceof Arrow))
				return;
			Arrow arr = (Arrow)proj;
			//this.turretByProjectile.remove(arr);
			TurretMissile missile = this.missileByProjectile.remove(arr);
			if(missile == null)
				return;
			missile.explode();
		}
		else if(event instanceof BlockPlaceEvent)
		{
			BlockPlaceEvent bpe = (BlockPlaceEvent)event;
			if(bpe.getBlock().getType() != this.getMaterial(m.getWorld()))
				return;
			Debugger.writeDebugOut("Turret placed by " + bpe.getPlayer().getName());
			PVPPlayer player = m.getPlayerExact(bpe.getPlayer());
			if(player == null || !player.isSpawned())
			{
				Debugger.writeDebugOut("Turret not created: Player not spawned: " + bpe.getPlayer().getName());
				bpe.setCancelled(true);
				return;
			}
			Block b = bpe.getBlock();
			if(MineFightWeaponAPI.instance.getProtections().isBlockProtected(b) && !Main.config.c4.ignoreProtection(b.getWorld()))
			{
				Debugger.writeDebugOut("Turret not created: Area protected: " + bpe.getPlayer().getName());
				bpe.setCancelled(true);
				return;
			}
			Debugger.writeDebugOut("Turret created: " + bpe.getPlayer().getName());
			WpTurret turret = new WpTurret(m, (Dispenser)b.getState(), player);
			this.turretsByPlayer.get(player).add(turret);
			this.turretsByBlock.put(b, turret);
			this.turretsByMatch.get(m).add(turret);
			if(this.turretsByPlayer.get(player).size() > Main.config.c4.getLimit(m.getWorld()))
			{
				List<WpTurret> turrets = new ArrayList<>(this.turretsByPlayer.get(player));
				if(turrets.size() > 0)
				{
					WpTurret tturret = turrets.get(0);
					tturret.remove();
					this.remove(tturret);
				}
			}
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
		if(Main.config.turret.despawnOnDeath(m.getWorld()))
		{
			List<WpTurret> turrets = new ArrayList<>(this.turretsByPlayer.get(killed));
			for(WpTurret turret : turrets)
			{
				this.remove(turret);
				turret.remove();
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
		turretsByMatch.put(m, new ArrayList<WpTurret>());
	}

	@Override
	public void matchEnded(Match m) 
	{
		turretsByMatch.remove(m);
	}

	@Override
	public void onTick()
	{
		//nop
	}

	@Override
	public void onJoin(Match m, PVPPlayer player) 
	{
		Debugger.writeDebugOut("Creating turret-entry for " + player.thePlayer.getName());
		turretsByPlayer.put(player, new ArrayList<WpTurret>());
	}

	@Override
	public void onLeave(Match m, PVPPlayer player) 
	{
		Debugger.writeDebugOut("Removing turret-entry for " + player.thePlayer.getName());
		List<WpTurret> turrets = turretsByPlayer.get(player);
		for(WpTurret turret : turrets)
		{
			turretsByBlock.remove(turret.dispenser.getBlock());
			turretsByMatch.get(m).remove(turret);
			turret.remove();
		}
		turretsByPlayer.remove(player);
	}

	@Override
	public void onTeamchange(Match m, PVPPlayer player)
	{
		Debugger.writeDebugOut("Removing turret due to teamchange: " + player.thePlayer.getName());
		List<WpTurret> turrets = new ArrayList<>(this.turretsByPlayer.get(player));
		for(WpTurret turret : turrets)
		{
			this.remove(turret);
			turret.remove();
		}
	}

	@Override
	public Material getMaterial(World w)
	{
		return Main.config.turret.getMaterial(w);
	}

	@Override
	public short getSubId(World w) 
	{
		return Main.config.turret.getSubid(w);
	}
	
	private void remove(WpTurret turret)
	{
		this.turretsByBlock.remove(turret.dispenser.getBlock());
		this.turretsByMatch.get(turret.getOwner().getMatch()).remove(turret);
		this.turretsByPlayer.get(turret.getOwner()).remove(turret);
	}
}

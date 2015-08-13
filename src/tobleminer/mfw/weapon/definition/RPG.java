package tobleminer.mfw.weapon.definition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import tobleminer.mfw.Main;
import tobleminer.mfw.weapon.explosive.missile.WpRPG;
import tobleminer.minefight.api.MineFightEventListener;
import tobleminer.minefight.debug.Debugger;
import tobleminer.minefight.engine.match.Match;
import tobleminer.minefight.engine.player.PVPPlayer;
import tobleminer.minefight.weapon.Weapon;

public class RPG implements Weapon, MineFightEventListener
{
	private HashMap<Match, List<WpRPG>>		rpgsByMatch		= new HashMap<>();
	private HashMap<PVPPlayer, List<WpRPG>>	rpgsByPlayer	= new HashMap<>();
	private HashMap<Arrow, WpRPG>			rpgByProjectile	= new HashMap<>();

	@Override
	public void getRequiredEvents(List<Class<?>> events)
	{
		events.add(PlayerInteractEvent.class);
		events.add(ProjectileHitEvent.class);
	}

	@Override
	public void onEvent(Match m, Event event)
	{
		World w = m.getWorld();
		if (event instanceof PlayerInteractEvent)
		{
			PlayerInteractEvent pie = (PlayerInteractEvent) event;
			PVPPlayer player = m.getPlayerExact(pie.getPlayer());
			if (player == null || !player.isSpawned())
			{
				Debugger.writeDebugOut("rpg not created: Player not spawned: " + pie.getPlayer().getName());
				pie.setCancelled(true);
				return;
			}
			ItemStack inHand = player.thePlayer.getItemInHand();
			if (inHand == null)
				return;
			Action action = pie.getAction();
			Debugger.writeDebugOut(String.format("Hand: %s:%d", inHand.getType().toString(), inHand.getDurability()));
			Debugger.writeDebugOut(String.format("RPG: %s:%d", Main.config.rpg.getMaterial(w).toString(),
					Main.config.rpg.getSubid(w)));
			if (inHand.getType() == Main.config.rpg.getMaterial(w)
					&& inHand.getDurability() == Main.config.rpg.getSubid(w))
			{
				if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK)
					return;
				HashSet<Byte> trans = new HashSet<Byte>();
				trans.add((byte) 31);
				trans.add((byte) 0);
				trans.add((byte) 20);
				trans.add((byte) 102);
				Block b = player.thePlayer.getTargetBlock(trans, 200);
				if (b != null)
				{
					Location playerEyeLoc = player.thePlayer.getEyeLocation();
					Vector locHelp = b.getLocation().subtract(playerEyeLoc).toVector();
					Location launchLoc = playerEyeLoc.add(locHelp.multiply(1.5d / locHelp.length()));
					Vector vec = b.getLocation().subtract(launchLoc).toVector();
					Arrow arr = w.spawnArrow(launchLoc, locHelp, 1f, 1f);
					WpRPG rpg = new WpRPG(m, arr, player, vec, this);
					this.rpgsByMatch.get(m).add(rpg);
					this.rpgByProjectile.put(arr, rpg);
					this.rpgsByPlayer.get(player).add(rpg);
					player.thePlayer.getInventory()
							.removeItem(new ItemStack(Main.config.rpg.getMaterial(w), 1, Main.config.rpg.getSubid(w)));
					player.thePlayer.updateInventory();
					pie.setCancelled(true);
				}
			}
		}
		else if (event instanceof ProjectileHitEvent)
		{
			ProjectileHitEvent phe = (ProjectileHitEvent) event;
			Projectile proj = phe.getEntity();
			if (!(proj instanceof Arrow))
				return;
			Arrow arr = (Arrow) proj;
			WpRPG rpg = this.rpgByProjectile.get(arr);
			if (rpg == null)
				return;
			rpg.explode();
		}
	}

	@Override
	public void onKill(Match m, PVPPlayer killer, PVPPlayer killed)
	{
		// nop
	}

	@Override
	public void onDeath(Match m, PVPPlayer killed, PVPPlayer killer)
	{
		if (Main.config.rpg.despawnOnDeath(m.getWorld()))
		{
			List<WpRPG> rpgs = new ArrayList<>(this.rpgsByPlayer.get(killed));
			for (WpRPG rpg : rpgs)
			{
				this.remove(rpg);
				rpg.remove();
			}
		}
	}

	@Override
	public void onRespawn(Match m, PVPPlayer player)
	{
		// nop
	}

	@Override
	public void matchCreated(Match m)
	{
		rpgsByMatch.put(m, new ArrayList<WpRPG>());
	}

	@Override
	public void matchEnded(Match m)
	{
		rpgsByMatch.remove(m);
	}

	@Override
	public void onTick()
	{
		// nop
	}

	@Override
	public void onJoin(Match m, PVPPlayer player)
	{
		Debugger.writeDebugOut("Creating rpg-entry for " + player.thePlayer.getName());
		rpgsByPlayer.put(player, new ArrayList<WpRPG>());
	}

	@Override
	public void onLeave(Match m, PVPPlayer player)
	{
		Debugger.writeDebugOut("Removing rpg-entry for " + player.thePlayer.getName());
		List<WpRPG> rpgs = new ArrayList<>(rpgsByPlayer.get(player));
		for (WpRPG rpg : rpgs)
		{
			rpgByProjectile.remove(rpg.arr);
			rpgsByMatch.get(m).remove(rpg);
			rpg.remove();
		}
		rpgsByPlayer.remove(player);
	}

	@Override
	public void onTeamchange(Match m, PVPPlayer player)
	{
		Debugger.writeDebugOut("Removing rpg due to teamchange: " + player.thePlayer.getName());
		List<WpRPG> rpgs = new ArrayList<>(this.rpgsByPlayer.get(player));
		for (WpRPG rpg : rpgs)
		{
			this.remove(rpg);
			rpg.remove();
		}
	}

	@Override
	public Material getMaterial(World w)
	{
		return Main.config.rpg.getMaterial(w);
	}

	@Override
	public short getSubId(World w)
	{
		return Main.config.rpg.getSubid(w);
	}

	public void remove(WpRPG rpg)
	{
		this.rpgByProjectile.remove(rpg.arr);
		this.rpgsByMatch.get(rpg.owner.getMatch()).remove(rpg);
		this.rpgsByPlayer.get(rpg.owner).remove(rpg);
	}
}

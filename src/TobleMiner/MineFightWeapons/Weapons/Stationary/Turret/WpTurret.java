package TobleMiner.MineFightWeapons.Weapons.Stationary.Turret;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Arrow;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import TobleMiner.MineFight.Debug.Debugger;
import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;
import TobleMiner.MineFightWeapons.Main;

public class WpTurret
{
	public final Dispenser dispenser;
	private final PVPPlayer owner;
	private final Match match;
	
	public WpTurret(Match match, Dispenser disp, PVPPlayer owner)
	{
		this.dispenser = disp;
		this.owner = owner;
		this.match = match;
	}		
	
	public Arrow shoot(Location target)
	{
		if(!dispenser.getBlock().getType().equals(Main.config.turret.getMaterial(dispenser.getWorld())))
		{
			return null;
		}
		Debugger.writeDebugOut("Trying to launch arrow from turret");
		Inventory inv = dispenser.getInventory();
		if(inv.containsAtLeast(new ItemStack(Main.config.turret.getAmmoMaterial(dispenser.getWorld()), 1, (short)Main.config.turret.getAmmoSubId(dispenser.getWorld())), 1))
		{
			Debugger.writeDebugOut("Launching arrow from turret");
			Vector locHelp = target.clone().subtract(this.dispenser.getLocation()).toVector();
			Location shootLoc = this.dispenser.getLocation().clone().add(locHelp.multiply(2.0d/locHelp.length()));
			Vector dir = target.clone().subtract(shootLoc.clone()).toVector();
			double speed = Main.config.turret.getAmmoSpeed(dispenser.getWorld());
			Vector vel = dir.clone().multiply(speed/dir.length());
			Arrow arr = dispenser.getWorld().spawnArrow(shootLoc, vel, (float)speed, 1.0F);
			arr.setVelocity(vel);
			inv.removeItem(new ItemStack(Main.config.turret.getAmmoMaterial(dispenser.getWorld()), 1, (short)Main.config.turret.getAmmoSubId(dispenser.getWorld())));
			return arr;
		}
		Debugger.writeDebugOut("Not enough resources available");
		return null;
	}
	
	public TurretMissile shootMissile(Location target)
	{
		if(!dispenser.getBlock().getType().equals(Main.config.turret.getMaterial(dispenser.getWorld())))
		{
			return null;
		}
		Debugger.writeDebugOut("Trying to launch missile from turret");
		Inventory inv = dispenser.getInventory();
		if(inv.containsAtLeast(new ItemStack(Main.config.turret.getMissileMaterial(dispenser.getWorld()), 1, (short)Main.config.turret.getMissileSubId(dispenser.getWorld())), 1))
		{
			Debugger.writeDebugOut("Launching missile from turret");
			Vector locHelp = target.clone().subtract(this.dispenser.getLocation()).toVector();
			Location shootLoc = this.dispenser.getLocation().clone().add(locHelp.multiply(2.0d/locHelp.length()));
			Vector dir = target.clone().subtract(shootLoc.clone()).toVector();
			double speed = Main.config.turret.getMissileSpeed(dispenser.getWorld());
			Vector vel = dir.clone().multiply(speed/dir.length());
			Arrow arr = dispenser.getWorld().spawnArrow(shootLoc, vel, (float)speed, 1.0F);
			TurretMissile missile = new TurretMissile(match, dir, this, Main.config.turret.getMissileBlastpower(dispenser.getWorld()), arr, speed);
			inv.removeItem(new ItemStack(Main.config.turret.getMissileMaterial(dispenser.getWorld()), 1, (short)Main.config.turret.getMissileSubId(dispenser.getWorld())));
			return missile;
		}
		Debugger.writeDebugOut("Not enough resources available");
		return null;
	}
	
	public PVPPlayer getOwner()
	{
		return this.owner;
	}
	
	public void remove()
	{
		this.dispenser.setType(Material.AIR);
	}
	
	public String getLocName()
	{
		return Main.langapi.localize("turret");
	}
}

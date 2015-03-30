package tobleminer.mfw.weapon.stationary.ims;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;

import tobleminer.mfw.Main;
import tobleminer.minefight.engine.GameEngine;
import tobleminer.minefight.engine.player.PVPPlayer;
import tobleminer.minefight.util.syncderp.EntitySyncCalls;
import tobleminer.minefight.weapon.tickcontrolled.TickControlledWeapon;

public class IMSGrenade extends TickControlledWeapon
{
	
	public final Arrow arr;
	private final WpIMS ims;
	private final PVPPlayer target;
	private final double maxLifetime;
	private final double exploDist;
	private final World world;
	private double time = 0;
	private int timer = 1;

	public IMSGrenade(Arrow arr, WpIMS ims, PVPPlayer target)
	{
		super(ims.match);
		this.world = arr.getWorld();
		this.ims = ims;
		this.arr = arr;
		Vector vel = this.arr.getVelocity();
		double targetVel = Main.config.ims.getGrenadeSpeed(world);
		double velLength = vel.length();
		if(velLength > 0)
		{
			arr.setVelocity(vel.clone().multiply(targetVel / velLength));
		}
		else
		{
			arr.setVelocity(new Vector(0d, targetVel, 0d));
		}
		this.target = target;
		this.maxLifetime = Main.config.ims.getMaxGrenadeLifetime(this.world);
		this.exploDist = Main.config.ims.getGrenadeExploDist(this.world);
		match.registerProjectile(arr);
	}

	@Override
	public void doUpdate()
	{
		if(timer > GameEngine.tps/10d)
		{
			timer = 0;
			time += 0.1d;
			if(time > this.maxLifetime)
			{
				this.remove();
				return;
			}
			if(this.arr.isOnGround() || this.arr.isDead())
			{
				this.explode();
				return;
			}
			if(this.arr.getVelocity().getY() < 0d)
			{
				Location locDesired = this.target.thePlayer.getLocation();
				Vector dir = locDesired.clone().subtract(this.arr.getLocation()).toVector();
				Vector vel = dir.clone().multiply(this.arr.getVelocity().getY() / dir.getY());
				this.arr.setVelocity(vel);
				if(this.arr.getLocation().distance(locDesired) <= this.exploDist)
				{
					this.explode();
				}
			}
		}
		timer++;		
	}

	public void explode()
	{
		this.ims.match.createExplosion(this.ims.getOwner(), this.arr.getLocation(), Main.config.ims.getGrenadeBlastpower(this.world), this.ims.getLocName());
		this.remove();
	}
	
	public void remove()
	{
		EntitySyncCalls.removeEntity(arr);
		ims.release(this.target);
		this.unregisterTickControlled();
		match.unregisterProjectile(this.arr);
	}
}

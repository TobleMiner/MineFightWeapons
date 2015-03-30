package tobleminer.mfw.weapon.explosive.missile;

import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;

import tobleminer.mfw.Main;
import tobleminer.mfw.weapon.definition.RPG;
import tobleminer.minefight.engine.GameEngine;
import tobleminer.minefight.engine.match.Match;
import tobleminer.minefight.engine.player.PVPPlayer;
import tobleminer.minefight.util.syncderp.EffectSyncCalls;
import tobleminer.minefight.util.syncderp.EntitySyncCalls;
import tobleminer.minefight.weapon.tickcontrolled.TickControlledWeapon;

public class WpRPG extends TickControlledWeapon
{
	public final Arrow arr;
	private int timer = 1;
	private double time = 0d;
	private final double lifeTime;
	private final double maxSpeed;
	private final double accel;
	private final Vector launchVec;
	private double speed = 0d;
	public final PVPPlayer owner;
	private final RPG manager;
	private final Random rand = new Random();
	private final double throttle;
	
	public WpRPG(Match match, Arrow arr, PVPPlayer owner, Vector launchVec, RPG manger)
	{
		super(match);
		this.arr = arr;
		World w = match.getWorld();
		this.lifeTime = Main.config.rpg.getMaxLifetime(w);
		this.maxSpeed = Main.config.rpg.getMaxSpeed(w);
		this.launchVec = launchVec;
		this.accel = Main.config.rpg.getAcceleration(w);
		this.owner = owner;
		this.manager = manger;
		this.throttle = Main.config.rpg.getThrottle(w);
		match.registerProjectile(arr);
	}

	@Override
	public void doUpdate()
	{
		if(timer >= GameEngine.tps/10d)
		{
			time += 0.1d;
			timer = 0;
			if(time > lifeTime)
			{
				this.explode();
			}
			Location loc = this.arr.getLocation();
			EffectSyncCalls.showEffect(loc, Effect.SMOKE, 0);
			EffectSyncCalls.showEffect(loc, Effect.MOBSPAWNER_FLAMES, 0);
			Vector vec = launchVec.clone();
			if(this.speed < this.maxSpeed)
			{
				this.speed += this.accel/10d;
			}
			else
			{
				this.speed = this.maxSpeed;
			}
			double mul = this.speed/vec.length();
			this.arr.setVelocity(vec.clone().multiply(mul).add(new Vector((rand.nextDouble() - 0.5d) * throttle * speed,(rand.nextDouble() - 0.5d) * throttle * speed,(rand.nextDouble() - 0.5d) * throttle * speed)));
			launchVec.setY(launchVec.getY()*0.97d);
		}
		timer++;
	}
	
	public Arrow getProjectile()
	{
		return this.arr;
	}

	public void explode() 
	{
		match.createExplosion(owner, arr.getLocation(), Main.config.rpg.getBlastPower(match.getWorld()), this.getLocName());
		this.remove();
		this.manager.remove(this);
	}
	
	public void remove()
	{
		this.unregisterTickControlled();
		EntitySyncCalls.removeEntity(this.arr);
		this.match.unregisterProjectile(this.arr);
	}
	
	public String getLocName()
	{
		return tobleminer.minefight.Main.gameEngine.dict.get("rpg");
	}

}

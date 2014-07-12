package TobleMiner.MineFightWeapons.Weapons.Stationary.Turret;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;

import TobleMiner.MineFight.GameEngine.GameEngine;
import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.Util.SyncDerp.EffectSyncCalls;
import TobleMiner.MineFight.Util.SyncDerp.EntitySyncCalls;
import TobleMiner.MineFight.Weapon.TickControlled.TickControlledWeapon;

public class TurretMissile extends TickControlledWeapon
{
	private final Vector dir;
	private final WpTurret turret;
	private final float exploStr;
	private final Arrow arr;
	private final double speed;
	private int timer = 1;
	private double time = 0d;
	private double lifeTime = 40d;
	
	public TurretMissile(Match match, Vector dir, WpTurret turret, float exploStr, Arrow arr, double speed)
	{
		super(match);
		this.dir = dir;
		this.turret = turret;
		this.exploStr = exploStr;
		this.arr = arr;
		this.speed = speed;
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
			Vector vec = dir.clone();
			double mul = this.speed/vec.length();
			this.arr.setVelocity(vec.clone().multiply(mul));
		}
		timer++;
	}
	
	public void explode()
	{
		this.unregisterTickControlled();
		match.createExplosion(turret.getOwner(), arr.getLocation(), exploStr, turret.getLocName());
		EntitySyncCalls.removeEntity(this.arr);
	}
	
	public Arrow getArrow()
	{
		return this.arr;
	}
}

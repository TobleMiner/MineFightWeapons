package tobleminer.mfw.weapon.explosive;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import tobleminer.mfw.Main;
import tobleminer.minefight.engine.match.Match;
import tobleminer.minefight.engine.player.PVPPlayer;
import tobleminer.minefight.util.geometry.Area3D;
import tobleminer.minefight.util.syncderp.EntitySyncCalls;

public class WpC4
{
	public final Block		block;
	public final Item		item;
	public final PVPPlayer	owner;
	private final Match		match;
	private Material		blockIdStore;
	private MaterialData	blockDataStore;
	private final boolean	damageEnviron;
	private boolean			exploded	= false;
	private final Area3D	area;

	public WpC4(Block b, Item i, PVPPlayer owner, Match match)
	{
		this.block = b;
		this.owner = owner;
		if (this.block != null)
		{
			this.blockIdStore = this.block.getType();
			this.blockDataStore = this.block.getState().getData();
			this.block.setType(Material.LAPIS_ORE);
		}
		this.match = match;
		this.item = i;
		this.damageEnviron = match.canEnvironmentBeDamaged();
		float dist = Main.config.c4.getBlastPower(this.match.getWorld());
		Vector vec = new Vector(dist, dist, dist);
		if (this.item != null)
		{
			this.area = new Area3D(this.item, vec, vec.clone().multiply(-1d));
		}
		else
		{
			Location loc = this.block.getLocation();
			this.area = new Area3D(loc.clone().add(vec.clone().multiply(-1d)), loc.clone().add(vec.clone()));
		}
		match.registerDangerZone(area);
	}

	public void remove()
	{
		if (this.block != null)
		{
			this.block.setType(blockIdStore);
			this.block.getState().setData(blockDataStore);
		}
		else if (this.item != null)
		{
			EntitySyncCalls.removeEntity(item);
		}
		this.match.unregisterDangerZone(area);
	}

	public void explode()
	{
		this.explode(this.owner);
	}

	public void explode(PVPPlayer exploder)
	{
		if (exploded)
			return;
		exploded = true;
		if (this.block != null)
		{
			if (this.block.getType().equals(Material.LAPIS_ORE))
			{
				match.createExplosion(exploder, this.block.getLocation(),
						Main.config.claymore.getBlastPower(this.match.getWorld()), "C4");
				if (damageEnviron)
				{
					this.block.setType(Material.AIR);
				}
				else
				{
					this.block.setType(blockIdStore);
					this.block.getState().setData(blockDataStore);
				}
			}
		}
		if (this.item != null)
		{
			Location loc = this.item.getLocation().clone();
			EntitySyncCalls.removeEntity(item);
			match.createExplosion(exploder, loc, Main.config.c4.getBlastPower(this.match.getWorld()),
					this.getLocName());
		}
	}

	public String getLocName()
	{
		return Main.langapi.localize("c4");
	}
}

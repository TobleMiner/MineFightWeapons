package TobleMiner.MineFightWeapons.Weapons.Explosive;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.material.MaterialData;

import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;
import TobleMiner.MineFight.Util.SyncDerp.EntitySyncCalls;
import TobleMiner.MineFightWeapons.Main;

public class WpC4
{
	public final Block block;
	public final Item item;
	public final PVPPlayer owner;
	private final Match match;
	private Material blockIdStore;
	private MaterialData blockDataStore;
	private final boolean damageEnviron;
	private boolean exploded = false;
	
	public WpC4(Block b, Item i, PVPPlayer owner, Match match)
	{
		this.block = b;
		this.owner = owner;
		if(this.block != null)
		{
			this.blockIdStore = this.block.getType();
			this.blockDataStore = this.block.getState().getData();
			this.block.setType(Material.LAPIS_ORE);
		}
		this.match = match;
		this.item = i;
		this.damageEnviron = match.canEnvironmentBeDamaged();
	}
	
	public void remove()
	{
		if(this.block != null)
		{
			this.block.setType(blockIdStore);
			this.block.getState().setData(blockDataStore);
		}
		else if(this.item != null)
		{
			EntitySyncCalls.removeEntity(item);
		}
	}
	
	public void explode()
	{
		this.explode(this.owner);
	}
	
	public void explode(PVPPlayer exploder)
	{
		if(exploded) return;
		exploded = true;
		if(this.block != null)
		{
			if(this.block.getType().equals(Material.LAPIS_ORE))
			{
				match.createExplosion(exploder, this.block.getLocation(), Main.config.claymore.getBlastPower(this.match.getWorld()), "C4");
				if(damageEnviron)
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
		if(this.item != null)
		{
			Location loc = this.item.getLocation().clone();
			EntitySyncCalls.removeEntity(item);
			match.createExplosion(exploder, loc, Main.config.claymore.getBlastPower(this.match.getWorld()), this.getLocName());
		}
	}
	
	public String getLocName()
	{
		return TobleMiner.MineFight.Main.gameEngine.dict.get("c4");
	}
}

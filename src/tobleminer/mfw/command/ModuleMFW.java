package tobleminer.mfw.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import tobleminer.mfw.Main;
import tobleminer.mfw.permission.Permission;
import tobleminer.minefight.command.CommandHelp;
import tobleminer.minefight.command.module.CommandModule;

public class ModuleMFW extends CommandModule
{
	private final Main main;
	
	public ModuleMFW(Main m)
	{
		this.main = m;
	}
	
	@Override
	public boolean handleCommand(String[] args, CommandSender sender)
	{
		if(args.length >= 1)
		{
			if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl"))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					if(!Main.cmdapi.hasPlayerPermission(p, Permission.MPVP_MFW_RELOAD.toString()))
					{
						p.sendMessage(this.noPermMsg);
						return true;
					}
				}
				this.main.reload();
				sender.sendMessage(ChatColor.DARK_GREEN + tobleminer.minefight.Main.gameEngine.dict.get("configrl"));				
				return true;
			}
		}
		return false;
	}

	@Override
	public String getName()
	{
		return "mfw";
	}

	@Override
	public CommandHelp getHelp(String cmd)
	{
		for(CommandHelp help : CommandMFWAdmin.values())
			if(help.getCmd().equalsIgnoreCase(cmd))
				return help;
		return null;
	}
	
	@Override
	public CommandHelp[] getHelp()
	{
		return CommandMFWAdmin.values();
	}
	
	private enum CommandMFWAdmin implements CommandHelp
	{
		RELOAD("mfw", "reload", 0, 0, "cmdDescrMFWReload", "/mpvp mfw reload", Permission.MPVP_MFW_RELOAD.toString());

		public final String module;
		public final String cmd;
		public final int argnumMin;
		public final int argnumMax;
		private final String descr;
		public final String perm;
		public final String syntax;
		
		CommandMFWAdmin(String module, String cmd, int argnumMin,int argnumMax, String descr, String syntax, String perm)
		{
			this.module = module;
			this.cmd = cmd;
			this.argnumMin = argnumMin;
			this.argnumMax = argnumMax;
			this.syntax = syntax;
			this.descr = descr;
			this.perm = perm;
		}

		@Override
		public String getCmd()
		{
			return cmd;
		}
		
		@Override
		public String getModule()
		{
			return module;
		}

		@Override
		public int argMin() 
		{
			return argnumMin;
		}

		@Override
		public int argMax()
		{
			return argnumMax;
		}

		@Override
		public String getDescr() 
		{
			return tobleminer.minefight.Main.gameEngine.dict.get(descr);
		}

		@Override
		public String getPermission()
		{
			return perm;
		}

		@Override
		public String getSyntax()
		{
			return syntax;
		}
	}
}

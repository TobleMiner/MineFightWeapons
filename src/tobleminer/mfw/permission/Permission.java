package tobleminer.mfw.permission;

public enum Permission
{
	MPVP_MFW_RELOAD("MineFightWeapons.admin.reload");

	private final String perm;

	private Permission(String perm)
	{
		this.perm = perm;
	}

	@Override
	public String toString()
	{
		return this.perm;
	}
}

package lilliputian.capabilities;

import net.minecraft.nbt.NBTTagCompound;

public interface ISizeCapability {
	
	public float getScale();
	
	public void setScale(float scale);

	public NBTTagCompound saveNBT();
	
	public void loadNBT(NBTTagCompound compound);

}

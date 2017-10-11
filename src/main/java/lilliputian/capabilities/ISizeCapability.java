package lilliputian.capabilities;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

public interface ISizeCapability {
	
	public float getBaseSize();
	public float getScale();
	public void setBaseSize(float baseSize);
	public void setScale(float scale);
	public void setScaleNoMorph(float scale);
	public float getActualSize();
	public float getActualScale();
	public float getActualScaleNoClamp();
	public void setActualScale(float actualScale);
	public float getPrevScale();
	
	public int getMorphTime();
	public int getMaxMorphTime();
	public void setMorphing();
	public void incrementMorphTime();

	public NBTTagCompound saveNBT();
	public void loadNBT(NBTTagCompound compound);
	
}

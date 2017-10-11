package lilliputian.capabilities;

import lilliputian.util.EntitySizeUtil;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

public class DefaultSizeCapability implements ISizeCapability {

	private float baseSize = 1F;
	private float scale = 1F;
	private float actualScale = 1F;
	private float prevScale = 1F;
	
	private int morphTime = 0;
	
	public DefaultSizeCapability() {
		
	}
	
	public DefaultSizeCapability(float baseSize) {
		this.baseSize = baseSize;
	}
	
	@Override
	public float getBaseSize() {
		return this.baseSize;
	}

	@Override
	public float getScale() {
		return this.scale;
	}

	@Override
	public void setBaseSize(float baseSize) {
		if (this.baseSize != baseSize) {
			this.baseSize = MathHelper.clamp(baseSize, EntitySizeUtil.HARD_MIN, EntitySizeUtil.HARD_MAX);
		}
	}

	@Override
	public void setScale(float scale) {
		if (this.scale != scale) {
			//System.out.println(this.scale + " != " + scale);
			this.prevScale = this.scale;
			//this.scale = MathHelper.clamp(scale, EntitySizeUtil.HARD_MIN / this.baseSize, EntitySizeUtil.HARD_MAX / this.baseSize);
			this.scale = scale;
			this.setMorphing();
		}
	}
	
	@Override
	public void setScaleNoMorph(float scale) {
		if (this.scale != scale) {
			//System.out.println(this.scale + " != " + scale);
			this.prevScale = this.scale;
			//this.scale = MathHelper.clamp(scale, EntitySizeUtil.HARD_MIN / this.baseSize, EntitySizeUtil.HARD_MAX / this.baseSize);
			this.scale = scale;
			this.actualScale = this.scale;
		}
	}
	
	@Override
	public float getActualSize() {
		return this.getActualScale() * this.baseSize;
	}
	
	@Override
	public float getActualScale() {
		//return this.actualScale;
		return MathHelper.clamp(this.actualScale, EntitySizeUtil.HARD_MIN / this.baseSize, EntitySizeUtil.HARD_MAX / this.baseSize);
	}
	
	@Override
	public float getActualScaleNoClamp() {
		return this.actualScale;
	}
	
	@Override
	public void setActualScale(float actualScale) {
		this.actualScale = actualScale;
	}
	
	@Override
	public float getPrevScale() {
		return this.prevScale;
	}

	@Override
	public int getMorphTime() {
		return this.morphTime;
	}
	
	@Override
	public int getMaxMorphTime() {
		return 20;
	}

	@Override
	public void setMorphing() {
		this.morphTime = this.getMaxMorphTime();
	}
	
	@Override
	public void incrementMorphTime() {
		if (this.morphTime > 0) {
			this.morphTime--;
		}
		if (this.morphTime < 0) {
			this.morphTime = 0;
		}
	}

	@Override
	public NBTTagCompound saveNBT() {
		return (NBTTagCompound) SizeCapabilityStorage.storage.writeNBT(SizeProvider.sizeCapability, this, null);
	}

	@Override
	public void loadNBT(NBTTagCompound compound) {
		SizeCapabilityStorage.storage.readNBT(SizeProvider.sizeCapability, this, null, compound);
	}

}

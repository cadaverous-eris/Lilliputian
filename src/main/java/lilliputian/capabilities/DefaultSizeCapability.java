package lilliputian.capabilities;

import lilliputian.util.EntitySizeUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

public class DefaultSizeCapability implements ISizeCapability {

	private float scale = 1F;
	
	public DefaultSizeCapability() {
		
	}
	
	public DefaultSizeCapability(float scale) {
		scale = MathHelper.clamp(scale, EntitySizeUtil.HARD_MIN, EntitySizeUtil.HARD_MAX);
		this.scale = scale;
	}

	@Override
	public float getScale() {
		return this.scale;
	}
	
	@Override
	public void setScale(float scale) {
		scale = MathHelper.clamp(scale, EntitySizeUtil.HARD_MIN, EntitySizeUtil.HARD_MAX);
		if (this.scale != scale) {
			this.scale = scale;
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

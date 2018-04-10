package lilliputian.capabilities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class SizeProvider implements ICapabilitySerializable<NBTTagCompound> {
	
	private ISizeCapability capabilitySize = null;
	
	public SizeProvider() {
		this.capabilitySize = new DefaultSizeCapability();
	}
	
	public SizeProvider(ISizeCapability capability) {
		this.capabilitySize = capability;
	}
	
	@CapabilityInject(ISizeCapability.class)
	public static final Capability<ISizeCapability> sizeCapability = null;
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == sizeCapability;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (sizeCapability != null && capability == sizeCapability) {
			return (T) capabilitySize;
		}
		return null;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return capabilitySize.saveNBT();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		capabilitySize.loadNBT(nbt);
	}

}

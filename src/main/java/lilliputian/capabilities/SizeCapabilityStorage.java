package lilliputian.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class SizeCapabilityStorage implements IStorage<ISizeCapability> {

	public static final SizeCapabilityStorage storage = new SizeCapabilityStorage();
	
	@Override
	public NBTBase writeNBT(Capability<ISizeCapability> capability, ISizeCapability instance, EnumFacing side) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setFloat("scale", instance.getScale());
		tag.setFloat("base_size", instance.getBaseSize());
		return tag;
	}

	@Override
	public void readNBT(Capability<ISizeCapability> capability, ISizeCapability instance, EnumFacing side,
			NBTBase nbt) {
		if (nbt instanceof NBTTagCompound) {
			NBTTagCompound tag = (NBTTagCompound) nbt;
			if (tag.hasKey("scale", 5)) {
				instance.setScale(tag.getFloat("scale"));
				instance.setActualScale(tag.getFloat("scale"));
			}
			if (tag.hasKey("base_size", 5)) {
				instance.setBaseSize(tag.getFloat("base_size"));
			}
		}
	}

}

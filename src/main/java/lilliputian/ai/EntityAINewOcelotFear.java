package lilliputian.ai;

import lilliputian.util.EntitySizeUtil;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAINewOcelotFear extends EntityAIAvoidEntity<EntityPlayer> {

	protected EntityOcelot ocelot;
	
	public EntityAINewOcelotFear(EntityOcelot ocelot, Class<EntityPlayer> classToAvoidIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn) {
		super(ocelot, classToAvoidIn, avoidDistanceIn, farSpeedIn, nearSpeedIn);
		this.ocelot = ocelot;
	}

	@Override
	public boolean shouldExecute() {
		if (super.shouldExecute()) {
			return !this.ocelot.isTamed() && EntitySizeUtil.getEntityScale(this.closestLivingEntity) > EntitySizeUtil.TINY_THRESHOLD;
		}
		return false;
	}

}

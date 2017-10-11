package lilliputian.ai;

import lilliputian.util.EntitySizeUtil;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAIHuntTinyCreatures extends EntityAINearestAttackableTarget {

	public EntityAIHuntTinyCreatures(EntityCreature creature) {
		super(creature, EntityLivingBase.class, false);
	}
	
	@Override
	public boolean shouldExecute() {
		if (super.shouldExecute()) {
			if (this.targetEntity != null) {
				return this.targetEntity.width < this.taskOwner.width && EntitySizeUtil.getEntityScale(this.targetEntity) <= EntitySizeUtil.TINY_THRESHOLD && (this.targetEntity instanceof EntityPlayer || this.targetEntity instanceof EntityCreature);
			}
		}
		return false;
	}
	
}

package lilliputian.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import lilliputian.capabilities.ISizeCapability;
import lilliputian.capabilities.SizeProvider;
import lilliputian.handlers.EntitySizeHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSizeChange implements IMessage {

	public float baseSize = 1F;
	public float scale = 1F;
	public int entityID = 0;
	public boolean morph = true;

	public MessageSizeChange() {

	}

	public MessageSizeChange(float baseSize, float scale, int id) {
		this.baseSize = baseSize;
		this.scale = scale;
		this.entityID = id;
		this.morph = true;
	}

	public MessageSizeChange(float baseSize, float scale, int id, boolean morph) {
		this.baseSize = baseSize;
		this.scale = scale;
		this.entityID = id;
		this.morph = morph;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.baseSize = buf.readFloat();
		this.scale = buf.readFloat();
		this.entityID = buf.readInt();
		this.morph = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeFloat(baseSize);
		buf.writeFloat(scale);
		buf.writeInt(entityID);
		buf.writeBoolean(this.morph);
	}

	public static class MessageHolder implements IMessageHandler<MessageSizeChange, IMessage> {

		@Override
		public IMessage onMessage(MessageSizeChange message, MessageContext ctx) {
			if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null) {
				
			} else {
				Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.entityID);

				if (entity != null && entity.hasCapability(SizeProvider.sizeCapability, null)) {
					ISizeCapability size = entity.getCapability(SizeProvider.sizeCapability, null);

					if (size.getBaseSize() != message.baseSize) {
						size.setBaseSize(message.baseSize);
					}
					if (size.getScale() != message.scale) {
						if (message.morph) {
							size.setScale(message.scale);
						} else {
							size.setScaleNoMorph(message.scale);
						}
					}
				}
			}
			return null;
		}

	}

}

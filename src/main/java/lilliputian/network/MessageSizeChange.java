package lilliputian.network;

import io.netty.buffer.ByteBuf;
import lilliputian.capabilities.ISizeCapability;
import lilliputian.capabilities.SizeProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSizeChange implements IMessage {

	public float scale = 1F;
	public int entityID = 0;

	public MessageSizeChange() {

	}

	public MessageSizeChange(float scale, int id) {
		this.scale = scale;
		this.entityID = id;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.scale = buf.readFloat();
		this.entityID = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeFloat(scale);
		buf.writeInt(entityID);
	}

	public static class MessageHolder implements IMessageHandler<MessageSizeChange, IMessage> {

		@Override
		public IMessage onMessage(MessageSizeChange message, MessageContext ctx) {
			if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null) {
				
			} else {
				Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.entityID);

				if (entity != null && entity.hasCapability(SizeProvider.sizeCapability, null)) {
					ISizeCapability size = entity.getCapability(SizeProvider.sizeCapability, null);
					size.setScale(message.scale);
				}
			}
			return null;
		}

	}

}

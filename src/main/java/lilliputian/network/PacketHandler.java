package lilliputian.network;

import lilliputian.Lilliputian;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
	
	public static SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Lilliputian.MODID);
	
	private static int id = 0;
    
    public static void registerMessages() {
    	INSTANCE.registerMessage(MessageSizeChange.MessageHolder.class, MessageSizeChange.class, id++, Side.CLIENT);
    }

}

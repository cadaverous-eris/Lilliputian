package lilliputian;

import java.io.File;

import lilliputian.capabilities.DefaultSizeCapability;
import lilliputian.capabilities.ISizeCapability;
import lilliputian.capabilities.SizeCapabilityStorage;
import lilliputian.commands.CommandResetSize;
import lilliputian.commands.CommandSetBaseSize;
import lilliputian.network.PacketHandler;
import lilliputian.proxy.CommonProxy;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = Lilliputian.MODID, name = Lilliputian.NAME, version = Lilliputian.VERSION, dependencies = Lilliputian.DEPENDENCIES)
public class Lilliputian {
	public static final String MODID = "lilliputian";
	public static final String NAME = "Lilliputian";
	public static final String VERSION = "0.0.1";
	public static final String DEPENDENCIES = "";

	@SidedProxy(clientSide = "lilliputian.proxy.ClientProxy", serverSide = "lilliputian.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	@Mod.Instance
	public static Lilliputian instance;
	
	public static Configuration config;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		CapabilityManager.INSTANCE.register(ISizeCapability.class, new SizeCapabilityStorage(), DefaultSizeCapability.class);
		PacketHandler.registerMessages();
		
		File directory = event.getModConfigurationDirectory();
        config = new Configuration(new File(directory.getPath(), "lilliputian.cfg"));
        Config.readConfig();
		
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		if (config.hasChanged()) {
            config.save();
        }
		Config.postInit();
		
		proxy.postInit(event);
	}
	
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandSetBaseSize());
		event.registerServerCommand(new CommandResetSize());
	}
	
}

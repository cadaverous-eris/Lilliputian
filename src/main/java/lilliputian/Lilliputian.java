package lilliputian;

import java.io.File;

import lilliputian.capabilities.DefaultSizeCapability;
import lilliputian.capabilities.ISizeCapability;
import lilliputian.capabilities.SizeCapabilityStorage;
import lilliputian.network.PacketHandler;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Lilliputian.MODID, name = Lilliputian.NAME, version = Lilliputian.VERSION, dependencies = Lilliputian.DEPENDENCIES)
public class Lilliputian {
	
	public static final String MODID = "lilliputian";
	public static final String NAME = "Lilliputian";
	public static final String VERSION = "1.0";
	public static final String DEPENDENCIES = "";
	
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
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		Config.postInit();
		if (config.hasChanged()) {
            config.save();
        }
	}
	
}

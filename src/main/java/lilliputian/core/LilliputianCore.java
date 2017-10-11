package lilliputian.core;

import java.util.Arrays;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class LilliputianCore extends DummyModContainer {

	public LilliputianCore() {
		super(new ModMetadata());
		ModMetadata meta = getMetadata();
		meta.modId = "lilliputiancore";
		meta.name = "Lilliputian Core";
		meta.version = "0.1";
		meta.credits = "";
		meta.authorList = Arrays.asList("mangoose");
		meta.description = "Coremod used to fix some issues with resizing";
		meta.screenshots = new String[0];
		meta.logoFile = "";
	}
	
	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		bus.register(this);
		return true;
	}
	
	@Subscribe
	public void modConstruction(FMLConstructionEvent event){
		event.getSide();
	}
	
	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		
	}
	
	@Subscribe
	public void init(FMLInitializationEvent event) {
	
	}
	
	
	@Subscribe
	public void postInit(FMLPostInitializationEvent event) {
	
	}
	
}

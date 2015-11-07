package pl.asie.technotronics;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

import pl.asie.technotronics.conveyor.BlockConveyorBelt;
import pl.asie.technotronics.conveyor.TileConveyorBelt;
import pl.asie.technotronics.conveyor.TravellingObject;
import pl.asie.technotronics.conveyor.TravellingObjectItemStack;
import pl.asie.technotronics.utils.network.NetworkChannelHandler;
import pl.asie.technotronics.conveyor.PacketConveyorBelt;
import pl.asie.technotronics.utils.network.PacketHandler;

@Mod(modid = Technotronics.MODID, version = Technotronics.VERSION)
public class Technotronics {
    @SidedProxy(clientSide = "pl.asie.technotronics.ClientProxy", serverSide = "pl.asie.technotronics.CommonProxy")
    public static CommonProxy proxy;

    public static final Set<Class<? extends TileEntity>> HOPPERS = new HashSet<Class<? extends TileEntity>>();
    public static final String MODID = "Technotronics";
    public static final String VERSION = "1.0";
    public static final Random RANDOM = new Random();
    public static NetworkChannelHandler network;
    public static EnumMap<Side,FMLEmbeddedChannel> channels;

    public static BlockConveyorBelt conveyorBelt;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        conveyorBelt = new BlockConveyorBelt();
        GameRegistry.registerBlock(conveyorBelt, "conveyorBelt");

        TravellingObject.CLASS_MAP.put(1, TravellingObjectItemStack.class);
        HOPPERS.add(TileEntityHopper.class);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        network = new NetworkChannelHandler();
        channels = NetworkRegistry.INSTANCE.newChannel("Redux2", network, new PacketHandler());
        network.addDiscriminator(0, PacketConveyorBelt.class);

        proxy.initRenderers();
        GameRegistry.registerTileEntity(TileConveyorBelt.class, "conveyorBelt");
    }
}

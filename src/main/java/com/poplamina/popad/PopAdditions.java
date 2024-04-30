package com.poplamina.popad;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.*;
import org.slf4j.Logger;

import java.util.function.Supplier;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(PopAdditions.MODID)
public class PopAdditions
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "popad";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Creates a new Block with the id "examplemod:example_block", combining the namespace and path
    public static final DeferredBlock<Block> POPLAMINA_BLOCK = BLOCKS.register("poplamina_block", () -> new Block(BlockBehaviour.Properties.of()
            .destroyTime(2.0f)
            .friction(0.99f)
            .lightLevel(state -> 7)
            .destroyTime(0.4f)
    ));
    //public static final DeferredBlock<Block> MY_BLOCK = BLOCKS.register("my_block", () -> new Block(...));
    // Creates a new BlockItem with the id "examplemod:example_block", combining the namespace and path
    public static final DeferredItem<BlockItem> POPLAMINA_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("poplamina_block", POPLAMINA_BLOCK);

    // Creates a new food item with the id "examplemod:example_id", nutrition 1 and saturation 2
    public static final DeferredItem<Item> EATABLE_POPLAMINA = ITEMS.registerSimpleItem("eatable_poplamina", new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEat().nutrition(1).saturationMod(2f).build()));

    public static final DeferredItem<Item> COOKED_POPLAMINA = ITEMS.registerSimpleItem("cooked_poplamina", new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEat().nutrition(9999).saturationMod(2f).build()));


    public class PoplaminaEntityType
    {
        public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, PopAdditions.MODID);
        public static final DeferredHolder<EntityType<?>, EntityType<Poplamina_Entity>> POPLAMINA = ENTITIES.register("poplamina", () -> EntityType.Builder.of(Poplamina_Entity::new, MobCategory.MISC).sized(0.6F, 1.90F).setShouldReceiveVelocityUpdates(true).build(PopAdditions.MODID + "poplamina"));


        public static class Poplamina_Entity extends Zombie {
            public Poplamina_Entity(EntityType<? extends Poplamina_Entity> type, Level world) {
                super(type, world);
            }
        }
    }


   public static final DeferredHolder<Item, DeferredSpawnEggItem> POPLAMINA_SPAWN_EGG = ITEMS.register("poplamina_spawn_egg", () -> new DeferredSpawnEggItem(PoplaminaEntityType.POPLAMINA, 5651507, 9804699, new Item.Properties()));
        // public static InteractionHand getHandWith(LivingEntity livingEntity, Predicate<Item> itemPredicate) {
        //    return itemPredicate.test(livingEntity.getMainHandItem().getItem()) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;


    // Creates a creative tab with the id "examplemod:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.popad")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> EATABLE_POPLAMINA.get().getDefaultInstance())
           .displayItems((parameters, output) -> {
                output.accept(EATABLE_POPLAMINA.get());
                output.accept(POPLAMINA_BLOCK.get());
                output.accept(COOKED_POPLAMINA.get());
                //output.accept(POPLAMINA_SPAWN_EGG.get());
                // Add the example item to the tab. For your own tabs, this method is preferred over the event
            }).build());

    //public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, PopAdditions.MODID);
    //public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, PopAdditions.MODID);
    //public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PopAdditions.MODID);
    //public static final Supplier<Block> POPLAMINA = BLOCKS.register("Poplamina", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    //public static final Supplier<Item> POPLAMINA_ITEM = ITEMS.register("Poplamina", () -> new BlockItem(POPLAMINA.get(), new Item.Properties()));

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public PopAdditions(IEventBus modEventBus)
    {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        //ENTITIES.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (PopAdditions) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        // modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // Add the example block item to the building blocks tab
    // private void addCreative(BuildCreativeModeTabContentsEvent event)
    // {
    //     if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
    //         event.accept(EXAMPLE_BLOCK_ITEM);
    // }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}

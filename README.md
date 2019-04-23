# How to use mccommons-inventories-[platform]

Maven dependency for spigot:
```xml
<dependency>
     <groupId>de.exceptionflug</groupId>
     <artifactId>mccommons-inventories-spigot</artifactId>
     <version>2.0-SNAPSHOT</version>
     <scope>provided</scope>
</dependency>
```
Maven dependency for proxy:
```xml
<dependency>
     <groupId>de.exceptionflug</groupId>
     <artifactId>mccommons-inventories-proxy</artifactId>
     <version>2.0-SNAPSHOT</version>
     <scope>provided</scope>
</dependency>
```
# Design proxy-side inventories with MCCommons
MCCommons works hand in hand with Protocolize. This is one of the biggest advantages of MCCommons. You can easily develop inventories which can be easily ported to Spigot or BungeeCord and this by using a similar api.
# InventoryWrapper
MCCommons wraps platform dependent inventory objects inside of its wrapper classes. Then you are developing an inventory, you will only interact with InventoryWrapper instances. There several types of InventoryWrappers for special use cases.

| Base type                                   | Description                                                                                                                      |
|---------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------|
| OnePageInventoryWrapper                     | A simple inventory which has no special abilities.                                                                               |
| MultiPageInventoryWrapper                   | A dynamic inventory which handles forward and backward items when multiple pages are needed.                                     |
| AbstractAsyncFetchMultiPageInventoryWrapper | Similar to MultiPageInventoryWrapper. This implementation is designed for massive data loads taking a while to fetch or process. |
# Platform dependent implementations
| Wrapper                                   | Spigot                                          | BungeeCord                                     |
|-------------------------------------------|-------------------------------------------------|------------------------------------------------|
| OnePageInventoryWrapper                   | SpigotOnePageInventoryWrapper                   | ProxyOnePageInventoryWrapper                   |
| MultiPageInventoryWrapper                 | SpigotMultiPageInventoryWrapper                 | ProxyMultiPageInventoryWrapper                 |
| AbstractAsyncFetchMultiPageInventoryWrapper | AbstractSpigotAsyncFetchMultiPageInventoryWrapper | AbstractProxyAsyncFetchMultiPageInventoryWrapper |

# A simple OnePageInventoryWrapper
This is a simple skeleton of an OnePageInventoryWrapper on Spigot:
```java
public class SkeletonWrapper extends SpigotOnePageInventoryWrapper {
    
    private static final ConfigWrapper CONFIG = ConfigFactory.create("plugin-name-goes-here", SkeletonWrapper.class);
    
    public SkeletonWrapper(final Player player) {
        super(player, CONFIG);
    }
    
}
```
The ConfigWrapper is used to describe the layout and design of the inventory itself. The goal is to develop a customizeable and multilangual inventory. MCCommons will automatically chose the correct language for that player. You can get it by using the `getLocale()` getter.
# ActionHandler
One of the big differences between the me.xkuyax inventory library and MCCommons is the usage of so called ActionHandlers. An ActionHandler is a defined algorithm which can be called independently. Items are using ActionHandler to do some logic when clicking on it. ActionHandlers are registered by overriding the method `registerActionHandlers()`. In it's body you can register an ActionHandler using the method `registerActionHandler(String name, ActionHandler handler)`. Here is an example for opening another proxy-side inventory:
```java
registerActionHandler("browseFavorites", (click) -> {
    InventoryModule.sendInventory(getPlayer(), new PlayerReplayOverviewInventoryWrapper(getPlayer(), getPlayer().getUniqueId()).buildFirstPage());
    return CallResult.DENY_GRABBING;
});
```
The CallResult can be DENY_GRABBING or ALLOW_GRABBING. ALLOW_GRABBING allows the player to pick up the clicked item.
# Modifying the wrapper
You are able to create items by using the configuration file, but sometimes it is needed to create items by code. This can be achieved by using the following methods:
- `set(int slot, ItemStack stack, String actionHandlerName)`
- `set(int slot, ConfigItemStack stack, String actionHandlerName, Arguments args)`
- `set(int slot, ConfigItemStack stack, String actionHandlerName)`
- `add(ItemStack stack, String actionHandlerName, Arguments args)`
- `add(ItemStack stack, String actionHandlerName)`
- `add(ConfigItemStack stack, String actionHandlerName, Arguments args)`
- `add(ConfigItemStack stack, String actionHandler)`

The difference between `set()` and `add()` is in the specification of a slot. When using `add()`, the item will be placed in the next free slot.
The required `actionHandlerName` is the name specified while registering an ActionHandler (see above). When no action should occurr, you can set the `actionHandlerName` to `"noAction"`.
## The updateInventory routine
MCCommons updates an inventory quite often. The first call to `updateInventory` occurrs with initialization of the wrapper. The `updateInventory` method is used to place config defined items and placeholders into the inventory. You can override it to place own items on every update.

**NOTE:** Adding items by using `add()` is not recommended in `updateInventory` since this will add a new item to the next free slot every time an update occurrs.
Here is an example of using the method:
```java
@Override
public void updateInventory() {
    super.updateInventory(); // Add config defined items
    final ItemStack itemStack = Converters.convert(CONFIG.getItemStack("Custom.toggleSharing.itemStack", getLocale()), ItemStack.class); // Getting a ConfigItemStack which will be converted to a platform dependent ItemStack
    try {
        final FriendSettings settings = FILIA_PROXY_PLUGABLE.getFiliaClient().getFriendSettingsApi().getSettings(getPlayer().getUniqueId());
        if(settings.getOrDefault("share-replays", "true").equalsIgnoreCase("true")) {
            ItemUtils.addGlow(itemStack); // Adding the glow effect to the item if the player shares replays with it's friends
        }
    } catch (ApiException e) {
        e.printStackTrace();
    }
    set(CONFIG.getOrSetDefault("Custom.toggleSharing.slot", 0), itemStack, "toggleSharing"); // Finally set the item to the config defined slot. Clicking on the item will fire the ActionHandler "toggleSharing"
}
```

## Modifying the inventory not on update
Sometimes you need to make changes to the inventory after a special delay has been completed or the player clicks on an item. In this situations you need to rebuild the inventory manually so the changes take effect. For example:
```java
registerActionHandler("doSomething", click -> {
    click.getInventoryItem().getItemStackWrapper().setType(ItemType.GREEN_BANNER);
    build();
    later(() -> {
        click.getInventoryItem().getItemStackWrapper().setType(ItemType.RED_BANNER);
        build();
    }, 3, TimeUnit.SECONDS);
    return CallResult.DENY_GRABBING;
});
```
In this example the clicked item get it's type changed. After 3 seconds the item will be changed again.
# Converters
MCCommons achieve it's platform independency by using a high command abstraction layer. So MCCommons handles internal wrapper objects for holding platform dependent types. Here I will go into detail about the differences of `ConfigItemStack`, `ItemStackWrapper` and the platform dependent `ItemStack`.
## ConfigItemStack
To provide uniform item deserialization across different platforms, there is the `ConfigItemStack` data class. It holds plain information about an item. You can convert a `ConfigItemStack` to a platform dependent equivalent by using `Converters.convert(myConfigItemStack, ItemStack.class)`.
## ItemStackWrapper
To provide uniform item data access across different platforms, there is the `ItemStackWrapper` interface. This interface is implemented differently on Spigot (`SpigotItemStackWrapper`) and BungeeCord (`ProtocolizeItemStackWrapper`). The ItemStackWrapper serves the following methods:
- `getDisplayName(): String`
- `getLore(): List<String>`
- `getType(): ItemType`
- `getNBT(): CompoundTag`
- `getAmount(): int`
- `getDurability(): short`
- `getHandle(): Object`
- `setType(ItemType type)`
- `setDisplayName(String name)`
- `setLore(List<String> lore)`
- `setNBT(CompoundTag tag)`
- `setAmount(int amount)`
- `setDurability(short durability)`

You may use this when working with `InventoryItem` because this class is not implemented differently on other platforms. Wrapping a platform dependent ItemStack is quite easy by using `Converters.convert(itemStack, ItemStackWrapper.class)`. The same logic to unwrap a ItemStackWrapper: `Converters.convert(itemStackWrapper, ItemStack.class)`
## Platform dependent ItemStack
This is the bare bones implementation of the used backing library (Bukkit / Protocolize). MCCommons supports the usage of platform dependent ItemStacks in InventoryWrappers.

# InventoryItems
Don't compare a normal ItemStack or it's abstractions with an `InventoryItem`! The `InventoryItem` describes exactly one placed item inside of the wrapper. It stores information about the ActionHandler which will be called when the item gets clicked, its arguments defined by the user and of course the information of the item itself represented as a `ItemStackWrapper` instance. There is a extension of this class. The `DataInventoryItem<T>` stores additionally one instance of `T` (used for async fetch inventories).

# ItemType
MCCommons also uses an own implementation of item types. This implementation is directly taken from Protocolize and is now also working on Spigot. The reason for this is to represent the item type as an enum across multiple platforms.
 
**NOTE:** MCCommons `ItemType` is not directly compatible with the Protocolize equivalent since Protocolize is only on the BungeeCord platform installed. You can convert the MCCommons `ItemType` to its platform dependent equivalents `MaterialData` and `ItemType` (Protocolize) and vice versa using the `Converters` class.

**NOTE:** ItemType stores more data then a Bukkit `MaterialData` does. Then working with spawn eggs there will likely be an exception thrown during the conversion process due to missing information when converting a `MaterialData` instance to MCCommons `ItemType`.

**NOTE:** ItemType contains all new item types till 1.13. You can only use item types which are present on your server version or there will likely be conversion issues then converting a `ItemType.TURTLE_EGG` to `MaterialData` on a spigot 1.8-R3 server.

# InventoryType
Like the ItemType there is also an own implementation for inventory types. This is also taken directly from Protocoloize with slight modifications making it uncompatbible with Protocolize. But there is no problem then converting objects of this type to a Bukkit or Protocolize `InventoryType`.

## Specifying the type of an inventory
You can specify the type by code or by using the configuration file of the wrapper. You can also switch between `InventoryType`s while the wrapper is opened.
For example (this time a proxy example :P):
```java
public ExampleWrapper(ProxiedPlayer player) {
    super(player, InventoryType.HOPPER, CONFIG);
}
```
or by setting it afterwards:
```java
setInventoryType(InventoryType.HOPPER);
build(); // Don't forget to rebuild the inventory after setting the type
```

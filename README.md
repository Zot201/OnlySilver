
OnlySilver
==========

A mod which primarily implements the old silver from SimpleOres. New features may be added for better overall experience or mod compatibility.

## Add-ons and Compatibility

OnlySilver API is accessible under the `zotmc.onlysilver.api` package. Please note that any class that is not placed under this package is considered internal and may be changed from time to time.

You may access the `dev` jar together with source code by adding the following to the `build.gradle` of your projects:

```groovy
repositories {
    maven { url 'https://dl.bintray.com/zot201/minecraft' }
}

dependencies {
    compile 'io.github.zot201.onlysilver:onlysilver:2.2.6-1.9.4:dev'
}
```

## Features

* Base Items and Blocks

  * Ore
  * Ingot
  * (Compressed) Block
  * Rod
    * Cost two ingots each

* Material Definitions

  * Tool Parameters
  * Armor Parameters

* Equipment

  * Vanilla Tool Set: Pickaxe, Axe, Shovel, Sword and Hoe
  * Vanilla Armor Set: Helmet, Chestplate, Leggings and Boots
  * Silver Bow
    * Dual ranged and melee knockback effect
    * Material logic applied: enchantability, repairing material, etc.
    * Crafted from rods, strings and an iron ingot

* Enchantments

  * Incantation
    * Silver tools only
    * "Incant" mob drops
    * Export Silver Aura to non-silver tools
  * Silver Aura
    * Obtained at enchanting table only with silver items
    * All-round boosts proportional to enchantability
    * Item entity invulnerability except to void damage

* Mobs

  * Silver Golem
    * Tiny and fast moving
    * Immune to poison
    * Crafted from a silver block and a pumpkin / lantern
      * Dispenser support is *mandatory*
    * Aspect definition for Thaumcraft
  * Skeleton Compatibility for Silver Bow
    * Enable them to shoot with silver bows
    * Dedicated handling of ranged knockback effect
      * Silver Aura boosts maybe handled explicitly as well
  * Equipment Spawning on Mobs in Vanilla Fashion
    * Armor on any `EntityLiving` that calls `setEquipmentBasedOnDifficulty` on its spawn
    * A Sword or a bow on skeletons

* Ore Generation

  * Settings is stored per world, more specifically, in `WorldInfo#getAdditionalProperty`
  * Customizable at Mod Options or at 'Customized' World Type

* Mod Options

  * High customizability
  * Intuitive configuration UI
  * Capability to apply settings in-game without restart
  * Send and receive settings in multiplayer games

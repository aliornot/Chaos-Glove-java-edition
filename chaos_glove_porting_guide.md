# Chaos Glove & 3D Crosshair Porting Guide
## Minecraft Java Edition (Fabric 1.20.1, Mojang Mappings, Java 17)

This document is a comprehensive guide summarizing the entire state of the **Chaos Glove & 3D Crosshair** mod ported from Bedrock Edition to Java Edition Fabric 1.20.1. It describes the implemented features, planned boss mechanics, technical bugs resolved, and tips to ensure any developer (including AI assistants) can continue development smoothly without repeating past mistakes.

---

## 1. Implemented Features Summary

### 1.1. 3D Crosshair & HUD Indicators
* **3D Rotating Cube Crosshair**: The vanilla 2D crosshair is canceled. In its place, a 3D semi-transparent gray cube of size 8x8x8 pixels is rendered. It rotates smoothly over multiple axes using frame delta-time (`client.getFrameTime()`) for fluid 60+ FPS motion.
* **3D Cube Outline**: To make the 3D rotating structure readable, a crisp black outline (`DEBUG_LINES`) is drawn over the 12 edges of the cube.
* **Look-At Info Box**: Aiming at any block or living entity displays a dark, semi-transparent beveled window to the right of the crosshair (`width / 2 + 15`). It queries the target's name and displays it in Russian using the standard Mojang font with auto-translation and a drop shadow.
* **Mode HUD Text**: Holding the Chaos Glove displays the active mode (`ДЕМОН`, `АНГЕЛ`, `АНГЕЛ ХАОСА`, `НЕКРОМАНТ` or `НЕ ВЫБРАН`) centered at the top of the HUD (`Y = 10`).

---

### 1.2. Chaos Glove Item & Modes Selection
* **3D Gauntlet Model**: The glove is represented as a high-fidelity 3D model comprised of overlapping elements (wrist cuff, palm body, index, middle, ring, pinky, thumb, and spikes) textured with the original steel-gray shiny Bedrock texture. It is scaled and translated so it wraps around the player's fist, completely enclosing the player's arm and hiding any underlying skin or noise.
* **Attack Modifier**: Holding the glove in the main hand adds **+7.0 attack damage** (totaling 8.0 damage per hit, equivalent to a Netherite Sword).
* **Open Menu Key (G)**: Pressing the `G` key (Russian `П`) while holding the glove opens a custom 3D-styled screen.
* **Mode Selection GUI Panel**: 
  * Features a dark gray render panel, a 3D-shaded **Double Gold Border** (`0xFFD4AF37`), and **4 Purple Chaos Gems** (`0xFFB100CD`) rendered at the corners.
  * Contains buttons to select one of 4 modes: **Demon, Angel, Chaos Angel, and Necromancer**.

---

### 1.3. Active Modes, Abilities & Ultimates

#### 🔥 DEMON MODE (Red Charge Bar on Left HUD, max 100)
* **Attack Effect**: Sets target on fire for **8 seconds**.
* **Charge**: Attacks give +1, kills give +5.
* **Summon (50 Charges, ПКМ)**: Spawns **4 custom Red Vexes** with Iron Swords. They float behind the owner, teleport to them if >15 blocks away, and automatically attack the player's active targets or defend the player when hurt.
* **Ultimate (100 Charges, ПКМ)**: **Demon Form (100 seconds)**:
  * Scales the player's model (`2.5x` width, `1.66x` height) and physical hitbox (`1.5` wide, `3.0` high).
  * Automatically lifts the player's camera (Eye Height) to **2.55 blocks** (85% of 3-block height).
  * Doubles maximum health (+20 Max HP) and heals the player.
  * Gives **Strength II** and **Regeneration I**.
  * Shows a red countdown timer on the HUD. Smoothly cleanses and resets everything when finished.

#### ✨ ANGEL MODE (Blue Charge Bar on Left HUD, max 100)
* **Attack Effect**: Inflicts **Slowness I** on target for **8 seconds**.
* **Charge**: Attacks give +1, kills give +5.
* **Summon (75 Charges, ПКМ)**: Spawns **1 custom Gold Golem** (textured in shiny yellow gold) that follows the player and defends them in combat.
* **Ultimate (100 Charges, ПКМ)**: **Angel Form (100 seconds)**:
  * Grants creative-like flight in survival mode.
  * Gives **Speed II**.
  * **No Flying Block-Mining Slowdown**: Overrides vanilla block-breaking speed calculations so the player mines blocks instantly even while floating/flying.
  * Shows a blue countdown timer on the HUD. Safely disables flight in survival when finished.

#### 🌌 CHAOS ANGEL MODE (Purple Charge Bar on Left HUD, max 100)
* **Attack Effect**: Inflicts **Poison I** and **Glowing I** for **8 seconds**.
* **Charge**: Attacks give +1, kills give +5.
* **Ultimate (100 Charges, ПКМ)**: **Chaos Angel Form (60 seconds)**:
  * Grants **Resistance 255** (absolute invulnerability), **Speed III**, and **Jump Boost III**.
* **Static Black Hole (2 charges, 20s cooldown)**: Spawns an opaque black sphere (diameter 2 blocks) with a rotating purple accretion ring (Dragon Breath). Pulls all mobs within a **90x90 blocks** area through walls, teleports them to the center instantly when <=10 blocks away, and detonates after **7 seconds** with a lag-free staggered explosion of **175 TNT** force.
* **Flying Black Hole (50 Charges, ПКМ)**: Shoots a black hole projectile traveling at **2.5 blocks/second** up to **25 blocks**. It destroys all blocks on its path, drags hit mobs with it, and detonates at the end with a lag-free staggered explosion of **100 TNT** force.

#### 💀 NECROMANCER MODE (Dual Left HUD Bars: 0/25 Green & 0/75 Black)
* **Attack Effect**: Inflicts **Wither I** for **5 seconds**.
* **Charge**: Charges **only on kills**. Normal kills = +1, killing mobs with >15 Max HP (7.5 hearts) = **+2 charges**.
* **Ultimate (25 Charges, ПКМ)**: Spawns **3 custom Dark Green Wither Skeletons** that act as companion defenders.
* **Ultimate (75 Charges, ПКМ - Priority)**: Spawns **2 custom Dark Green Ravagers**. Grants the player and all active green companions **Regeneration II for 100 seconds**.

---

## 2. Planned Features (Boss Fight & Arena)

To complete the port, a custom dimension, portal, and boss fight must be implemented:

### 2.1. Dimension & Portal Mechanics
* **Portal Construction**: Built like a Nether Portal (vertical frame, minimum 4x5) made of **Iron Blocks**.
* **Portal Activation**: Ignited using **Flint and Steel** inside the frame. It fills the frame with `ChaosPortalBlock` (uses Nether Portal's animated purple texture).
* **The Chaos Dimension (`chaos_glove:chaos_dimension`)**:
  * A custom void dimension type that locks time to night (`18000`), disables beds, and features purple fog.
  * Spawns exactly **one circular flat island made of End Stone** of radius **42 blocks** and height 4 (from Y=76 to Y=79) centered at `(0, 76, 0)`.
  * **Void Spawning Platform**: Players spawn on a 3x3 Bedrock platform at `(-50, 80, 0)` looking at the island, creating a dramatic **7-block void gap** they must jump or build across.
  * **Exit Portal**: An active End-style portal made of Bedrock and custom `ChaosPortalBlock` sits in the center of the island `(0, 80, 0)`, teleporting players safely back to the Overworld spawn.
  * **Mob Spawning**: Natural monster spawning (like Endermen) is completely disabled inside this dimension.

### 2.2. The Boss: "Collapse Eater" (Пожиратель Коллапса) - Planned AI
* **Status**: 1000 Max HP, immune to knockback, fire damage, and fall damage.
* **Abilities**:
  1. *Gravity Pull*: Drags the player towards his center, trying to push them into the void.
  2. *Chaos Laser*: Shoots a thick purple beam that deals magic damage and inflicts blindness.
  3. *Summons*: Shield phase where he summons Red Vexes and Green Wither Skeletons, gaining invulnerability until they are defeated.
  4. *Enraged Mode*: Under 250 HP, fires lasers and mini-explosions rapidly.
* **Loot (Charged Stars)**: For every 250 damage taken (at 750, 500, 250 HP), the boss drops a **Charged Star** item. Picking it up plays a level-up sound, immediately consumes the star, and charges the glove's active mode scales to the maximum (+100 or +25/+75)!

---

## 3. Major Bugs & Pitfalls (For Continuing Developers/AI)

These are the most critical compile-time and runtime bugs encountered. **Do not repeat these errors!**

### ⚠️ Bug 1: Java Version Class Version Error (`class file version 65.0`)
* **Problem**: Standard Gradle build templates default to Java 21 compilation. However, PojavLauncher/FCL and standard 1.20.1 clients run on **Java 17**. Loading a Java 21 jar on Java 17 results in a crash on start: `UnsupportedClassVersionError (class file version 65.0, only recognizes up to 61.0)`.
* **Fix**: In `build.gradle`, strictly configure target compatibility and compilation release to Java 17:
  ```gradle
  tasks.withType(JavaCompile).configureEach {
      it.options.release = 17
  }
  java {
      sourceCompatibility = JavaVersion.VERSION_17
      targetCompatibility = JavaVersion.VERSION_17
  }
  ```

### ⚠️ Bug 2: Scoreboard Tags Client-Server Sync Failure
* **Problem**: Adding custom tags to entities (e.g. `vex.addTag("RedVex")`) on the server does **NOT** synchronize to the client automatically in vanilla Minecraft. Client-side Mixins trying to check `vex.getTags().contains("RedVex")` to apply custom textures (red Vex, gold Golem, green skeletons) will return `false`, rendering standard blue/gray vanilla textures.
* **Fix**: Use the **Custom Name** property, which automatically syncs to the client. Set the custom name of the helper on spawn and hide its visibility plate:
  ```java
  entity.setCustomName(Component.literal("RedVex")); // or "GoldGolem", "GreenWither", "GreenRavager"
  entity.setCustomNameVisible(false);
  ```
  In the client-side Mixin, simply check the synced name:
  ```java
  if (entity.hasCustomName() && "RedVex".equals(entity.getCustomName().getString())) { ... }
  ```

### ⚠️ Bug 3: Invalid Custom Dimension Datapack Schemas (OOM & World Gen Crash)
* **Problem**: 
  1. The field `"monster_spawn_light_test"` is obsolete in 1.20.1. Passing it causes datapack validation failure and prevents world loading. Use the `"monster_spawn_light_level"` object instead.
  2. In `minecraft:flat` world generators, using `"block": "minecraft:air"` as a flat layer is rejected by vanilla validation and crashes world loading.
* **Fix**: Use `#minecraft:infiniburn_end` with a leading hash `#` for the infiniburn tag, configure correct light providers, and use `minecraft:bedrock` (height 1) for the flat world air layer (the player spawns 80 blocks above it anyway, so it acts as an empty void).

### ⚠️ Bug 4: NullPointerExceptions on Early Player World Join
* **Problem**: The player constructor and world login triggers `getDimensions()` and `getStandingEyeHeight()` calls *before* the player's inventory or network handler is initialized. Calling `player.getItemInHand(...)` during this phase throws a `NullPointerException`, preventing players from joining their worlds.
* **Fix**: Ensure `DemonUltimateManager.isUltimateActive(player)` checks if `player.getInventory()` is null and wraps the entire logic inside a `try-catch` block:
  ```java
  if (player.getInventory() == null) return false;
  try { ... } catch (Throwable t) { return false; }
  ```

### ⚠️ Bug 5: Vanilla Flying Mining Speed Penalty
* **Problem**: Vanilla Minecraft divides mining speed by 5x while flying in survival mode, making blocks feel unbreakable during the Angel flight ultimate.
* **Fix**: Inject a mixin into `Player.getDestroySpeed(BlockState state)` and multiply the return value by `5.0f` if the Angel ultimate is active.

### ⚠️ Bug 6: EndPortalBlock Hardcoded Destination
* **Problem**: Stepping into a vanilla `EndPortalBlock` (`Blocks.END_PORTAL`) in any dimension other than the End will teleport the player to the End instead of the Overworld.
* **Fix**: Construct the return portal on the island using the mod's custom `ChaosPortalBlock`, which teleports the player back to the Overworld spawn point.

---

## 4. Coding & Architecture Best Practices

When building upon this codebase, adhere to these guidelines:
1. **Clean up Daemons**: When compiling inside sandboxed environments, Gradle daemons might hang or consume excessive memory, leading to Out of Memory (OOM) kills. Always stop daemons and run build tasks with `--no-daemon` and skip unnecessary source mapping:
   ```bash
   ./gradlew --stop && ./gradlew build -x remapSourcesJar -x sourcesJar --no-daemon
   ```
2. **Server-Side Authority**: Apply status effects, damage, summons, and attribute modifiers strictly on the server side (`!world.isClientSide()`). Use Minecraft's native networking packets (`ServerPlayNetworking`) to synchronize non-standard actions (like spawning client-side black hole particle emitters).
3. **Smooth Client Rendering**: Use client tick and frame delta-time (`client.getFrameTime()`) inside client render loops to interpolate rotations, animations, and particle orbits, keeping graphics fully fluid at high refresh rates.
4. **Coordinate Mapping Dividers**: Java Edition JSON models use a normalized `0-16` UV scale. When mapping 32x32 skin textures (like `chaos_glove_model.png`), divide the pixel coordinates by `2.0` in the JSON elements.

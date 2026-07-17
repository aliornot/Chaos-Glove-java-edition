# Chaos Glove Java Edition — Context Transfer Guide

**Purpose:** full handoff document for a **new clean chat / AI session**.  
Read this file first before changing the mod. Do **not** repeat the mistakes listed below.

**Date of this snapshot:** 2026-07-17  
**Platform:** Fabric Minecraft **1.20.1**, **Java 17**, intermediary mappings in shipped JARs  
**Current playable JAR (session):** `cube_crosshair-1.0.9-star-launcher.jar`  
**Branch used for work:** `arena/019f6c6d-chaos-glove-java-edition` (or your session branch)  
**Repos:** [aliornot/Chaos-Glove-java-edition](https://github.com/aliornot/Chaos-Glove-java-edition)

---

## 0. Current project state (what exists)

### 0.1. What is in the repo
- Often only a **compiled JAR** + assets + partial sources under `star_launcher_patch/`.
- **Full original Java sources** of the whole mod are **not** always present — many features exist only as `.class` in the JAR.
- Historical name/id: mod id `cube_crosshair`, item/namespace `chaos_glove`.
- Package: `com.example.cubecrosshair.*`

### 0.2. What works (as of 1.0.9)
| Feature | Status |
|---|---|
| Chaos Glove + 4 modes (Demon/Angel/Chaos Angel/Necromancer) | Working (original bytecode) |
| Mode menu on **G** (ChaosGloveScreen + ButtonWidget) | Working |
| Charge bars / ultimates / summons / black holes | Working (original) |
| Chaos portal texture (animated magenta, not missing texture) | Working (asset patch) |
| Star Launcher simple 3D model | Working (asset + model JSON) |
| Star Launcher menu on **J** (wooden table + 6 scrolls) | Working (1.0.9) |
| Star element HUD + mana bar 10/10 | Working (client) |
| Star mode NBT `StarMode` + client cache | Working (1.0.9) |
| Chaos dimension / island / boss | **Not finished** (planned below) |

### 0.3. Keybinds
| Item | Key | Action |
|---|---|---|
| Chaos Glove | **G** (GLFW 71) | Mode menu (Demon/Angel/…) |
| Star Launcher | **J** (GLFW 74) | Element table (Fire/Water/Earth/Star/Light/Dark) |

**Never bind both menus to the same key** — `KeyBinding.wasPressed()` consumes the press and **both menus break**.

### 0.4. NBT keys (items)
**Chaos Glove**
- `ChaosMode` — `demon` / `angel` / `chaos_angel` / `necromancer` / `none`
- Charge: `DemonCharge`, `AngelCharge`, `ChaosAngelCharge`, `NecromancerCharge`, `NecromancerCharge75`
- Ult flags/ticks: `DemonUltActive`, `DemonUltTicks`, `AngelUltActive`, `AngelUltTicks`, `ChaosAngelUltActive`, `ChaosAngelUltTicks`
- BH: `BHCharges`, `BHCooldownTicks`

**Star Launcher**
- `StarMode` — `fire` / `water` / `earth` / `star` / `light` / `dark` / `none`
- `StarMana`, `StarManaInit` (mana 0–10, default full when unset)
- Client cache field: `StarLauncherItem.clientSelectedMode` (immediate HUD after menu click)

### 0.5. Network packets
| ID | Direction | Purpose |
|---|---|---|
| `chaos_glove:change_mode` | C→S | Glove mode string |
| `chaos_glove:change_star_mode` | C→S | Star element string |
| `chaos_glove:spawn_black_hole` | S→C | Client BH particles |
| `chaos_glove:spawn_flying_bh` | S→C | Client flying BH particles |

---

## 1. Best practices, tips, stability & optimization

### 1.1. Build / Java (critical for Android FCL / Pojav)
- Target **Java 17 only** (`class` file major 61). Java 21 (65.0) **crashes** on common 1.20.1 mobile launchers.
```gradle
tasks.withType(JavaCompile).configureEach {
    it.options.release = 17
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
```
- Prefer **Fabric Loom** full project builds over hand-compiled “stub classpath” patches when possible.
- Sandboxed builds:
```bash
./gradlew --stop
./gradlew build -x remapSourcesJar -x sourcesJar --no-daemon
```
- Stop Gradle daemons to avoid OOM kills in constrained environments.

### 1.2. Server authority & networking
- Apply damage, effects, summons, attributes **only on server** (`!world.isClientSide()`).
- Use Fabric networking (`ServerPlayNetworking` / `ClientPlayNetworking`) for non-vanilla visuals (black holes, etc.).
- **PacketByteBuf.writeString** (`method_10814`) returns **`PacketByteBuf`**, not `void` (fluent API). Match original glove network bytecode.
- **Text.literal** (`class_2561.method_43470`) returns **`MutableText` (`class_5250`)**, not `Text` (`class_2561`).
- Prefer **explicit handler classes** for `PlayChannelHandler` over complex lambdas when patching bytecode by hand (lambdas + wrong stubs caused `NoClassDefFoundError`).

### 1.3. Client rendering (1.20.1 intermediary)
- HUD text: use **`DrawContext.method_25300`** = `drawCenteredTextWithShadow(TextRenderer, String, x, y, color)` — **5 args, returns void**.  
  Do **not** invent `method_25303(..., boolean)` — it caused world crash.
- UI fill: `method_25294(x1,y1,x2,y2,argb)`.
- Smooth animation: use `client.getFrameTime()` / tick delta for cube crosshair and particles.
- Screens: copy **ChaosGloveScreen** patterns (`ButtonWidget` + `method_37063`) when you need stable vanilla widgets.
- Custom painted UIs: only use APIs already proven in the original JAR.

### 1.4. Items & detection
- `chaos_glove:star_launcher` is registered as **plain `Item`**, **not** a custom subclass.  
  Detection must use:
  1. `stack.getItem() == CubeCrosshair.STAR_LAUNCHER` / `stack.isOf(...)`  
  2. registry id `chaos_glove:star_launcher`  
  3. **not** only `instanceof StarLauncherItem` (always false for the registered item).
- After menu select on client: **write NBT immediately** on the held stack **and** send packet (HUD won’t wait for server round-trip).

### 1.5. Entities / custom textures
- **Scoreboard tags do not sync to client.** For custom textures (RedVex, GoldGolem, GreenWither, GreenRavager):
```java
entity.setCustomName(Component.literal("RedVex"));
entity.setCustomNameVisible(false);
// client mixin: entity.getCustomName().getString()
```

### 1.6. Dimensions / portal datapack
- Use `monster_spawn_light_level` (not obsolete `monster_spawn_light_test`).
- Do **not** use `"block": "minecraft:air"` flat layers — validation crash. Use bedrock layer height 1 as void floor.
- `infiniburn` needs `#` tag form: `#minecraft:infiniburn_end`.
- Exit portal in custom dim: use **custom ChaosPortalBlock**, not vanilla End portal (hardcoded to The End).

### 1.7. Mixins / early join safety
- `getDimensions()` / eye height can run **before inventory is ready** → NPE blocks world join.
```java
if (player.getInventory() == null) return false;
try { ... } catch (Throwable t) { return false; }
```
- Angel flight: mixin `Player.getDestroySpeed` and **×5** while Angel ult is active (cancel vanilla flying mining penalty).

### 1.8. Assets
- Portal: self-contained model + **animated** texture `.png` + `.mcmeta` (`interpolate: true`). Parent `minecraft:block/portal` may not exist → missing texture (purple/black).
- JSON models UV for 32×32 skins: divide pixel coords by 2.0 (0–16 space).
- Keep item 3D models **simple** (few elements) — complex multi-part staff UVs broke easily.

### 1.9. Keybinds
- One feature → one key. Glove **G**, Star **J**.
- Register with Fabric `KeyBindingHelper` using the same 3-arg constructor pattern as the original mod (`translationKey`, glfwCode, `category`).

### 1.10. JAR shipping hygiene
- Never ship **stub** classes (`net/minecraft/*`, `net/fabricmc/*`) inside the mod JAR.
- Only ship real feature classes + resources.
- On Android FCL: remove **all** old `cube_crosshair*` jars (including ` (1)` duplicates) before testing a new version.

### 1.11. Optimization (gameplay code)
- Black hole explosions: **staggered** detonation (scheduler), not one-frame 175 TNT.
- Summons AI tick on server scheduler; cap companions; clean tags/names on death.
- Client particle emitters for BH only while active; remove on expire.
- Prefer server tick managers over per-entity spam.

---

## 2. Errors & pitfalls (original guide + this session)

### 2.1. From original `chaos_glove_porting_guide.md`

#### Bug A — Java class file version 65.0
- **Symptom:** `UnsupportedClassVersionError` (65.0 vs 61.0).
- **Cause:** compiled with Java 21.
- **Fix:** force `release = 17` (see §1.1).

#### Bug B — Scoreboard tags not on client
- **Symptom:** custom summon textures never apply.
- **Fix:** CustomName string + client mixin check (§1.5).

#### Bug C — Invalid dimension datapack
- **Symptom:** world gen crash / OOM / datapack fail.
- **Fix:** correct light fields; no air flat layers; `#infiniburn_...` (§1.6).

#### Bug D — NPE on world join
- **Symptom:** cannot join world; NPE in dimensions/eye height.
- **Fix:** null inventory check + try/catch in ultimate dimension hooks (§1.7).

#### Bug E — Flying mining slowdown
- **Symptom:** Angel form cannot mine while flying.
- **Fix:** destroy speed ×5 mixin when Angel ult active (§1.7).

#### Bug F — Vanilla End portal destination
- **Symptom:** exit portal sends player to The End.
- **Fix:** custom `ChaosPortalBlock` for return (§1.6).

---

### 2.2. Errors hit while patching Star Launcher / menus (this session)

#### Error 1 — `ServerPlayNetworking$PacketSender` NoClassDefFoundError
- **When:** game start, entrypoint `StarLauncherBootstrap`.
- **Cause:** hand-compiled stubs treated `PacketSender` as nested type / bad lambda metafactory vs real Fabric API (`net.fabricmc.fabric.api.networking.v1.PacketSender` is **top-level**).
- **Fix:** use real signature; prefer **named handler class** implementing `PlayChannelHandler`; `registerGlobalReceiver` returns **`boolean` (`Z`)**, not `void`.

#### Error 2 — Two keybinds on G → no menus
- **Symptom:** G opens nothing for glove or star.
- **Cause:** both bindings used G; `wasPressed()` consumed once.
- **Fix:** Glove **G**, Star **J**.

#### Error 3 — J pressed but star menu never opens
- **Cause:** `instanceof StarLauncherItem` always false (item is plain `Item`).
- **Fix:** detect via `CubeCrosshair.STAR_LAUNCHER` / `isOf` / registry id (§1.4).

#### Error 4 — World crash: `method_25303(... boolean)` NoSuchMethodError
- **When:** after joining world (HUD render).
- **Cause:** wrong DrawContext method for 1.20.1 intermediary.
- **Fix:** only `method_25300` (5-arg centered string), same as glove HUD.

#### Error 5 — Crash on J: `method_43470` wrong return type
- **When:** opening StarLauncherScreen.
- **Cause:** assumed `method_43470` returns `class_2561`; real return is `class_5250`.
- **Fix:** match ChaosGloveScreen: cast/use MutableText return type.

#### Error 6 — Crash on selecting element: `method_10814` void NoSuchMethodError
- **When:** click scroll/button in star menu.
- **Cause:** `writeString` compiled as `void`; real method returns `PacketByteBuf`.
- **Fix:** signature `method_10814(String)Lclass_2540;` like `ChaosGloveClientNetwork`.

#### Error 7 — Mode selected but HUD still “not selected”
- **Cause:** only server packet; client NBT not updated for HUD.
- **Fix:** client `setMode` on held stack + `clientSelectedMode` cache, then packet.

#### Error 8 — Portal missing texture (purple/black)
- **Cause:** model parent `minecraft:block/portal` missing; no own texture.
- **Fix:** self-contained portal model + animated `chaos_portal.png` + `.mcmeta`.

#### Error 9 — Complex Star Launcher 3D model “broken”
- **Cause:** too many elements / bad UVs.
- **Fix:** simple 4-element staff model + solid atlas.

#### Error 10 — Stub classes in classpath vs game
- Compiling against **fake stubs** is OK for producing feature classes, but:
  - stubs must match **real** intermediary descriptors exactly;
  - never package stubs into the shipped mod JAR.

#### Error 11 — Android duplicate jars
- FCL may keep `mod (1).jar` alongside new versions → old broken code still loads.
- Always delete **all** old `cube_crosshair*` before testing.

---

## 3. Boss: «Пожиратель Коллапса» / Collapse Eater (PLANNED)

> Status: **not implemented yet**. Spec from original porting guide — implement after dimension/portal polish.

### 3.1. Dimension & portal prerequisites (planned)
- **Portal frame:** iron blocks, min vertical **4×5**, like Nether.
- **Ignition:** flint & steel → fill with `ChaosPortalBlock` (nether-portal-like purple animation; custom chaos texture preferred).
- **Dimension** `chaos_glove:chaos_dimension`:
  - void-like type, fixed time **18000**, beds off, End-like effects/fog.
  - One circular **End Stone** island: radius **42**, height 4 (Y **76–79**), center `(0, 76, 0)`.
  - Spawn: 3×3 bedrock at `(-50, 80, 0)`, facing island (~7 block void gap).
  - Exit: bedrock + **ChaosPortalBlock** at island center `(0, 80, 0)` → Overworld spawn.
  - **No natural mob spawns** (Endermen etc. disabled).

### 3.2. Boss identity
| Field | Value |
|---|---|
| Name (RU) | Пожиратель Коллапса |
| Name (EN) | Collapse Eater |
| Max HP | **1000** |
| Immunities | Knockback, fire damage, fall damage |
| Arena | Chaos dimension island |

### 3.3. Abilities (AI plan)
1. **Gravity Pull**  
   - Pulls the player toward boss center.  
   - Goal: drag player off the island into the void.

2. **Chaos Laser**  
   - Thick **purple** beam.  
   - Magic damage + **Blindness**.

3. **Summons / Shield phase**  
   - Summons **Red Vexes** and **Green Wither Skeletons** (same companion systems as glove where possible).  
   - Boss **invulnerable** until summons are cleared.

4. **Enraged mode** (`HP < 250`)  
   - Rapid lasers.  
   - Mini-explosions in quick succession.

### 3.4. Loot — Charged Stars
- Every **250 damage taken** (at remaining HP thresholds **750 / 500 / 250**), boss drops a **Charged Star** item.
- On pickup:
  - play level-up sound;
  - consume star immediately;
  - fill active Chaos Glove charge scales to max (**100**, or Necromancer **25/75**).

### 3.5. Implementation notes for the boss (when coding)
- Server-side AI only; client for beam/VFX packets if needed.
- Use existing `ServerScheduler` for delayed waves.
- Reuse `VexManager` spawn helpers + CustomName tags for textures.
- Do not use vanilla End portal for arena exit.
- Keep explosion stagger pattern from black holes to avoid mobile lag/OOM.
- Test on **Java 17** + Fabric API 0.92.x line for 1.20.1 (as in FCL logs).

---

## 4. Implemented feature reference (quick)

### 4.1. Chaos Glove modes (original)
- **Demon:** fire 8s; +1 hit / +5 kill; 50 → 4 Red Vex; 100 → Demon Form 100s (scale, +20 max HP, Strength II, Regen I).
- **Angel:** slowness 8s; 75 → Gold Golem; 100 → flight 100s, Speed II, no fly mining penalty.
- **Chaos Angel:** poison+glow 8s; 100 → Resistance 255 / Speed III / Jump III for 60s; static BH (2 charges, 20s CD); flying BH (50 charge).
- **Necromancer:** wither 5s; charge **on kills only** (+1 / +2 if target max HP > 15); 25 → 3 green wither skeletons; 75 → 2 green ravagers + Regen II 100s.

### 4.2. Star Launcher elements (current)
- Menu: wooden table + scrolls (J).
- Modes stored in `StarMode`; HUD shows selected element; mana bar 10/10 reserved for future abilities.
- **Abilities per element:** not fully designed/implemented yet — next design pass.

---

## 5. Suggested next work order
1. Confirm 1.0.9 stable on device (G glove, J star table, HUD updates, no crash on select).
2. Finish chaos dimension island + spawn gap + exit portal (datapack + generation code).
3. Implement **Collapse Eater** AI + phases + Charged Stars.
4. Design Star Launcher combat per element (spend mana).
5. Ideally: restore full Loom source tree (decompile JAR cleanly once) so future work isn’t stub-compiled.

---

## 6. File map (useful paths)
```
cube_crosshair-1.0.9-star-launcher.jar   # current playable build
chaos_glove_porting_guide.md             # original long-form guide
CONTEXT_TRANSFER_GUIDE.md                # THIS file
star_launcher_patch/src/                 # Star Launcher Java sources (hand-maintained)
star_launcher_patch/assets/              # portal + star models/textures/lang
src/main/resources/assets/               # may mirror assets
```

### Core original classes (inside JAR)
- `CubeCrosshair`, `ChaosGloveItem`, `ChaosPortalBlock`
- `CubeCrosshairClient`, `ChaosGloveScreen`, `ChaosGloveClientNetwork`
- `BlackHoleServerManager`, `FlyingBlackHoleManager`, `VexManager`, `ServerScheduler`
- Mixins: `PlayerMixin`, `ServerLevelMixin`, renderer mixins for summons

### Star Launcher classes (added)
- `StarLauncherBootstrap`, `StarLauncherItem`
- `StarLauncherClient`, `StarLauncherScreen`, `StarLauncherClientNetwork`

---

## 7. Checklist for the next AI session
- [ ] Read this entire file  
- [ ] Confirm target: Fabric 1.20.1 + Java 17 only  
- [ ] Do not rebind both menus to G  
- [ ] Do not use `method_25303` for HUD text  
- [ ] Do not compile `writeString` as void  
- [ ] Do not use `instanceof StarLauncherItem` alone  
- [ ] Do not ship stub classes in the mod JAR  
- [ ] Before boss: finish dimension island + exit portal  
- [ ] Boss: 1000 HP, 4 abilities, Charged Stars every 250 damage  

---

*End of context transfer document. Prefer extending a proper Loom project over further JAR-only patches when possible.*

# 💎 NetheriteSilkSpawners

Advanced Spawner Management for Minecraft (1.19 - 1.21.1) with Smart Status Checks, Discord Integration, and Total Lockdown Protection.

---

## 🚀 Key Features

*   **Multi-Version Support:** Native compatibility for Minecraft 1.19 up to 1.21.1. Compiled in Java 17 for maximum stability across different server environments.
*   **Global Lockdown System:** 
    *   **Immune to Griefing:** Spawners are now immune to Wither Boss attacks, Ender Dragon breath, and Enderman block picking.
    *   **Piston Protection:** Spawners are hard-locked as immovable blocks. They cannot be pushed or pulled by normal or sticky pistons.
    *   **Anti-Fake Spawner:** Every spawn attempt is validated via NBT Legitimacy Tags. This blocks other plugins or external sources from generating entities via non-vanilla or unauthorized blocks.
*   **Multi-Language Support:** Fully localized templates included for Portuguese (pt), English (en), Spanish (es), and Russian (ru).
*   **Triple-Layer Territory Protection:** Native integration with WorldGuard, GriefPrevention, and RedProtect (v8) ensures strict "Trust/Member" validation.
*   **Anvil Security:** Blocks players from renaming or modifying spawner items in anvils, preventing NBT tampering or rule bypasses.
*   **Smart Deny System:** Provides real-time feedback on missing requirements (Tool, Silk Touch, or Permission) with clean, prefix-free status indicators.
*   **Discord Webhook Audit:** Real-time logging of all interactions (Place, Collect, Change, or Explosion) directly to your staff channel.

---

## 📜 Commands

*   /nss reload - Reloads the configuration and all language files (messages_*.yml).

---

## 🔑 Permissions


| Permission | Description | Default |
| :--- | :--- | :--- |
| nss.admin | Full bypass for protection checks and access to /nss reload. | OP |
| nss.collect.all | Allows mining any type of spawner. | OP |
| nss.place.all | Allows placing any type of spawner. | OP |
| nss.change.all | Allows changing spawner types using Spawn Eggs. | OP |
| nss.tool.netherite_pickaxe | Allows using a Netherite Pickaxe to mine. | true |
| nss.collect.[mob] | Allows mining a specific mob spawner (e.g., nss.collect.blaze). | OP |

---

## ⚙️ Configuration Snippet

language: "en" (Options: pt, en, es, ru)
allowed-pickaxes: NETHERITE_PICKAXE
auto-inventory: true
require-tool-permission: true
require-silk-touch: true
prevent-explosion-drop: true
discord-webhook-url: "YOUR_URL_HERE"

---

## 🐞 Bug Fixes in v1.3

*   **NBT Safety:** Resolved a critical NullPointerException when placing "type-less" or vanilla-generated spawners.
*   **Clean Status Display:** Fixed a visual bug where the plugin prefix appeared twice in the Smart Status messages.
*   **External Tool Bypass:** Fixed a security flaw that allowed custom tools from other plugins (like Slimefun) to mine spawners without proper permissions.

---

## ⚠️ Requirements

*   Java 17 or higher.
*   Supported Protections: WorldGuard (7.x), GriefPrevention (16.x), or RedProtect (8.x).

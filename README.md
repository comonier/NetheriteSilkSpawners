# 💎 NetheriteSilkSpawners v1.1

Advanced Spawner Management for Minecraft 1.21.1 with Smart Status Checks, Discord Integration, and Direct Inventory Collection.

---

## 🚀 Key Features

*   **Smart Deny System:** Provides detailed feedback when an action is blocked. Players see exactly what they are missing (Netherite Tool, Silk Touch, or Permission) with dynamic **&aOn** / **&cOff** status.
*   **Direct Inventory Collection:** Mined spawners go directly into the player's inventory, bypassing external autoloot systems. If the inventory is full, the item safely drops at the player's feet.
*   **Discord Webhook Integration:** Real-time auditing for all spawner actions (Collect, Place, Change, and Explosions) sent directly to your staff channel.
*   **Global Announcements:** Optional chat broadcasts to inform the server about who is mining or placing spawners.
*   **Territory Protection:** Full integration with **WorldGuard** and **GriefPrevention** to prevent theft in protected regions or claims.
*   **Advanced Explosion Control:** Prevents spawners from being destroyed or dropped by TNT, Creepers, or Anchor/Bed explosions.
*   **1.21.1 Ready:** Full support for new mobs (Bogged, Breeze) and custom tool materials like the Copper Pickaxe.

---

## 📜 Commands

*   `/nss reload` - Reloads the configuration and language files (messages_pt.yml / messages_en.yml).

---

## 🔑 Permissions


| Permission | Description | Default |
| :--- | :--- | :--- |
| `nss.admin` | Full bypass for protection checks and access to /nss reload. | OP |
| `nss.collect.all` | Allows mining any type of spawner. | OP |
| `nss.place.all` | Allows placing any type of spawner. | OP |
| `nss.change.all` | Allows changing spawner types using Spawn Eggs. | OP |
| `nss.tool.netherite_pickaxe` | Allows using a Netherite Pickaxe to mine (if enabled). | true |
| `nss.collect.<mob>` | Allows mining a specific mob spawner (e.g., nss.collect.creeper). | OP |

---

## ⚙️ Configuration Snippet

You can customize allowed tools, language, and Discord Webhooks in the `config.yml`:
```yaml
language: "pt"
allowed-pickaxes:
  - "NETHERITE_PICKAXE"
#  - "COPPER_PICKAXE"
discord-webhook-url: "YOUR_URL_HERE"
announce-globally: true

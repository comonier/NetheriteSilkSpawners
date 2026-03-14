# 💎 NetheriteSilkSpawners v1.2

Advanced Spawner Management for Minecraft 1.21.1 with Smart Status Checks, Discord Integration, and Anti-Loss Inventory Protection.

---

## 🚀 Key Features

*   **Smart Deny System:** Provides detailed feedback when an action is blocked. Players see exactly what they are missing (Netherite Tool, Silk Touch, or Permission) with dynamic **&aOn** / **&cOff** status.
*   **Anti-Loss Auto-Inventory:** Mined spawners go directly to the player's inventory.
    *   **Security Lock:** If `auto-inventory` is enabled, the plugin **prevents the block from breaking** if the player's inventory is full, ensuring zero item loss.
*   **Discord Webhook Integration:** Real-time auditing for all spawner actions (Collect, Place, Change, and Explosions) sent directly to your staff channel.
*   **Global Announcements:** Optional chat broadcasts to inform the server about who is mining or placing spawners.
*   **Territory Protection:** Full integration with **WorldGuard** and **GriefPrevention** to prevent theft in protected regions or claims.
*   **Advanced Explosion Control:** Professional-grade protection against TNT, Creepers, or Anchor/Bed explosions.
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

You can customize allowed tools, security locks, and Discord Webhooks in the `config.yml`:

```yaml
language: "pt"
allowed-pickaxes:
  - "NETHERITE_PICKAXE"
#  - "COPPER_PICKAXE"

# If true, block will NOT break if inventory is full (prevents loss)
auto-inventory: true

discord-webhook-url: "YOUR_URL_HERE"
announce-globally: true
```

##  ⚠️ Important: Update Instructions

* Please read carefully before updating to v1.1:

*  **Delete Old Files**: You MUST delete the old config.yml, messages_pt.yml, and messages_en.yml for the new safety and smart status logic to work.

*  **Inventory Check**: Players must have at least one empty slot to mine spawners when auto-inventory is enabled. If they don't, the spawner will simply not break.

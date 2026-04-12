# 💎 NetheriteSilkSpawners

Advanced Spawner Management for Minecraft (1.19 - 1.21.1) with Smart Status Checks, Discord Integration, and Triple-Layer Territory Protection.

---

## 🚀 Key Features

*   **Multi-Language Support:** Available in Portuguese (pt), English (en), Spanish (es), and Russian (ru).
*   **Triple-Layer Protection:** Full native integration with **WorldGuard**, **GriefPrevention**, and **RedProtect**.
    *   Ensures players can only interact with spawners in areas where they have explicit **Trust**, **Membership**, or **Leadership**.
*   **Custom Tool Security:** Hard-coded event cancellation that prevents external plugins (like **Slimefun** or custom industrial pickaxes) from bypassing spawner collection rules.
*   **Smart Deny System:** Provides detailed feedback when an action is blocked. Players see exactly what they are missing (Tool, Silk Touch, or Permission) with dynamic status indicators.
*   **Anti-Loss Auto-Inventory:** Mined spawners go directly to the player's inventory.
    *   **Security Lock:** If `auto-inventory` is enabled, the plugin **prevents the block from breaking** if the player's inventory is full, ensuring zero item loss.
*   **Discord Webhook Integration:** Real-time auditing for all spawner actions (Collect, Place, Change, and Explosions) sent directly to your staff channel.
*   **Advanced Explosion Control:** Professional-grade protection against TNT, Creepers, or Anchor/Bed explosions with configurable drop toggles.
*   **NBT Safety:** Robust handling of spawner placement to prevent server crashes or NullPointerExceptions when dealing with vanilla or glitched spawner items.

---

## 📜 Commands

*   `/nss reload` - Reloads the configuration and language files.

---

## 🔑 Permissions


| Permission | Description | Default |
| :--- | :--- | :--- |
| `nss.admin` | Full bypass for protection checks and access to reload. | OP |
| `nss.collect.all` | Allows mining any type of spawner. | OP |
| `nss.place.all` | Allows placing any type of spawner. | OP |
| `nss.change.all` | Allows changing spawner types using Spawn Eggs. | OP |
| `nss.tool.netherite_pickaxe` | Allows using a Netherite Pickaxe to mine. | true |
| `nss.collect.<mob>` | Allows mining a specific mob spawner. | OP |

---

## ⚙️ Configuration

*   **language:** Set to "pt", "en", "es", or "ru".
*   **allowed-pickaxes:** List of materials allowed to mine.
*   **auto-inventory:** Toggle for direct inventory collection.
*   **discord-webhook-url:** Your Discord Webhook URL for logs.

---

## ⚠️ Requirements

*   **Java 21** or higher.
*   **Supported Protection Plugins**: WorldGuard (7.x), GriefPrevention (16.x), or RedProtect (7.x).

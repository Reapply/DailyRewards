## **Prompt: Custom Daily Reward Crate System**

### Goal:

  Create a plugin that gives each player access to a **Daily Reward Crate GUI** 
  once every 24 hours. Players can open the GUI with `/dailycrate`, choose one of several 
  randomized rewards, and can't open it again until the next day.

  
  
  ###  Core Requirements:
  
  ###  `/dailycrate` Command:
  
  * Opens a custom **3x3 inventory GUI** titled "Daily Reward"
  * 5 randomly shuffled items (predefined rewards)
  * Player clicks **one** to receive it, and the rest disappear
  * GUI closes automatically after selection
  
  ###  Cooldown System:
  
  * Each player can only open the crate once every 24 hours
  * Show a message like:
        `&cYou can open your next crate in 17h 32m.` if on cooldown
  * Cooldown must persist across server restarts (store in file)
  
  ###  Example Rewards:
  
  * 5x Diamond
  * 1x Enchanted Golden Apple
  * 32x Arrows
  * \$1,000 (simulated economy)
  * 1x Random potion (choose from a set of effects)
  
  You can hard-code these into the plugin or use a simple config if preferred.
  
  ###  Feedback & UX:
  
  * Send a message to the player upon receiving the reward
  * Prevent inventory glitches, nulls, or double claims
  * Prevent crate access during combat or if inventory is full
  
  ###  Bonus Points (Optional if time allows):
  
  *  Add glowing items in GUI using enchantment trick
  *  Add `/dailycrate reload` to reload rewards from config
  *  Track how many crates each player has claimed (`/dailycrate stats`)
  *  Cooldown bypass permission (`dailycrate.bypass`)
  
  
  ###  Deliverables:
  
  * The compiled JAR plugin
  * The source code (public GitHub repo or ZIP)
  * (Optional) A short README or summary of features
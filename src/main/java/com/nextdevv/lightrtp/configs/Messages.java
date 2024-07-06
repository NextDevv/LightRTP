package com.nextdevv.lightrtp.configs;

import de.exlll.configlib.Configuration;
import lombok.Getter;

@Getter
@Configuration
public class Messages {
    String prefix = "&7[&6LightRTP&7]&r";
    String noPermission = "{prefix}&7 You don't have permission to do that!";
    String onlyPlayers = "{prefix}&7 Only players can use this command!";
    String loadingPosition = "{prefix}&7 Hold on, we're finding a safe location for you!";
    String[] loading = { "&6■&7■■", "&7■&6■&7■", "&7■■&6■", "&7■■■" };
    String teleported = "&7You have been teleported to a safe location!";
    String error = "{prefix}&7 An error occurred while trying to find a safe location!";
    String youMoved = "{prefix}&7 You moved while we were finding a safe location!";
    String retrying = "{prefix}&7 Retrying... attempt &6{attempt}&7/&6{maxAttempts}!";
    String cooldown = "{prefix}&7 You must wait &6{time}&7 seconds before using this command again!";
    String notTeleporting = "{prefix}&7 You aren't teleporting right now!";
    String cancelled = "{prefix}&7 Teleportation cancelled!";
    String alreadyTeleporting = "{prefix}&7 You are already teleporting!";
    String netherNotAllowed = "{prefix}&7 You are not allowed to &6rtp&7 in the nether!";
}

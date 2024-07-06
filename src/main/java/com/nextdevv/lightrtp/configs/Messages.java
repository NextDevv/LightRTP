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
}

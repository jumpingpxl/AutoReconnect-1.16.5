package de.jumpingpxl.labymod.autoreconnect.util;

import com.google.gson.JsonObject;
import de.jumpingpxl.labymod.autoreconnect.AutoReconnect;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.HeaderElement;
import net.labymod.settings.elements.NumberElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.Material;

import java.util.List;

public class Settings {

	private final AutoReconnect autoReconnect;
	private boolean enabled;
	private int secondsUntilReconnect;

	public Settings(AutoReconnect autoReconnect) {
		this.autoReconnect = autoReconnect;
	}

	private JsonObject getConfig() {
		return autoReconnect.getConfig();
	}

	private void saveConfig() {
		autoReconnect.saveConfig();
	}

	public void loadConfig() {
		enabled = !getConfig().has("enabled") || getConfig().get("enabled").getAsBoolean();
		secondsUntilReconnect = getConfig().has("secondsUntilReconnect") ? getConfig().get(
				"secondsUntilReconnect").getAsInt() : 5;
	}

	public void fillSettings(List<SettingsElement> list) {
		list.add(new HeaderElement("§eAutoReconnect v" + AutoReconnect.VERSION));
		list.add(new HeaderElement(" "));
		list.add(new BooleanElement("§6Enabled", new ControlElement.IconData(Material.LEVER),
				value -> {
			enabled = value;
			getConfig().addProperty("enabled", value);
			saveConfig();
		}, enabled));

		NumberElement numberElement = new NumberElement("§6Seconds until reconnect",
				new ControlElement.IconData(Material.WATCH), secondsUntilReconnect);
		numberElement.setRange(5, 60);
		numberElement.addCallback(integer -> {
			secondsUntilReconnect = integer;
			getConfig().addProperty("secondsUntilReconnect", integer);
			saveConfig();
		});

		list.add(numberElement);
	}

	public boolean isEnabled() {
		return enabled && (autoReconnect.getLastServer() != null
				&& !autoReconnect.getLastServer().serverIP.split(":")[0].toLowerCase().endsWith(
				"labymod.net"));
	}

	public int getSecondsUntilReconnect() {
		return secondsUntilReconnect;
	}
}

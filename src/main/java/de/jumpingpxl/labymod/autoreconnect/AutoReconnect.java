package de.jumpingpxl.labymod.autoreconnect;

import de.jumpingpxl.labymod.autoreconnect.listener.GuiOpenListener;
import de.jumpingpxl.labymod.autoreconnect.util.Settings;
import net.labymod.api.LabyModAddon;
import net.labymod.settings.elements.SettingsElement;
import net.minecraft.client.multiplayer.ServerData;

import java.lang.reflect.Field;
import java.util.List;
import java.util.NoSuchElementException;

public class AutoReconnect extends LabyModAddon {

	public static final int VERSION = 4;

	private Settings settings;
	private ServerData lastServer;

	@Override
	public void onEnable() {
		settings = new Settings(this);

		getApi().getEventService().registerListener(new GuiOpenListener(this, settings));
	}

	@Override
	public void loadConfig() {
		settings.loadConfig();
	}

	@Override
	protected void fillSettings(List<SettingsElement> settingsElements) {
		settings.fillSettings(settingsElements);
	}

	public ServerData getLastServer() {
		return lastServer;
	}

	public void setLastServer(ServerData lastServer) {
		this.lastServer = lastServer;
	}

	public Field findField(Class<?> clazz, String... fieldNames) {
		for (String fieldName : fieldNames) {
			try {
				Field f = clazz.getDeclaredField(fieldName);
				f.setAccessible(true);
				return f;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		throw new NoSuchElementException();
	}
}

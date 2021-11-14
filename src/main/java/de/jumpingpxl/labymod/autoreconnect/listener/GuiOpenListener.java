package de.jumpingpxl.labymod.autoreconnect.listener;

import de.jumpingpxl.labymod.autoreconnect.AutoReconnect;
import de.jumpingpxl.labymod.autoreconnect.util.ModDisconnectedScreen;
import de.jumpingpxl.labymod.autoreconnect.util.Settings;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.events.client.gui.screen.ScreenOpenEvent;
import net.labymod.gui.GuiRefreshSession;
import net.labymod.gui.ModGuiMultiplayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConnectingScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;

public class GuiOpenListener {

	private static final String[] TITLE_MAPPINGS = new String[]{"d", "title"};

	private final AutoReconnect autoReconnect;
	private final Settings settings;

	public GuiOpenListener(AutoReconnect autoReconnect, Settings settings) {
		this.autoReconnect = autoReconnect;
		this.settings = settings;
	}

	@Subscribe(priority = 5)
	public void onScreenOpen(ScreenOpenEvent event) {
		Screen screen = event.getScreen();
		if (screen instanceof ConnectingScreen) {
			autoReconnect.setLastServer(Minecraft.getInstance().getCurrentServerData());
		}

		if (screen instanceof DisconnectedScreen && settings.isEnabled()) {
			DisconnectedScreen disconnectedScreen = (DisconnectedScreen) screen;
			try {
				ITextComponent title = (ITextComponent) autoReconnect.findField(Screen.class,
						TITLE_MAPPINGS).get(disconnectedScreen);
				screen = new ModDisconnectedScreen(autoReconnect, settings, disconnectedScreen, title);
			} catch (IllegalStateException e) {
				//screen = new GuiRefreshSession(new ModGuiMultiplayer(null));
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

			event.setScreen(screen);
		}
	}
}

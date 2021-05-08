package de.jumpingpxl.labymod.autoreconnect.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.jumpingpxl.labymod.autoreconnect.AutoReconnect;
import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.gui.screen.ConnectingScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Timer;
import java.util.TimerTask;

public class ModDisconnectedScreen extends Screen {

	private static final String[] PARENT_SCREEN_MAPPINGS = new String[]{"c", "field_146307_h",
			"nextScreen"};
	private static final String[] MESSAGE_MAPPINGS = new String[]{"a", "field_146304_f", "message"};

	private final AutoReconnect autoReconnect;
	private final Screen parentScreen;
	private final ITextComponent message;
	private IBidiRenderer renderer = IBidiRenderer.field_243257_a;
	private Button reconnectButton;
	private Timer timer;
	private int secondsLeft;
	private int textHeight;

	public ModDisconnectedScreen(AutoReconnect autoReconnect, Settings settings,
	                             DisconnectedScreen disconnectedScreen, ITextComponent title)
			throws IllegalAccessException {
		super(title);
		this.autoReconnect = autoReconnect;

		parentScreen = (Screen) autoReconnect.findField(DisconnectedScreen.class,
				PARENT_SCREEN_MAPPINGS).get(disconnectedScreen);
		message = (ITextComponent) autoReconnect.findField(DisconnectedScreen.class, MESSAGE_MAPPINGS)
				.get(disconnectedScreen);

		secondsLeft = settings.getSecondsUntilReconnect();
		if (message.getUnformattedComponentText().equals(I18n.format("disconnect.loginFailedInfo",
				I18n.format("disconnect" + ".loginFailedInfo.invalidSession")))) {
			throw new IllegalStateException();
		}
	}

	@Override
	protected void init() {
		this.renderer = IBidiRenderer.func_243258_a(this.font, this.message, this.width - 50);
		this.textHeight = this.renderer.func_241862_a() * 9;
		this.addButton(new Button(this.width / 2 - 10,
				Math.min(this.height / 2 + this.textHeight / 2 + 9, this.height - 30), 125, 20,
				new TranslationTextComponent("gui.toMenu"), onClick -> {
			timer.cancel();
			this.minecraft.displayGuiScreen(parentScreen);
		}));

		reconnectButton = new Button(width / 2 - 115,
				Math.min(this.height / 2 + this.textHeight / 2 + 9, this.height - 30), 100, 20,
				new StringTextComponent("Reconnect in: §a" + secondsLeft + "s"), onClick -> {
			timer.cancel();
			this.minecraft.displayGuiScreen(
					new ConnectingScreen(parentScreen, this.minecraft, autoReconnect.getLastServer()));
		});

		this.addButton(reconnectButton);
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				secondsLeft--;
				String color = "§a";
				switch (secondsLeft) {
					case 9:
					case 8:
					case 7:
					case 6:
						color = "§e";
						break;
					case 5:
					case 4:
						color = "§c";
						break;
					case 3:
					case 2:
					case 1:
						color = "§4";
						break;
					case 0:
						color = "§4";
						timer.cancel();
						break;
				}

				reconnectButton.setMessage(
						new StringTextComponent("Reconnect in: " + color + secondsLeft + "s"));
			}
		}, 1000L, 1000L);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		drawCenteredString(matrixStack, this.font, this.title, this.width / 2,
				this.height / 2 - this.textHeight / 2 - 9 * 2, 11184810);
		this.renderer.func_241863_a(matrixStack, this.width / 2,
				this.height / 2 - this.textHeight / 2);
		super.render(matrixStack, mouseX, mouseY, partialTicks);

		if (secondsLeft == 0) {
			reconnectButton.onPress();
		}
	}

	@Override
	public void onClose() {
		super.onClose();
		timer.cancel();
	}
}

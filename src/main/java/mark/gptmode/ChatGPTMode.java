package mark.gptmode;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mark.gptmode.utils.Constants;

import java.io.IOException;

public class ChatGPTMode implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger(Constants.MOD_ID);

	@Override
	public void onInitialize() {
			ModCommands.initialize();
    }
}
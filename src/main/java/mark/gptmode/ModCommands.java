package mark.gptmode;


import mark.gptmode.utils.Constants;
import mark.gptmode.utils.Commands.Register;
import mark.gptmode.utils.PlayerConversationHandler;

import java.io.IOException;


public class ModCommands {

    public static void initialize() {
        Register.gptCommand();
        Register.conversationCommands();
        Register.AITokenCommands();
        PlayerConversationHandler.registerPlayer();
    }
}

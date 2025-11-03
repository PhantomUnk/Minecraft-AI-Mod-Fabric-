package mark.gptmode.utils.Commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import mark.gptmode.utils.Constants;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;

public class Register {
    public static void gptCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal(Constants.CommandNames.GPT_ASK_COMMAND)
                    .then(CommandManager.argument(Constants.ArgumentNames.GPT_QUESTION, StringArgumentType.string()) // * First argument
                            .then(CommandManager.argument(Constants.ArgumentNames.GPT_CHAT_GLOBAL, BoolArgumentType.bool()) // * Second argument
                                    .executes(GPT::askGpt)))); // * Passing both arguments to the executeAskGpt method
        });
    }

    public static void conversationCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal(Constants.CommandNames.CONVERSATION_COMMAND)

                    .then(CommandManager.literal(Constants.CommandNames.CONVERSATION_GET)
                            .executes(Conversation::getConversation))

                    .then(CommandManager.literal(Constants.CommandNames.CONVERSATION_SET)
                            .then(CommandManager.argument(Constants.ArgumentNames.CONVERSATION_INDEX, IntegerArgumentType.integer())
                                    .executes(Conversation::setConversation)))

                    .then(CommandManager.literal(Constants.CommandNames.CONVERSATION_CREATE)
                            .then(CommandManager.argument(Constants.ArgumentNames.CONVERSATION_NAME, StringArgumentType.string())
                                    .executes(Conversation::createConversation)))

                    .then(CommandManager.literal(Constants.CommandNames.CONVERSATION_DELETE)
                            .then(CommandManager.literal(Constants.CommandNames.CONVERSATION_DELETE_BY_INDEX)
                                    .then(CommandManager.argument(Constants.ArgumentNames.CONVERSATION_INDEX, IntegerArgumentType.integer())
                                            .executes(Conversation::deleteConversationByIndex)))
                        .then(CommandManager.literal(Constants.CommandNames.CONVERSATION_DELETE_ALL)
                            .executes(Conversation::deleteAllConversations)))


            );
        });
    }

    public static void AITokenCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal(Constants.CommandNames.AI_COMMAND)

                    .then(CommandManager.literal(Constants.CommandNames.SET_AI_TOKEN)
                        .then(CommandManager.argument(Constants.ArgumentNames.AI_TOKEN, StringArgumentType.string()) // * First argument
                            .executes(GPT::setAIToken)))); // * Passing both arguments to the executeAskGpt method
        });
    }
}

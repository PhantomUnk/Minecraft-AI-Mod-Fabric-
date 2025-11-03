package mark.gptmode.utils.Commands;

import com.mojang.brigadier.context.CommandContext;
import mark.gptmode.ChatGPTMode;
import mark.gptmode.utils.Constants;
import mark.gptmode.utils.PlayerConversationHandler;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.IOException;
import java.util.Arrays;

@SuppressWarnings({"SuspiciousArrayIndexOutOfBounds", "ConstantConditions", "RedundantCondition"})
public class Conversation {
    public static int getConversation(CommandContext<ServerCommandSource> ctx){
        try {
            String playerID
                    = ctx.getSource().getPlayer().getUuidAsString();

            String[] conversationNames =
                    PlayerConversationHandler.getPlayerConversationNames(playerID);

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Your chats:\n");

            for (
                    int conversationIndex = 0;
                    conversationIndex < conversationNames.length;
                    conversationIndex++
            ) {
                stringBuilder
                        .append("\n")
                        .append(conversationIndex + 1)
                        .append(": ")
                        .append(conversationNames[conversationIndex]);
            }

            stringBuilder
                    .append("\n\n")
                    .append("Your current chat: ");

            Text feedback =
                    Text.literal(
                                    stringBuilder.toString())
                            .append(Text.literal(PlayerConversationHandler.getCurrentPlayerConversationName(playerID)).
                                    formatted(Formatting.GREEN, Formatting.BOLD)
                            );

            ctx.getSource().sendFeedback(() -> feedback, false);

        }
        catch (Exception e) {
            ChatGPTMode.LOGGER.error("Error while getting conversations", e);
        }

        return 1;
    }

    public static int setConversation (CommandContext<ServerCommandSource> ctx){
        int conversationIndex = ctx.getArgument(Constants.ArgumentNames.CONVERSATION_INDEX,
                Integer.class) - 1; // ? -1 because of 0-indexing
        try {
            String playerID
                    = ctx.getSource().getPlayer().getUuidAsString();

            String[] conversationNames =
                    PlayerConversationHandler.getPlayerConversationNames(playerID);


            if (conversationIndex < 0 || conversationIndex >= conversationNames.length) {
                ctx.getSource().sendFeedback(() ->
                                Text.literal(Constants.FeedbackMessages.CONVERSATION_INVALID_INDEX + "\n\n")
                                        .formatted(Formatting.RED, Formatting.BOLD),
                        false);
                getConversation(ctx);
                return 0;
            }

            PlayerConversationHandler
                    .setCurrentPlayerConversation(conversationNames[conversationIndex], playerID);

            Text feedback =
                    Text.literal(Constants.FeedbackMessages.CONVERSATION_SUCCESSFULLY_SET)
                            .append(
                                    Text.literal(conversationNames[conversationIndex])
                                            .formatted(Formatting.GREEN, Formatting.BOLD)
                            );

            ctx.getSource().sendFeedback(() ->
                    feedback, false);

        }
        catch (Exception e) {
            ChatGPTMode.LOGGER.error("Error while setting conversation", e);
        }
        return 1;

    }

    public static int createConversation (CommandContext<ServerCommandSource> ctx) {
        try {
            String playerID
                    = ctx.getSource().getPlayer().getUuidAsString();

            String conversationName = ctx.getArgument(
                    Constants.ArgumentNames.CONVERSATION_NAME,
                    String.class
            );

            if (Arrays.asList(
                    PlayerConversationHandler.getPlayerConversationNames(playerID)
            ).contains(conversationName)) {
                ctx.getSource().sendFeedback(() ->
                                Text.literal(Constants.FeedbackMessages.CONVERSATION_ALREADY_EXISTS + "\n\n")
                                        .formatted(Formatting.RED, Formatting.BOLD),
                        false);
                return 0;
            }

            PlayerConversationHandler.createPlayerConversation(conversationName, playerID);

            ctx.getSource().sendFeedback(() ->
                            Text.literal(
                                    Constants.FeedbackMessages.CONVERSATION_SUCCESSFULLY_CREATED
                                            + "\n\n"
                            ).formatted(Formatting.GREEN, Formatting.BOLD),
                    false);

            getConversation(ctx);
        }
        catch (Exception e) {
            ChatGPTMode.LOGGER.error("Error while creating conversation", e);
        }

        return 1;
    }

    public static int deleteConversationByIndex(CommandContext<ServerCommandSource> ctx) {
        // TODO: Сделать проверку если удаляется текущий чат
        int conversationIndex = ctx.getArgument(
                Constants.ArgumentNames.CONVERSATION_INDEX,
                Integer.class
        ) - 1; // ? -1 because of 0-indexing
        try {
            String playerID
                    = ctx.getSource().getPlayer().getUuidAsString();

            if (conversationIndex < 0 || conversationIndex >= PlayerConversationHandler.getPlayerConversationNames(playerID).length) {
                ctx.getSource().sendFeedback(() ->
                                Text.literal(Constants.FeedbackMessages.CONVERSATION_INVALID_INDEX)
                                        .formatted(Formatting.RED, Formatting.BOLD),
                        false);
                getConversation(ctx);
                return 0;
            }

            PlayerConversationHandler.deletePlayerConversationByIndex(conversationIndex, playerID);

            ctx.getSource().sendFeedback(() ->
                            Text.literal(
                                    Constants.FeedbackMessages.CONVERSATION_SUCCESSFULLY_DELETED
                                    + "\n\n"
                            ).formatted(Formatting.GREEN, Formatting.BOLD),
                    false);

            getConversation(ctx);

        }
        catch (IOException e) {
            ChatGPTMode.LOGGER.error(
                    Constants.LoggerMessages.CONVERSATION_READING_FILE_ERROR,
                    e.getMessage()
            );
        }

        return 1;
    }

    public static int deleteAllConversations(CommandContext<ServerCommandSource> ctx) {
        try {
            String playerID
                    = ctx.getSource().getPlayer().getUuidAsString();

            PlayerConversationHandler.deleteAllPlayerConversations(playerID);

            ctx.getSource().sendFeedback(() ->
                            Text.literal(
                                    Constants.FeedbackMessages.CONVERSATION_SUCCESSFULLY_DELETED_ALL
                                            + "\n\n"
                            ).formatted(Formatting.GREEN, Formatting.BOLD),
                    false);
        }
        catch (IOException e) {
            ChatGPTMode.LOGGER.error(
                    Constants.LoggerMessages.CONVERSATION_READING_FILE_ERROR,
                    e.getMessage()
            );
        }

        return 1;
    }
}

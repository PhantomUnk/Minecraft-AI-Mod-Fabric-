package mark.gptmode.utils.Commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.datafixers.kinds.Const;
import mark.gptmode.ChatGPTMode;
import mark.gptmode.api.chatGPT;
import mark.gptmode.utils.Constants;
import mark.gptmode.utils.PlayerConversationHandler;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.IOException;
import java.util.Objects;

public class GPT {
    public static int askGpt(CommandContext<ServerCommandSource> ctx) {
        String question = ctx.getArgument(
                Constants.ArgumentNames.GPT_QUESTION,
                String.class
        );
        boolean isGlobal = ctx.getArgument(
                Constants.ArgumentNames.GPT_CHAT_GLOBAL,
                Boolean.class
        );

        if (ctx.getSource().getPlayer() == null) return 0;

        String playerID
                = ctx.getSource().getPlayer().getUuidAsString();

        String playerConversationName =
                PlayerConversationHandler.getCurrentPlayerConversationName(playerID);

        String parentMessageID =
                PlayerConversationHandler.getCurrentPlayerConversationParentMessageID(playerID);

        String playerAIToken =
                    PlayerConversationHandler.getPlayerAIToken(playerID);

        if (playerConversationName == "") {
            ctx.getSource().sendFeedback(() ->
                            Text.literal(Constants.FeedbackMessages.GPT_CONVERSATION_NULL)
                                    .formatted(Formatting.RED, Formatting.BOLD),
                    false);
            return 0;
        }
        else if (playerAIToken == "") {
            ctx.getSource().sendFeedback(() ->
                            Text.literal(Constants.FeedbackMessages.AI_TOKEN_NULL)
                                    .formatted(Formatting.RED, Formatting.BOLD),
                    false);
            return 0;
        }

            ctx.getSource().sendFeedback(() ->
                            Text.literal(Constants.FeedbackMessages.GPT_ACCEPTED_QUESTION)
                                    .formatted(Formatting.GREEN, Formatting.BOLD),
                    false);


            chatGPT.sendRequest(question, playerID, parentMessageID, playerAIToken)
                .thenAcceptAsync(answer -> {
                    if (!isGlobal) { // ! Send only to the player chat
                        String feedback = "Your Request: %s\n\nAnswer: %s"
                                .formatted(question, answer);
                        ctx.getSource().sendFeedback(() -> Text.literal(feedback), false);
                        return;
                    };
                    // ! Else send to all players
                    String feedback = "Player: %s\nAsked: %s\n\nAnswer: %s"
                            .formatted(
                                    Objects.requireNonNull(ctx.getSource().getPlayer()).getName().getString(),
                                    question,
                                    answer
                            );

                    ServerPlayerEntity player = ctx.getSource().getPlayer();

                    if (player == null
                            || player.getServer() == null) return;

                    player.getServer().getPlayerManager().broadcast(Text.literal(feedback), false);

                }).exceptionally(
                        e -> {
                            ctx.getSource().sendFeedback(() -> Text.literal(
                                            Constants.LoggerMessages.GPT_AI_ERROR),
                                    false
                            );
                            ChatGPTMode.LOGGER.error(
                                    Constants.LoggerMessages.GPT_AI_ERROR,
                                    e.getMessage()
                            );
                            return null;
                        });

        return 1;
    }

    public static int setAIToken(CommandContext<ServerCommandSource> ctx) {
        String AIToken = ctx.getArgument(
                Constants.ArgumentNames.AI_TOKEN,
                String.class
        );

        if (ctx.getSource().getPlayer() == null) return 0;

        String playerID
                = ctx.getSource().getPlayer().getUuidAsString();

        try {
            PlayerConversationHandler.setPlayerAIToken(playerID, AIToken);

            ctx.getSource().sendFeedback(() ->
                            Text.literal(
                                    Constants.FeedbackMessages.AI_TOKEN_SET_SUCCESSFULLY
                            ).formatted(Formatting.GREEN, Formatting.BOLD),
                    false);
        }
        catch (IOException e) {
            ctx.getSource().sendFeedback(() ->
                            Text.literal(
                                    Constants.FeedbackMessages.AI_TOKEN_SET_FAILED
                            ).formatted(Formatting.RED, Formatting.BOLD),
                    false);

            ChatGPTMode.LOGGER.error(Constants.LoggerMessages.AI_TOKEN_SET_ERROR, e);
        }


        return 1;
    }
}

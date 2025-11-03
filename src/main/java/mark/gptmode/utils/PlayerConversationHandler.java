package mark.gptmode.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mark.gptmode.ChatGPTMode;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PlayerConversationHandler {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static List<JacksonModels.PlayerConversations.PlayerConversation> loadAllPlayers() {
        File file = Constants.PLAYER_CONVERSATIONS_FILEPATH;
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }

        try {
            return mapper.readValue(file, new TypeReference<>() {});
        } catch (IOException e) {
            ChatGPTMode.LOGGER.error(Constants.LoggerMessages.CONVERSATION_READING_FILE_ERROR, e.getMessage());
            return new ArrayList<>();
        }
    }

    private static void saveAllPlayers(List<JacksonModels.PlayerConversations.PlayerConversation> players) {
        try {
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(Constants.PLAYER_CONVERSATIONS_FILEPATH, players);
        } catch (IOException e) {
            ChatGPTMode.LOGGER.error(Constants.LoggerMessages.CONVERSATION_READING_FILE_ERROR, e.getMessage());
        }
    }

    private static JacksonModels.PlayerConversations.PlayerConversation getOrCreatePlayer(String playerID, List<JacksonModels.PlayerConversations.PlayerConversation> allPlayers) {
        return allPlayers.stream()
                .filter(p -> Objects.equals(p.playerId, playerID))
                .findFirst()
                .orElseGet(() -> {
                    var newPlayer =
                            new JacksonModels.PlayerConversations.PlayerConversation(playerID);
                    newPlayer.conversations = new ArrayList<>();
                    allPlayers.add(newPlayer);
                    return newPlayer;
                });
    }

    public static void registerPlayer() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
        String playerID = handler.player.getUuidAsString();

        List<JacksonModels.PlayerConversations.PlayerConversation> allPlayers = loadAllPlayers();
        getOrCreatePlayer(playerID, allPlayers);
        saveAllPlayers(allPlayers);

        });
    }

    public static String getPlayerAIToken(String playerID) {
        var players = loadAllPlayers();
        var player = getOrCreatePlayer(playerID, players);

        return player.playerAIToken;
    }

    public static void setPlayerAIToken(String playerID, String AIToken) throws IOException {
        var players = loadAllPlayers();
        var player = getOrCreatePlayer(playerID, players);

        player.playerAIToken = AIToken;

        saveAllPlayers(players);
    }

    public static String getCurrentPlayerConversationName(String playerID) {
        var players = loadAllPlayers();
        return getOrCreatePlayer(playerID, players).currentPlayerConversation;
    }


    public static void setCurrentPlayerConversation(String currentPlayerConversation, String playerID) throws IOException {
        var players = loadAllPlayers();
        var player = getOrCreatePlayer(playerID, players);
        player.currentPlayerConversation = currentPlayerConversation;
        saveAllPlayers(players);
    }

    public static String getCurrentPlayerConversationParentMessageID(String playerID)  {
        var allPlayers = loadAllPlayers();
        var player = getOrCreatePlayer(playerID, allPlayers);
        return player.conversations.stream()
                .filter(pc -> Objects.equals(pc.name, player.currentPlayerConversation))
                .findFirst()
                .map(pc -> pc.parentMessageID)
                .orElseGet(() -> {
                    ChatGPTMode.LOGGER.error(Constants.LoggerMessages.CONVERSATION_READING_FILE_ERROR, player.currentPlayerConversation);
                    return "";
                });
    }

    public static void setCurrentPlayerConversationParentMessageID(String playerID, String parentMessageID) throws IOException {
        var allPlayers = loadAllPlayers();
        var player = getOrCreatePlayer(playerID, allPlayers);

        player.conversations.stream()
                .filter(pc -> Objects.equals(pc.name, player.currentPlayerConversation))
                .findFirst()// ? ifPresent - if Optional has value then....
                .ifPresent(pc -> pc.parentMessageID = parentMessageID);

        saveAllPlayers(allPlayers);
    }

    public static String[] getPlayerConversationNames(String playerID) throws IOException {
        var allPlayers = loadAllPlayers();
        var player = getOrCreatePlayer(playerID, allPlayers);

        if (player.conversations == null) return new String[0];

        return player.conversations.stream()
                .map(conversation -> conversation.name)
                .toArray(String[]::new);
    }

    public static void createPlayerConversation(String conversationName, String playerID) throws IOException {
        if (Arrays.asList(getPlayerConversationNames(playerID)).contains(conversationName)) { // ? if conversation already exists
            return;
        }

        var allPlayers = loadAllPlayers();
        var player = getOrCreatePlayer(playerID, allPlayers);

//        if (player.conversations == null) {
//            player.conversations = new ArrayList<>();
//        }

        player.conversations.add(
                new JacksonModels.PlayerConversations.PlayerConversation.Conversation(
                        conversationName,
                        ""
                )
        );

        saveAllPlayers(allPlayers);
    }

    public static void deletePlayerConversationByIndex(int conversationID, String playerID) throws IOException {
        var allPlayers = loadAllPlayers();
        var player = getOrCreatePlayer(playerID, allPlayers);

        if (player.conversations == null || conversationID < 0 || conversationID >= player.conversations.size()) {
            return;
        }

        String conversationName = player.conversations.get(conversationID).name;
        player.conversations.remove(conversationID);

        if (Objects.equals(player.currentPlayerConversation, conversationName)) {
            player.currentPlayerConversation = "";
        }

        saveAllPlayers(allPlayers);
    }

    public static void deleteAllPlayerConversations(String playerID) throws IOException {
        var allPlayers = loadAllPlayers();
        var player = getOrCreatePlayer(playerID, allPlayers);

        player.conversations.clear();
        player.currentPlayerConversation = "";

        saveAllPlayers(allPlayers);
    }

}

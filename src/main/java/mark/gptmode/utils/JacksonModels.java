package mark.gptmode.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class JacksonModels {
    public static final class Answer {
        public String message;
        public String id;
        public String finish_reason;
        public String response_id;

        public String status_code;
        public String error_code;
        public String unauthorized_access;
        public String details;
    }


public static final class PlayerConversations {

    // ? JsonProperty is the original name of the field in the JSON
    @JsonProperty // ? There is no need to specify the name of the field in the JSON
    public List<PlayerConversation> playerConversations;

    public static final class PlayerConversation {
        @JsonProperty("player_id") // ? JsonProperty is the original name of the field in the JSON
        public String playerId;

        @JsonProperty("current_player_conversation")
        public String currentPlayerConversation;

        @JsonProperty("player_ai_token") // ? JsonProperty is the original name of the field in the JSON
        public String playerAIToken;

        @JsonProperty("conversations")
        public List<Conversation> conversations;

        public PlayerConversation() {} // ? Empty constructor is required for Jackson to first Initialize

        public PlayerConversation(String playerID) {
            this.currentPlayerConversation = ""; // ? currentPlayerConversation cannot be null
            this.playerAIToken = ""; // ? playerAIToken cannot be null
            this.playerId = playerID;
        }

        public static final class Conversation {

            public Conversation() {} // ? Empty constructor is required for Jackson to first Initialize

            public Conversation(String name, String id) {
                this.name = name;
                this.parentMessageID = id;
            }

            @JsonProperty("conversation_name")
            public String name;

            @JsonProperty("parent_message_id")
            public String parentMessageID;
        }
    }

    }
}

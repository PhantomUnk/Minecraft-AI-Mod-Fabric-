package mark.gptmode.utils;

import io.github.cdimascio.dotenv.Dotenv;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;

public class Constants {

    public static final String MOD_ID = "chatgptmode";
    public static final File PLAYER_CONVERSATIONS_FILEPATH = new File(
            FabricLoader.getInstance().getConfigDir().toFile(),
            "playerConversations.json"
    );

    public static class AI {
        public static final String URL =
                "https://timeweb.cloud/api/v1/cloud-ai/" +
                        "agents/1b57bf9f-0498-4540-9957-7f48be49c5a6/call";
    }

    public static class CommandNames {
        public static final String GPT_ASK_COMMAND = "askgpt";

        public static final String CONVERSATION_COMMAND = "chat";

        public static final String CONVERSATION_GET = "get";
        public static final String CONVERSATION_SET = "set";

        public static final String CONVERSATION_CREATE = "create"
                ;
        public static final String CONVERSATION_DELETE = "delete";
        public static final String CONVERSATION_DELETE_ALL = "all";
        public static final String CONVERSATION_DELETE_BY_INDEX = "byIndex";

        public static final String AI_COMMAND = "ai";
        public static final String SET_AI_TOKEN = "setToken";
        public static final String GET_AI_TOKEN = "getToken";

    }

    public static class ArgumentNames {
        public static final String GPT_QUESTION = "Your Question";
        public static final String GPT_CHAT_GLOBAL = "Send to global chat?";

        public static final String CONVERSATION_INDEX = "Chat Index";
        public static final String CONVERSATION_NAME = "Chat Name";

        public static final String AI_TOKEN = "AI Token";
    }

    public static class FeedbackMessages {
        public static final String GPT_ACCEPTED_QUESTION = "Your question has been accepted!";
        public static final String GPT_CONVERSATION_NULL = "You don't have any active chat!";

        public static final String CONVERSATION_INVALID_INDEX = "Invalid chat index!";
        public static final String CONVERSATION_SUCCESSFULLY_CREATED = "Your chat has been created!";
        public static final String CONVERSATION_SUCCESSFULLY_SET = "Your chat has been set to: ";
        public static final String CONVERSATION_SUCCESSFULLY_DELETED = "Your chat has been deleted!";
        public static final String CONVERSATION_SUCCESSFULLY_DELETED_ALL = "All chats have been deleted!";
        public static final String CONVERSATION_ALREADY_EXISTS = "This chat already exists!";

        public static final String AI_TOKEN_SET_SUCCESSFULLY = "AI Token has been set successfully!";
        public static final String AI_TOKEN_SET_FAILED = "AI Token has not been set! Something went wrong!";
        public static final String AI_TOKEN_NULL = "You should set AI Token first!";
    }

    public static class LoggerMessages {
        public static final String MOD_LOAD_ERROR = "Error while loading mod!";
        public static final String GPT_AI_ERROR = "Error while contacting AI!";
        public static final String CONVERSATION_READING_FILE_ERROR = "Error while reading playerConversations.json: {}";
        public static final String AI_TOKEN_SET_ERROR = "Error while setting AI Token!";
    }
}

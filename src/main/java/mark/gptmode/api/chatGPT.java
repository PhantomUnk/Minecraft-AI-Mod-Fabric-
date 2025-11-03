package mark.gptmode.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.ObjectMapper;

import mark.gptmode.ChatGPTMode;
import mark.gptmode.utils.Constants;
import mark.gptmode.utils.JacksonModels;
import mark.gptmode.utils.PlayerConversationHandler;

public class chatGPT {
    public static CompletableFuture<String> sendRequest(String prompt, String playerID, String parentMessageID, String playerAIToken) {
        try {

//            String playerAIToken =
//                    PlayerConversationHandler.getPlayerAIToken(playerID);

            HttpClient client = HttpClient.newHttpClient();


            String requestMessage = """
            {
              "message": "%s",
              "parent_message_id": "%s"
            }
        """.formatted(prompt, parentMessageID);


            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Constants.AI.URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + playerAIToken)
//                    .header("Authorization", "Bearer " + Constants.AI.getAIToken())
                    .POST(HttpRequest.BodyPublishers.ofString(requestMessage))
                    .build();

            CompletableFuture<HttpResponse<String>> responseFuture =
                    client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();

            // возвращаем future, а не фиксированное значение
            return responseFuture.thenApply(httpResponse -> {

                if (httpResponse == null || httpResponse.body() == null) {
                    return "Something went wrong";
                }

                ChatGPTMode.LOGGER.info(httpResponse.body());

                try {
                    JacksonModels.Answer answer =
                            mapper.readValue(httpResponse.body(), JacksonModels.Answer.class);

                    PlayerConversationHandler
                            .setCurrentPlayerConversationParentMessageID(playerID, answer.id);

                    return answer.message;
                } catch (Exception e) {
                    ChatGPTMode.LOGGER.error("Exception in Jackson Model: {}", e.getMessage());
                    return "Something went wrong";
                }
            }).exceptionally(ex -> {
                ChatGPTMode.LOGGER.error("Exception: {}", ex.getMessage());
                return "Something went wrong";
            });

        } catch (Exception e) {
            ChatGPTMode.LOGGER.error("Exception: {}", e.getMessage());
            return CompletableFuture.completedFuture("Something went wrong");
        }
    }
}

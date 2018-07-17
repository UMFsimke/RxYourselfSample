package playground.com.playground;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

public class ChatMessageEvent extends ChatEvent {

    private boolean received;
    private boolean sent;
    private String name;
    private String message;

    public static ChatMessageEvent fromPubnub(JsonElement pubnubMessage) {
        JsonObject jsonObject = pubnubMessage.getAsJsonObject();
        JsonObject sender = jsonObject.get("sender").getAsJsonObject();
        String name = sender.get("displayName").getAsString();
        String message = jsonObject.get("message").getAsString();
        boolean received = sender.get("id").getAsString().equals(UserSession.getInstance().getAccountId());

        return new ChatMessageEvent(name, message, received, true);
    }

    public static ChatMessageEvent fromFirebase(RemoteMessage remoteMessage) {
        Map<String, String> jsonObject = remoteMessage.getData();
        String name = jsonObject.get("senderDisplayName");
        String message = jsonObject.get("message");
        boolean received = jsonObject.get("senderId").equals(UserSession.getInstance().getAccountId());

        return new ChatMessageEvent(name, message, received, true);
    }

    public static ChatMessageEvent sent(String message, boolean failedToSend) {
        return new ChatMessageEvent(null, message, false, !failedToSend);
    }

    private ChatMessageEvent(String name, String message, boolean received, boolean sent) {
        this.name = name;
        this.message = message;
        this.received = received;
        this.sent = sent;
    }

    public boolean isReceived() {
        return received;
    }

    public boolean isSent() {
        return sent;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }
}

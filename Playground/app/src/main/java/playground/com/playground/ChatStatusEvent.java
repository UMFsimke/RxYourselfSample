package playground.com.playground;

public class ChatStatusEvent extends ChatEvent {

    enum NetworkStatus {
        ONLINE,
        OFFLINE
    }

    private NetworkStatus networkStatus;

    public static ChatStatusEvent offline() {
        return new ChatStatusEvent(NetworkStatus.OFFLINE);
    }

    public static ChatStatusEvent online() {
        return new ChatStatusEvent(NetworkStatus.ONLINE);
    }

    private ChatStatusEvent(NetworkStatus networkStatus) {
        this.networkStatus = networkStatus;
    }

    public boolean isOnline() {
        return networkStatus == NetworkStatus.ONLINE;
    }


}
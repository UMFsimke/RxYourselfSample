package playground.com.playground;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseChatService extends FirebaseMessagingService {

    private static PublishSubject<ChatEvent> eventsSubject = PublishSubject.create();

    public FirebaseChatService() {
        super();
    }

    public static Observable<ChatEvent> events() {
        return eventsSubject;
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        ChatMessageEvent event = ChatMessageEvent.fromFirebase(remoteMessage);
        eventsSubject.onNext(event);
    }

}
package playground.com.playground;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.util.Arrays;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class RxPubnub {

    public static RxPubnub instance() {
        if (instance == null) {
            instance = new RxPubnub();
        }

        return instance;
    }

    private static RxPubnub instance;

    private PubNub pubnub;
    private Observable<ChatEvent> receivedMessagesStream;
    private PublishSubject<String> sendMessageStream;

    private RxPubnub() {
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey("demo");
        pnConfiguration.setPublishKey("demo");

        pubnub = new PubNub(pnConfiguration);
        initReceivedMessageStream();
        initSendMessageStream();
    }

    private void initReceivedMessageStream() {
        receivedMessagesStream = Observable.create(subscriber -> {
            SubscribeCallback callback = new SubscribeCallback() {
                @Override
                public void status(PubNub pubnub, PNStatus status) {
                    if (subscriber.isDisposed()) { return; }

                    switch (status.getCategory()) {
                        case PNDisconnectedCategory:
                        case PNAccessDeniedCategory:
                        case PNUnexpectedDisconnectCategory:
                            subscriber.onNext(ChatStatusEvent.offline());
                            break;
                        case PNConnectedCategory:
                        case PNReconnectedCategory:
                            subscriber.onNext(ChatStatusEvent.online());
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void message(PubNub pubnub, PNMessageResult message) {
                    if (subscriber.isDisposed()) { return; }

                    subscriber.onNext(ChatMessageEvent.fromPubnub(message.getMessage()));
                }

                @Override
                public void presence(PubNub pubnub, PNPresenceEventResult presence) {
                    //no operation
                }
            };

            pubnub.addListener(callback);
            pubnub.subscribe()
                    .channels(Arrays.asList("awesomeChannel"))
                    .execute();
        });
    }

    private void initSendMessageStream() {
        sendMessageStream = PublishSubject.create();
    }

    public void sendMessage(String message) {
        sendMessageStream.onNext(message);
    }

    public Observable<ChatEvent> getEvents() {
        return Observable.merge(receivedMessagesStream,
                sendMessageStream.flatMap(this::constructSendMessageObservable));
    }

    private Observable<ChatEvent> constructSendMessageObservable(String message) {
        return Observable.create(subscriber -> {
            PNCallback<PNPublishResult> publishListener = new PNCallback<PNPublishResult>() {
                @Override
                public void onResponse(PNPublishResult result, PNStatus status) {
                    if (subscriber.isDisposed()) { return; }

                    ChatMessageEvent event = ChatMessageEvent.sent(message, status.isError());
                    subscriber.onNext(event);
                }
            };

            pubnub.publish()
                    .channel("awesomeChannel")
                    .message(message)
                    .async(publishListener);
        });
    }
}

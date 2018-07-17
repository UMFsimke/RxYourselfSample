package playground.com.playground;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    private CompositeDisposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupObservers();
    }

    private void setupObservers() {
        Disposable chatEvents = Observable.merge(RxPubnub.instance().getEvents(),
                FirebaseChatService.events())
                .ofType(ChatMessageEvent.class)
                .subscribe(chatMessageEvent -> {
                    updateUi(chatMessageEvent);
                }, error -> {
                    Log.d("Error caught", error.getLocalizedMessage());
                });

        Disposable statusEvents = Observable.merge(RxPubnub.instance().getEvents(),
                FirebaseChatService.events())
                .ofType(ChatStatusEvent.class)
                .subscribe(chatStatusEvent -> {
                    updateOnlineStatus(chatStatusEvent);
                }, error -> {
                    Log.d("Error caught", error.getLocalizedMessage());
                });
    }

    private void updateUi(ChatMessageEvent event) {
        //add message to UI list
    }

    private void updateOnlineStatus(ChatStatusEvent event) {
        //add online status
    }
}

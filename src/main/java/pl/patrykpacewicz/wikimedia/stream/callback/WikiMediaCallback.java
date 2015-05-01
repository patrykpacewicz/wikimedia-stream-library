package pl.patrykpacewicz.wikimedia.stream.callback;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import pl.patrykpacewicz.wikimedia.stream.listener.EmptyListener;
import pl.patrykpacewicz.wikimedia.stream.listener.Listener;

public class WikiMediaCallback implements IOCallback {
    private static final Logger LOGGER = Logger.getLogger(WikiMediaCallback.class);
    private static final String SUPPORTED_EVENT = "change";

    private final SocketIO io;
    private final String subscribeChannel;
    private final EmptyListener onDisconnectListener;
    private final EmptyListener onConnectListener;
    private final Listener<Object> onChangeListener;
    private final Listener<SocketIOException> onErrorListener;

    public WikiMediaCallback(
            SocketIO io,
            String subscribeChannel,
            EmptyListener onDisconnectListener,
            EmptyListener onConnectListener,
            Listener<Object> onChangeListener,
            Listener<SocketIOException> onErrorListener
    ) {
        this.io = io;
        this.subscribeChannel = subscribeChannel;
        this.onDisconnectListener = onDisconnectListener;
        this.onConnectListener = onConnectListener;
        this.onChangeListener = onChangeListener;
        this.onErrorListener = onErrorListener;
    }

    @Override
    public void onDisconnect() {
        LOGGER.debug("onDisconnect");
        onDisconnectListener.call();
    }

    @Override
    public void onConnect() {
        LOGGER.debug("onConnect, subscribing to: " + subscribeChannel);
        io.emit("subscribe", subscribeChannel);
        onConnectListener.call();
    }

    @Override
    public void onMessage(String s, IOAcknowledge ioAcknowledge) {
        LOGGER.warn("onMessage is not supported but was executed with " + s + " message");
    }

    @Override
    public void onMessage(JSONObject jsonObject, IOAcknowledge ioAcknowledge) {
        onMessage(jsonObject.toString(), ioAcknowledge);
    }

    @Override
    public void on(String s, IOAcknowledge ioAcknowledge, Object... objects) {
        if (!s.equalsIgnoreCase(SUPPORTED_EVENT)) {
            LOGGER.warn("unsupported message: " + s);
            return;
        }

        LOGGER.debug("on " + s + " was executed with " + objects.length + " messages");
        for (Object object : objects) {
            LOGGER.debug("message: " + object.toString());
            onChangeListener.call(object);
        }
    }

    @Override
    public void onError(SocketIOException e) {
        LOGGER.error("onError", e);
        onErrorListener.call(e);
    }
}

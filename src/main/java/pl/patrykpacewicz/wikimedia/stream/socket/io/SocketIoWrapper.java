package pl.patrykpacewicz.wikimedia.stream.socket.io;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import pl.patrykpacewicz.wikimedia.stream.on.EmptyListener;
import pl.patrykpacewicz.wikimedia.stream.on.Listener;

public class SocketIoWrapper implements IOCallback {
    private final static Logger logger = Logger.getLogger(SocketIoWrapper.class);

    private final SocketIO io;
    private final String wikiName;
    private final EmptyListener onDisconnectListener;
    private final EmptyListener onConnectListener;
    private final Listener<Object> onChangeListener;
    private final Listener<SocketIOException> onErrorListener;

    public SocketIoWrapper(
            SocketIO io,
            String wikiName,
            EmptyListener onDisconnectListener,
            EmptyListener onConnectListener,
            Listener<Object> onChangeListener,
            Listener<SocketIOException> onErrorListener
    ) {
        this.io = io;
        this.wikiName = wikiName;
        this.onDisconnectListener = onDisconnectListener;
        this.onConnectListener = onConnectListener;
        this.onChangeListener = onChangeListener;
        this.onErrorListener = onErrorListener;
    }

    @Override
    public void onDisconnect() {
        logger.debug("onDisconnect");
        onDisconnectListener.call();
    }

    @Override
    public void onConnect() {
        logger.debug("onConnect");
        logger.debug("Subscribing to: " + wikiName);
        io.emit("subscribe", wikiName);
        onConnectListener.call();
    }

    @Override
    public void onMessage(String s, IOAcknowledge ioAcknowledge) {
        logger.warn("onMessage is not supported but was executed with " + s +" message");
    }

    @Override
    public void onMessage(JSONObject jsonObject, IOAcknowledge ioAcknowledge) {
        logger.warn("onMessage is not supported but was executed with " + jsonObject.toString() +" message");
    }

    @Override
    public void on(String s, IOAcknowledge ioAcknowledge, Object... objects) {
        if (!s.equalsIgnoreCase("change")) {
            logger.warn("unsupported message: " + s);
            return;
        }

        logger.debug("on "+ s +" was executed with " + objects.length + " messages");
        for (Object object : objects) {
            logger.debug("message: " + object.toString());
            onChangeListener.call(object);
        }
    }

    @Override
    public void onError(SocketIOException e) {
        logger.error("onError", e);
        onErrorListener.call(e);
    }
}

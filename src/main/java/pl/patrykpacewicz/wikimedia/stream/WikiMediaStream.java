package pl.patrykpacewicz.wikimedia.stream;

import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;
import pl.patrykpacewicz.wikimedia.stream.callback.WikiMediaCallback;
import pl.patrykpacewicz.wikimedia.stream.listener.EmptyListener;
import pl.patrykpacewicz.wikimedia.stream.listener.Listener;
import pl.patrykpacewicz.wikimedia.stream.listener.Listeners;

import java.net.MalformedURLException;

public class WikiMediaStream {
    private final IOCallback ioCallback;
    private final SocketIO socketIO;

    private WikiMediaStream(IOCallback ioCallback, SocketIO socketIO) {
        this.ioCallback = ioCallback;
        this.socketIO = socketIO;
    }

    public static Builder builder() { return new Builder(); }

    public IOCallback getIoCallback() {
        return ioCallback;
    }

    public SocketIO getSocketIO() {
        return socketIO;
    }

    public void connect() {
        socketIO.connect(ioCallback);
    }

    public void disconnect() {
        socketIO.disconnect();
    }

    private static class Builder {
        private String wikimediaStreamUlr = "http://stream.wikimedia.org/rc";
        private String subscribeWikiName = "commons.wikimedia.org";
        private EmptyListener onDisconnectListener = Listeners.emptyListener;
        private EmptyListener onConnectListener = Listeners.emptyListener;
        private Listener<Object> onChangeListener = Listeners.objectListener;
        private Listener<SocketIOException> onErrorListener = Listeners.exceptionListener;

        public Builder mikimediaStreamUlr(String wikimediaStreamUlr) {
            this.wikimediaStreamUlr = wikimediaStreamUlr;
            return this;
        }

        public Builder subscribeWikiName(String subscribeWikiName) {
            this.subscribeWikiName = subscribeWikiName;
            return this;
        }

        public Builder onDisconnect(EmptyListener onDisconnectListener) {
            this.onDisconnectListener = onDisconnectListener;
            return this;
        }

        public Builder onConnect(EmptyListener onConnectListener) {
            this.onConnectListener = onConnectListener;
            return this;
        }

        public Builder onChange(Listener<Object> onChangeListener) {
            this.onChangeListener = onChangeListener;
            return this;
        }

        public Builder onError(Listener<SocketIOException> onErrorListener) {
            this.onErrorListener = onErrorListener;
            return this;
        }

        public WikiMediaStream build() throws MalformedURLException {
            SocketIO io = new SocketIO(wikimediaStreamUlr);
            WikiMediaCallback wikiMediaCallback = new WikiMediaCallback(
                    io, subscribeWikiName, onDisconnectListener,
                    onConnectListener, onChangeListener, onErrorListener
            );

            return new WikiMediaStream(wikiMediaCallback, io);
        }
    }
}

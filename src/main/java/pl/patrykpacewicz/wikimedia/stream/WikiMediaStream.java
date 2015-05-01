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
        private String wikimediaStreamUrl = "http://stream.wikimedia.org/rc";
        private String subscribeChannel = "commons.wikimedia.org";
        private EmptyListener onDisconnectListener = Listeners.emptyListener;
        private EmptyListener onConnectListener = Listeners.emptyListener;
        private Listener<Object> onChangeListener = Listeners.objectListener;
        private Listener<SocketIOException> onErrorListener = Listeners.exceptionListener;

        public Builder mikimediaStreamUrl(String wikimediaStreamUrl) {
            this.wikimediaStreamUrl = wikimediaStreamUrl;
            return this;
        }

        public Builder subscribeChannel(String subscribeChannel) {
            this.subscribeChannel = subscribeChannel;
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
            SocketIO io = new SocketIO(wikimediaStreamUrl);
            WikiMediaCallback wikiMediaCallback = new WikiMediaCallback(
                    io, subscribeChannel, onDisconnectListener,
                    onConnectListener, onChangeListener, onErrorListener
            );

            return new WikiMediaStream(wikiMediaCallback, io);
        }
    }
}

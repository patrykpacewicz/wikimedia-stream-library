package pl.patrykpacewicz.wikimedia.stream;

import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;
import pl.patrykpacewicz.wikimedia.stream.on.EmptyListener;
import pl.patrykpacewicz.wikimedia.stream.on.Listener;
import pl.patrykpacewicz.wikimedia.stream.on.Listeners;
import pl.patrykpacewicz.wikimedia.stream.socket.io.SocketIoWrapper;

import java.net.MalformedURLException;

public class WikiMediaStream {
    private final IOCallback ioCallback;
    private final SocketIO socketIO;

    public static Builder builder() { return new Builder(); }

    private WikiMediaStream(IOCallback ioCallback, SocketIO socketIO) {
        this.ioCallback = ioCallback;
        this.socketIO = socketIO;
    }

    public IOCallback getIoCallback() {
        return ioCallback;
    }

    public SocketIO getSocketIO() {
        return socketIO;
    }

    public void connect() {
        socketIO.connect(ioCallback);
    }

    public static class Builder {
        private String wikimediaStreamUlr = "http://stream.wikimedia.org/rc";
        private String wikiToListen = "commons.wikimedia.org";
        private EmptyListener onDisconnectListener = Listeners.emptyListener;
        private EmptyListener onConnectListener = Listeners.emptyListener;
        private Listener<Object> onChangeListener = Listeners.objectListener;
        private Listener<SocketIOException> onErrorListener = Listeners.exceptionListener;

        public Builder mikimediaStreamUlr(String wikimediaStreamUlr) {
            this.wikimediaStreamUlr = wikimediaStreamUlr;
            return this;
        }

        public Builder wikiToListen(String wikiToListen) {
            this.wikiToListen = wikiToListen;
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
            SocketIoWrapper socketIoWrapper = new SocketIoWrapper(
                    io, wikiToListen, onDisconnectListener, onConnectListener, onChangeListener, onErrorListener
            );

            return new WikiMediaStream(socketIoWrapper, io);
        }
    }
}

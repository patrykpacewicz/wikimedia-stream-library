package pl.patrykpacewicz.wikimedia.stream.on;

import io.socket.SocketIOException;

public class Listeners {
    public static final Listener<Object> objectListener = new Listener<Object>() {
        @Override public void call(Object data) { }
    };
    public static final Listener<SocketIOException> exceptionListener = new Listener<SocketIOException>() {
        @Override public void call(SocketIOException data) { }
    };
    public static final EmptyListener emptyListener = new EmptyListener() {
        @Override public void call() { }
    };

    private Listeners() {}
}

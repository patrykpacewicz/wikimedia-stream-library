package pl.patrykpacewicz.wikimedia.stream.on;

public interface Listener<T> {
    void call(T data);
}

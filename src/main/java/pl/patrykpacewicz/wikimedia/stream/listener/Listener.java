package pl.patrykpacewicz.wikimedia.stream.listener;

public interface Listener<T> {
    void call(T data);
}

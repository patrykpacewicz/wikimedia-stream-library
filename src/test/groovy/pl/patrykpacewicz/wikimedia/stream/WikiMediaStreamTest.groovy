package pl.patrykpacewicz.wikimedia.stream

import pl.patrykpacewicz.wikimedia.stream.listener.EmptyListener
import pl.patrykpacewicz.wikimedia.stream.listener.Listener
import pl.patrykpacewicz.wikimedia.stream.listener.Listeners
import spock.lang.Specification

class WikiMediaStreamTest extends Specification {
    def 'should create WikiMediaStream using builder'() {
        given:
        def url = "http://any.url.com"
        def subscribeWikiName = "*.wikipedia.org"
        def onDisconnectListener = Mock(EmptyListener)
        def onConnectListener = Mock(EmptyListener)
        def onChangeListener = Mock(Listener)
        def onErrorListener = Mock(Listener)

        when:
        def wikiMediaStream = WikiMediaStream.builder()
            .subscribeChannel(subscribeWikiName)
            .mikimediaStreamUrl(url)
            .onDisconnect(onDisconnectListener)
            .onConnect(onConnectListener)
            .onChange(onChangeListener)
            .onError(onErrorListener)
            .build()

        then:
        wikiMediaStream.socketIO.url == new URL(url)
        wikiMediaStream.ioCallback.io == wikiMediaStream.socketIO
        wikiMediaStream.ioCallback.subscribeChannel == subscribeWikiName
        wikiMediaStream.ioCallback.onDisconnectListener == onDisconnectListener
        wikiMediaStream.ioCallback.onConnectListener == onConnectListener
        wikiMediaStream.ioCallback.onChangeListener == onChangeListener
        wikiMediaStream.ioCallback.onErrorListener == onErrorListener
    }

    def 'should create WikiMediaStream with default values'() {
        when:
        def wikiMediaStream = WikiMediaStream.builder().build()

        then:
        wikiMediaStream.socketIO.url == new URL('http://stream.wikimedia.org/rc')
        wikiMediaStream.ioCallback.io == wikiMediaStream.socketIO
        wikiMediaStream.ioCallback.subscribeChannel == 'commons.wikimedia.org'
        wikiMediaStream.ioCallback.onDisconnectListener == Listeners.emptyListener
        wikiMediaStream.ioCallback.onConnectListener == Listeners.emptyListener
        wikiMediaStream.ioCallback.onChangeListener == Listeners.objectListener
        wikiMediaStream.ioCallback.onErrorListener == Listeners.exceptionListener
    }
}

package pl.patrykpacewicz.wikimedia.stream.callback

import io.socket.SocketIO
import io.socket.SocketIOException
import org.apache.log4j.Appender
import org.apache.log4j.Level
import org.apache.log4j.LogManager
import org.apache.log4j.spi.LoggingEvent
import org.json.JSONObject
import pl.patrykpacewicz.wikimedia.stream.listener.EmptyListener
import pl.patrykpacewicz.wikimedia.stream.listener.Listener
import spock.lang.Specification

class WikiMediaCallbackTest extends Specification {
    def loggerAppender = Mock(Appender)

    def io = Mock(SocketIO)
    def wikiName = 'any-wiki-name'
    def onDisconnectListener = Mock(EmptyListener)
    def onConnectListener = Mock(EmptyListener)
    def onChangeListener = Mock(Listener)
    def onErrorListener = Mock(Listener)

    def wikiMediaCallback = new WikiMediaCallback(
        io, wikiName, onDisconnectListener, onConnectListener,
        onChangeListener, onErrorListener
    )

    def setup() {
        LogManager.rootLogger.addAppender(loggerAppender);
    }

    def cleanup() {
        LogManager.rootLogger.removeAppender(loggerAppender);
    }

    def 'should execute onDisconnect callback'() {
        when:
        wikiMediaCallback.onDisconnect()

        then:
        1 * onDisconnectListener.call()
    }

    def 'should execute onConnect callback and emit subscribe message'() {
        when:
        wikiMediaCallback.onConnect()

        then:
        1 * onConnectListener.call()
        1 * io.emit('subscribe', wikiName)
    }

    def 'should log warning message when onMessage method was used' () {
        given:
        def msg = 'example message'

        when:
        wikiMediaCallback.onMessage(msg, null)

        then:
        1 * loggerAppender.doAppend { LoggingEvent loggingEvent ->
            assert loggingEvent.level == Level.WARN
            assert loggingEvent.renderedMessage.contains('onMessage')
            assert loggingEvent.renderedMessage.contains('is not supported')
            loggingEvent.renderedMessage.contains(msg)
        }
    }

    def 'should log warning message when onMessage method was used with JSONObject' () {
        given:
        def msg = '{"example":"message"}'
        def jsonMsg = new JSONObject(msg)

        when:
        wikiMediaCallback.onMessage(jsonMsg, null)

        then:
        1 * loggerAppender.doAppend { LoggingEvent loggingEvent ->
            assert loggingEvent.level == Level.WARN
            assert loggingEvent.renderedMessage.contains('onMessage')
            assert loggingEvent.renderedMessage.contains('is not supported')
            loggingEvent.renderedMessage.contains(msg)
        }
    }

    def 'should execute onError callback and log error message'() {
        given:
        def exception = new SocketIOException('any msg')

        when:
        wikiMediaCallback.onError(exception)

        then:
        1 * onErrorListener.call(exception)
        1 * loggerAppender.doAppend { LoggingEvent loggingEvent ->
            assert loggingEvent.level == Level.ERROR
            loggingEvent.renderedMessage.contains('onError')
        }
    }

    def 'should execute onChange callback'() {
        given:
        def msg = '{"some":"wikimedia", "json":"data"}'

        when:
        wikiMediaCallback.on('change', null, msg, msg, msg)

        then:
        3 * onChangeListener.call(msg)
    }

    def '''
            should not execute onChange callback
            and should call warning log message
            when message type is different from change
        '''() {
        given:
        def msg = '{"some":"wikimedia", "json":"data"}'

        when:
        wikiMediaCallback.on('not-change', null, msg, msg, msg)

        then:
        0 * onChangeListener.call(msg)
        1 * loggerAppender.doAppend { LoggingEvent loggingEvent ->
            assert loggingEvent.level == Level.WARN
            assert loggingEvent.renderedMessage.contains('unsupported message')
            loggingEvent.renderedMessage.contains('not-change')
        }
    }

}

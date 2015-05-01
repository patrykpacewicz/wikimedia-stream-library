wikimedia-stream-library
========================

[![Build Status](https://travis-ci.org/patrykpacewicz/wikimedia-stream-library.svg?branch=master)](https://travis-ci.org/patrykpacewicz/wikimedia-stream-library)
[ ![Download](https://api.bintray.com/packages/patrykpacewicz/maven/wikimedia-stream-library/images/download.svg) ](https://bintray.com/patrykpacewicz/maven/wikimedia-stream-library/_latestVersion)

wikimedia-stream-library is a library getting an edit stream
of wikimedia based pages (e.g, *en.wikipedia.org*).

Binaries
--------

Example for Gradle:

```groovy
repositories {
    maven { url "https://dl.bintray.com/patrykpacewicz/maven" }
}
dependencies {
    compile 'pl.patrykpacewicz:wikimedia-stream-library:0.1.0'
}
```

Implementation details
----------------------

The library works through socket.io wikimedia server connection.
Details about the api and the data structures can be found in the mediawiki documentation.
- https://wikitech.wikimedia.org/wiki/RCStream
- https://www.mediawiki.org/wiki/Manual:RCFeed

How to use:
-----------

Configure and set listeners using `WikiMediaStream.builder()` and then `connect()` to the source of events

```java
WikiMediaStream wikiMediaStream = WikiMediaStream.builder()
    .mikimediaStreamUrl(...)
    .subscribeChannel(...)
    .onDisconnect(...)
    .onConnect(...)
    .onChange(...)
    .onError(...)
.build()

wikiMediaStream.connect();
wikiMediaStream.disconnect();
```

# Example usage

Simple example of listening on `*.wikipedia.org` channels and printing out the data.

```java
import pl.patrykpacewicz.wikimedia.stream.WikiMediaStream;

public class Start {
    public static void main(String[] args) throws Exception {
        WikiMediaStream.builder()
            .subscribeChannel("*.wikipedia.org")
            .onChange(System.out::println)
            .build().connect();
    }
}

```

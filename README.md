# Example usage

```java
import pl.patrykpacewicz.wikimedia.stream.WikiMediaStream;

public class Start {
    public static void main(String[] args) throws Exception {
        WikiMediaStream.builder()
            .onChange(System.out::println)
            .build().connect();
    }
}


```

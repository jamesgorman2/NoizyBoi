import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.jyre.ZreEvent;
import org.jyre.ZreInterface;
import org.zeromq.api.Message;
import org.zeromq.api.Message.Frame;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Boi {
  public static void main(String[] args) throws InterruptedException {
    ZreInterface boi = new ZreInterface();
    boi.start();
    System.out.println(boi.getUuid());
    System.out.println("****************************************************");
    System.out.println(" My name    : " + boi.getName());
    System.out.println(" My uuid    : " + boi.getUuid());
    System.out.println("****************************************************");

    boi.join("boiz");

    Subject<ZreEvent> receivedMessages = PublishSubject.create();

    receivedMessages
      .map(
        e ->
          "Got message:" +
          "  Type: " + e.getEventType().name() + "\n" +
          "  From: " + e.getPeer() + "\n" +
          "  Msg: " + Optional.ofNullable(e.getContent())
            .flatMap(
              c -> Optional.ofNullable(c.getFrames())
            )
            .map(fs -> fs.stream().map(Frame::toString).collect(Collectors.joining()))
            .orElse("<NONE>")
      )
      .subscribe(
        System.out::println,
        t -> {
          System.err.println(t.getMessage());
          t.printStackTrace();
        }
      );

    // Talk every second
    Flowable.interval(2, TimeUnit.SECONDS)
      .doOnNext(
        i -> {
          if (i % 2 == 0) {
            System.out.println("Being noisy...");
            boi.shout("boiz", new Message("HEY"));
          } else if (!boi.getPeers().isEmpty()) {
            System.out.println("Being quiet...");
            String p = boi.getPeers().get(new Random().nextInt(boi.getPeers().size()));
            boi.whisper(p, new Message("hey"));
          }
        }
      )
      .subscribe(
        i -> {},
        t -> {
          System.err.println(t.getMessage());
          t.printStackTrace();
        }
      );


    // Check peers
    Flowable.interval(2, TimeUnit.SECONDS)
      .doOnNext(
        i -> System.out.println("Peers: " + boi.getPeers().toString())
      )
      .subscribe(
        i -> {},
        t -> {
          System.err.println(t.getMessage());
          t.printStackTrace();
        }
      );

    while (true) {
      System.out.println("Waiting for message...");
      ZreEvent incoming = boi.receive();
      receivedMessages.onNext(incoming);
    }
  }
}

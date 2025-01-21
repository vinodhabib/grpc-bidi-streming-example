
import com.example.hello.HelloServiceGrpc;
import com.example.hello.HelloStreamProto.HelloRequest;
import com.example.hello.HelloStreamProto.HelloResponse;
import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class HelloClient {

  public static void main(String[] args) throws InterruptedException {
    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
        .usePlaintext()
        .build();

    // Start a new thread to monitor and print the channel's state
    new Thread(() -> monitorChannelState(channel)).start();

    HelloServiceGrpc.HelloServiceStub asyncStub = HelloServiceGrpc.newStub(channel);

    CountDownLatch latch = new CountDownLatch(1);

    StreamObserver<HelloRequest> requestObserver = asyncStub.sayHello(
        new StreamObserver<HelloResponse>() {
          @Override
          public void onNext(HelloResponse response) {
            System.out.println("Received: " + response.getMessage());
          }

          @Override
          public void onError(Throwable t) {
            t.printStackTrace();
            latch.countDown();
          }

          @Override
          public void onCompleted() {
            System.out.println("Stream completed.");
            latch.countDown();
          }
        });

    for (String name : new String[]{"Alice", "Bob", "Charlie"}) {
      HelloRequest request = HelloRequest.newBuilder().setName(name).build();
      requestObserver.onNext(request);
      Thread.sleep(500); // Simulate some delay
    }

    requestObserver.onCompleted();
    latch.await(1, TimeUnit.MINUTES);

    channel.shutdown();
  }

  private static void monitorChannelState(ManagedChannel channel) {
    while (true) {
      ConnectivityState state = channel.getState(true); // Request to reconnect if idle
      System.out.println("Channel state: " + state);

      // If the state is TRANSIENT_FAILURE, you can also print additional details or handle reconnections.
      if (state == ConnectivityState.TRANSIENT_FAILURE) {
        System.out.println("Channel in TRANSIENT_FAILURE state. Trying to recover...");
      }

      // Sleep for a short duration to avoid busy waiting
      try {
        Thread.sleep(1000); // Check the state every second
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
      }
    }
  }

}




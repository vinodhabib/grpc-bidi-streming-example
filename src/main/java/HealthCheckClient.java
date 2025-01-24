import com.example.healthCheck.HealthCheckGrpc;
import com.example.healthCheck.HealthGrpc;
import com.example.healthCheck.HealthGrpc.HealthBlockingStub;
import com.example.healthCheck.HelloHealthCheckProto.HealthStatus;
import com.example.healthCheck.HelloHealthCheckProto.HelloReply;
import com.example.healthCheck.HelloHealthCheckProto.HelloRequest;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class HealthCheckClient {

  public static void main(String[] args) {
    // Create a channel to communicate with the server
    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
        .usePlaintext()
        .build();

    // Create a stub for the Greeter service
    HealthCheckGrpc.HealthCheckBlockingStub healthCheckStub = HealthCheckGrpc.newBlockingStub(channel);
    HelloRequest request = HelloRequest.newBuilder().setName("World").build();
    HelloReply reply = healthCheckStub.sayHello(request);

    System.out.println("Greeting: " + reply.getMessage());

    // Create a stub for the Health service
    HealthBlockingStub healthStub = HealthGrpc.newBlockingStub(channel);
    HealthStatus healthStatus = healthStub.check(Empty.getDefaultInstance());

    System.out.println("Health Status: " + healthStatus.getStatus());

    // Shutdown the channel
    channel.shutdown();
  }

}

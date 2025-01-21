import com.example.helloworldexecutor.GreeterGrpc;
import com.example.helloworldexecutor.HelloWorldExecutorProto.HelloReply;
import com.example.helloworldexecutor.HelloWorldExecutorProto.HelloRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ExecutorHelloWorldClient {

  public static void main(String[] args) {
    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
        .usePlaintext()
        .build();

    GreeterGrpc.GreeterBlockingStub stub = GreeterGrpc.newBlockingStub(channel);

    HelloRequest request = HelloRequest.newBuilder().setName("World").build();
    HelloReply reply = stub.sayHello(request);

    System.out.println("Response from server: " + reply.getMessage());
    channel.shutdown();
  }

}

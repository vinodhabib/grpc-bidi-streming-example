
import com.example.hello.HelloServiceGrpc;
import com.example.hello.HelloStreamProto.HelloRequest;
import com.example.hello.HelloStreamProto.HelloResponse;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;

public class HelloServer {

  public static void main(String[] args) throws IOException, InterruptedException {
    Server server = ServerBuilder.forPort(50051)
        .addService(new HelloServiceImpl())
        .build();
    System.out.println("Starting server...");
    server.start();
    System.out.println("Server started on port 50051.");
    Thread.sleep(30000);
    server.shutdown();
    //server.awaitTermination();
  }

  static class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {

    @Override
    public StreamObserver<HelloRequest> sayHello(StreamObserver<HelloResponse> responseObserver) {
      return new StreamObserver<HelloRequest>() {
        @Override
        public void onNext(HelloRequest request) {
          String message = "Hello, " + request.getName() + "!";
          HelloResponse response = HelloResponse.newBuilder()
              .setMessage(message)
              .build();
          responseObserver.onNext(response);
        }

        @Override
        public void onError(Throwable t) {
          t.printStackTrace();
        }

        @Override
        public void onCompleted() {
          responseObserver.onCompleted();
        }
      };
    }
  }

}

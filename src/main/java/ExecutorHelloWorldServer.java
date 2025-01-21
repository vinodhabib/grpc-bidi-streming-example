import com.example.helloworldexecutor.GreeterGrpc;
import com.example.helloworldexecutor.HelloWorldExecutorProto.HelloReply;
import com.example.helloworldexecutor.HelloWorldExecutorProto.HelloRequest;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import io.netty.util.concurrent.DefaultThreadFactory;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorHelloWorldServer {

  private static ThreadPoolExecutor executor =
      new ThreadPoolExecutor(8, 8, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
          new DefaultThreadFactory("proxy-test-pool", true));
  public static void main(String[] args) throws Exception {
    Server server = ServerBuilder.forPort(50051).executor(executor).addService(new GreeterImpl())
        .build()
        .start();

    /*.forPort(50051)
        .addService(new GreeterImpl())
        .build()
        .start();*/



    System.out.println("Server started on port 50051");
    server.awaitTermination();
  }

  static class GreeterImpl extends GreeterGrpc.GreeterImplBase {
    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
      String greeting = "Hello, " + request.getName();
      HelloReply reply = HelloReply.newBuilder().setMessage(greeting).build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }
  }

}

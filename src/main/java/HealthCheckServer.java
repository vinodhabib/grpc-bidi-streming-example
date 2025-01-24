import com.example.healthCheck.HealthCheckGrpc;
import com.example.healthCheck.HealthGrpc;
import com.example.healthCheck.HelloHealthCheckProto.HealthStatus;
import com.example.healthCheck.HelloHealthCheckProto.HelloReply;
import com.example.healthCheck.HelloHealthCheckProto.HelloRequest;
import com.google.protobuf.Empty;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.HealthStatusManager;
import io.grpc.stub.StreamObserver;


public class HealthCheckServer {

  public static void main(String[] args) throws Exception {
    // Create a HealthStatusManager instance
    HealthStatusManager healthStatusManager = new HealthStatusManager();

    // Build and start the server
    Server server = ServerBuilder
        .forPort(50051)
        .addService(new HealthCheckImpl())
        .addService(healthStatusManager.getHealthService())
        .build();

    // Start the server
    server.start();

    // Set initial health status to "SERVING"
    healthStatusManager.setStatus("", io.grpc.health.v1.HealthCheckResponse.ServingStatus.SERVING);

    System.out.println("Server started, listening on port 50051");

    // Add a shutdown hook to cleanly terminate the server
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.out.println("Shutting down the server...");
      healthStatusManager.clearStatus(""); // Clear health status
      server.shutdown();
    }));

    // Keep the server running
    server.awaitTermination();
  }

  // Greeter service implementation
  static class HealthCheckImpl extends HealthCheckGrpc.HealthCheckImplBase {
    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
      String greeting = "Hello, " + request.getName();
      HelloReply reply = HelloReply.newBuilder().setMessage(greeting).build();

      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }
  }

 /* // Health check service implementation
  static class HealthServiceImpl extends HealthGrpc.HealthImplBase {
    public void check(Empty request, StreamObserver<HealthStatus> responseObserver) {
      HealthStatus status = HealthStatus.newBuilder().setStatus("SERVING").build();
      responseObserver.onNext(status);
      responseObserver.onCompleted();
    }
  }*/
}

import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command()
public class CLI implements Runnable {

    @Command(name="start", description="Start a node and listen for requests")
    void startServer(
        @Option(names={ "-p", "--port"}, description="The port to listen on", arity="1")
        int port
    ) {
        System.out.println("Node started on port " + port);
    }

    @Command(name="list-neighbors", description="Give a list of neighbors")
    public void listNeighbors() {
        System.out.println("Neighbor 1");
        System.out.println("Neighbor 2");
        System.out.println("Neighbor 3");
    }
    public void run() {
        System.out.println("Distributed Content Sharing");
    }

    public static void main(String[] args) {
        CommandLine.run(new CLI(), args);
    }
}
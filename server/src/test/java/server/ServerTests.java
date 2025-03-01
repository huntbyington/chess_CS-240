package server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ServerTests {

    private Server myServer;

    @BeforeEach
    void setUp() {
        myServer = new Server();
    }

    @AfterEach
    void tearDown() {
        if(myServer != null) {
            myServer.stop();
        }
    }

    @Test
    @DisplayName("Server Boot Up Test")
    public void serverBootUpTest() {
        myServer.run(8080);
    }

}

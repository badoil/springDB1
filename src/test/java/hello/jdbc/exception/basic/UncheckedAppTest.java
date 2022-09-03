package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

@Slf4j
public class UncheckedAppTest {


    @Test
    void Checked() {
        Controller controller = new Controller();
        Assertions.assertThatThrownBy(() -> controller.request()).isInstanceOf(Exception.class);
    }

    static class Controller {
        Service service = new Service();

        public void request() throws SQLException, ConnectException {
            service.logic();
        }
    }

    static class Service {
        Repository repository = new Repository();
        ConnectClient connectClient = new ConnectClient();

        public void logic() throws SQLException, ConnectException {
            repository.call();
            connectClient.call();
        }
    }

    static class Repository {
        public void call() throws SQLException {
            throw new SQLException("EX");
        }
    }

    static class ConnectClient {
        public void call() throws ConnectException {
            throw new ConnectException("EX");
        }
    }
}

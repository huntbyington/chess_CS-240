package dataaccess;

/*
If a data access method fails it throws this exception
 */

public class DataAccessException extends RuntimeException {
    public DataAccessException(String message) {
        super(message);
    }
}

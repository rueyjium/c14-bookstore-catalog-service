package lab.bookstore.catalogservice.domain;

public class BookNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BookNotFoundException(String isbn) {
        super("The book with ISBN " + isbn + " was not found.");
    }

}

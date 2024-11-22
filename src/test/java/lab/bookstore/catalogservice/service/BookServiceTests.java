package lab.bookstore.catalogservice.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import lab.bookstore.catalogservice.domain.Book;
import lab.bookstore.catalogservice.domain.BookAlreadyExistsException;
import lab.bookstore.catalogservice.domain.BookNotFoundException;
import lab.bookstore.catalogservice.repo.BookRepository;

@ExtendWith(MockitoExtension.class)
class BookServiceTests {

	@Mock
	private BookRepository bookRepository;

	@InjectMocks
	private BookService bookService;

	@Test
	void should_throw_BookAlreadyExistsException_when_book_to_create_already_exist() {
		// given
		var isbn = "1234567890";
		var bookToCreate = Book.of(isbn, "java", "jim", 888.8, "Gotop");
		// when
		when(bookRepository.existsByIsbn(isbn)).thenReturn(true);
		// then
		assertThatThrownBy(() -> bookService.addBookToCatalog(bookToCreate))
				.isInstanceOf(BookAlreadyExistsException.class)
				.hasMessage("A book with ISBN " + isbn + " already exists.");
	}

	@Test
	void should_throw_BookNotFoundException_when_book_to_get_not_exist() {
		// given
		var isbn = "1234567890";
		// when
		when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.empty());
		// then
		assertThatThrownBy(() -> bookService.viewBookDetails(isbn))
				.isInstanceOf(BookNotFoundException.class)
				.hasMessage("The book with ISBN " + isbn + " was not found.");
	}

}

package lab.bookstore.catalogservice.repo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import lab.bookstore.catalogservice.config.DataConfig;
import lab.bookstore.catalogservice.domain.Book;

@DataJdbcTest
@Import(DataConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("integration")
class BookRepositoryJdbcTests {

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private JdbcAggregateTemplate jdbc;

	@Test
	void should_findAll_get_books_when_books_were_inserted() {
		// given
		var book1 = Book.of("1234567895", "spring boot", "jim", 888.8, "Gotop");
		var book2 = Book.of("1234567896", "spring cloud", "jim", 888.8, "Gotop");
		jdbc.insert(book1);
		jdbc.insert(book2);
		// when
		Iterable<Book> createdBooks = bookRepository.findAll();
		// then
		assertThat(StreamSupport.stream(createdBooks.spliterator(), true)
				.filter(book -> book.isbn().equals(book1.isbn()) || book.isbn().equals(book2.isbn()))
				.collect(Collectors.toList())).hasSize(2);
	}

	@Test
	void should_findByIsbn_get_book_when_isbn_is_existing() {
		// given
		var bookIsbn = "1234567895";
		var book = Book.of(bookIsbn, "spring cloud", "jim", 888.8, "Gotop");
		jdbc.insert(book);
		// when
		Optional<Book> createdBook = bookRepository.findByIsbn(bookIsbn);
		// then
		assertThat(createdBook).isPresent();
		assertThat(createdBook.get().isbn()).isEqualTo(book.isbn());
	}

	@Test
	void should_findByIsbn_not_get_book_when_isbn_is_not_existing() {
		// given, when
		Optional<Book> createdBook = bookRepository.findByIsbn("1234567899");
		// then
		assertThat(createdBook).isEmpty();
	}

	@Test
	void should_existsByIsbn_get_true_when_isbn_is_existing() {
		// given
		var bookIsbn = "1234567895";
		var bookToCreate = Book.of(bookIsbn, "spring cloud", "jim", 888.8, "Gotop");
		jdbc.insert(bookToCreate);
		// when
		boolean existing = bookRepository.existsByIsbn(bookIsbn);
		// then
		assertThat(existing).isTrue();
	}

	@Test
	void should_existsByIsbn_get_false_when_isbn_is_not_existing() {
		// given, when
		boolean existing = bookRepository.existsByIsbn("1234567895");
		// then
		assertThat(existing).isFalse();
	}
	
    @Test
    void whenCreateBookNotAuthenticatedThenNoAuditMetadata() {
        var bookToCreate = Book.of("1232343456", "Title", "Author", 12.90, "GoTop");
        var createdBook = bookRepository.save(bookToCreate);

        assertThat(createdBook.createdBy()).isNull();
        assertThat(createdBook.lastModifiedBy()).isNull();
    }

    @Test
    @WithMockUser("john")
    void whenCreateBookAuthenticatedThenAuditMetadata() {
        var bookToCreate = Book.of("1232343457", "Title", "Author", 12.90, "GoTop");
        var createdBook = bookRepository.save(bookToCreate);

        assertThat(createdBook.createdBy()).isEqualTo("john");
        assertThat(createdBook.lastModifiedBy()).isEqualTo("john");
    }

	@Test
	void should_deleteByIsbn_delete_book_when_isbn_is_existing() {
		// given
		var bookIsbn = "1234567895";
		var bookToCreate = Book.of(bookIsbn, "spring cloud", "jim", 888.8, "Gotop");
		var createdBook = jdbc.insert(bookToCreate);
		// when
		bookRepository.deleteByIsbn(bookIsbn);
		// then
		assertThat(jdbc.findById(createdBook.id(), Book.class)).isNull();
	}

}

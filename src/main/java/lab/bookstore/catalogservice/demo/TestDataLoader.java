package lab.bookstore.catalogservice.demo;

import java.util.List;

import lab.bookstore.catalogservice.domain.Book;
import lab.bookstore.catalogservice.repo.BookRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Profile("load-test-data")
public class TestDataLoader {

	@Autowired
	private BookRepository bookRepository;

	@EventListener(ApplicationReadyEvent.class)
	public void loadBookTestData() {
		bookRepository.deleteAll();
		var book1 = Book.of("1234567890", "Spring Boot", "Jim1", 100.0, "Gotop");
		var book2 = Book.of("1234567891", "Spring Cloud", "Jim2", 200.0, "Gotop");
		bookRepository.saveAll(List.of(book1, book2));
	}

}

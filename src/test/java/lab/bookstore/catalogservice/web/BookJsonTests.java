package lab.bookstore.catalogservice.web;

import java.time.Instant;

import lab.bookstore.catalogservice.domain.Book;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookJsonTests {

    @Autowired
    private JacksonTester<Book> jsonTester;

    @Test
    void test_serialize_from_book_to_json() throws Exception {
        var now = Instant.now();
        var book = new Book(99L, "1234567890", "JavaBook", "Jim", 888.8, "Gotop", now, now, "Jim2", "Jim3", 9);
        var jsonContent = jsonTester.write(book);
        assertThat(jsonContent).extractingJsonPathNumberValue("@.id")
                .isEqualTo(book.id().intValue());
        assertThat(jsonContent).extractingJsonPathStringValue("@.isbn")
                .isEqualTo(book.isbn());
        assertThat(jsonContent).extractingJsonPathStringValue("@.name")
                .isEqualTo(book.name());
        assertThat(jsonContent).extractingJsonPathStringValue("@.author")
                .isEqualTo(book.author());
        assertThat(jsonContent).extractingJsonPathNumberValue("@.price")
                .isEqualTo(book.price());
        assertThat(jsonContent).extractingJsonPathStringValue("@.publisher")
                .isEqualTo(book.publisher());
        assertThat(jsonContent).extractingJsonPathStringValue("@.createdDate")
                .isEqualTo(book.createdDate().toString());
        assertThat(jsonContent).extractingJsonPathStringValue("@.lastModifiedDate")
                .isEqualTo(book.lastModifiedDate().toString());
        assertThat(jsonContent).extractingJsonPathStringValue("@.createdBy")
        		.isEqualTo(book.createdBy());
        assertThat(jsonContent).extractingJsonPathStringValue("@.lastModifiedBy")
        		.isEqualTo(book.lastModifiedBy());        
        assertThat(jsonContent).extractingJsonPathNumberValue("@.version")
                .isEqualTo(book.version());
    }

    @Test
    void test_deserialize_from_json_to_book() throws Exception {
        var instant = Instant.parse("2024-07-01T21:50:44.145339Z");
        var content = """
                {
                    "id": 99,
                    "isbn": "1234567890",
                    "name": "JavaBook",
                    "author": "Jim",
                    "price": 888.8,
                    "publisher": "Gotop",
                    "createdDate": "2024-07-01T21:50:44.145339Z",
                    "lastModifiedDate": "2024-07-01T21:50:44.145339Z",
                    "createdBy": "Jim2",
                    "lastModifiedBy": "Jim3",                    
                    "version": 9
                }
                """;
        assertThat(jsonTester.parse(content))
                .usingRecursiveComparison()
                .isEqualTo(new Book(99L, "1234567890", "JavaBook", "Jim", 888.8, "Gotop", instant, instant, "Jim2", "Jim3", 9));
    }

}

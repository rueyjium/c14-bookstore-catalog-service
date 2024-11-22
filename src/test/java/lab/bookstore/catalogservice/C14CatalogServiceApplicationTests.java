package lab.bookstore.catalogservice;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import lab.bookstore.catalogservice.domain.Book;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@Testcontainers
public class C14CatalogServiceApplicationTests {

    // Customer
    private static KeycloakToken bjornTokens;
    // Customer and employee
    private static KeycloakToken isabelleTokens;
	
    @Autowired
    private WebTestClient webTestClient;

    @Container
    private static final KeycloakContainer keycloakContainer = new KeycloakContainer("quay.io/keycloak/keycloak:23.0")
			.withRealmImportFile("test-realm-config.json");

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> keycloakContainer.getAuthServerUrl() + "/realms/LineBookstore");
    }

    @BeforeAll
    static void generateAccessTokens() {
        WebClient webClient = WebClient.builder()
                .baseUrl(keycloakContainer.getAuthServerUrl() + "/realms/LineBookstore/protocol/openid-connect/token")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();

        isabelleTokens = authenticateWith("isabelle", "password", webClient);
        bjornTokens = authenticateWith("bjorn", "password", webClient);
    }    
    
    @Test
    void should_get_book_when_query_with_isbn() {
        // given
        var isbn = "1234567890";
        var bookToCreate = Book.of(isbn, "java", "jim", 888.8, "Gotop");
        Book expectedBook = webTestClient
                .post()
                .uri("/books")
                .headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken()))
                .bodyValue(bookToCreate)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Book.class).value(book -> assertThat(book).isNotNull())
                .returnResult().getResponseBody();
        // when, then
        webTestClient
                .get()
                .uri("/books/" + isbn)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Book.class).value(book -> {
                    assertThat(book).isNotNull();
                    assertThat(book.isbn()).isEqualTo(expectedBook.isbn());
                });
    }

    @Test
    void should_create_book_when_post_request() {
    	// given
        var expectedBook = Book.of("1234567891", "java", "jim", 888.8, "Gotop");
        // when, then
        webTestClient
                .post()
                .uri("/books")
                .headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken()))
                .bodyValue(expectedBook)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Book.class).value(returnedBook -> {
                    assertThat(returnedBook).isNotNull();
                    assertThat(returnedBook.isbn()).isEqualTo(expectedBook.isbn());
                });
    }
    
    @Test
    void whenPostRequestUnauthenticatedThen401() {
        var expectedBook = Book.of("1231231231", "Title", "Author", 9.90, "GoTop");

        webTestClient
                .post()
                .uri("/books")
                .bodyValue(expectedBook)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void whenPostRequestUnauthorizedThen403() {
        var expectedBook = Book.of("1231231231", "Title", "Author", 9.90, "GoTop");

        webTestClient
                .post()
                .uri("/books")
                .headers(headers -> headers.setBearerAuth(bjornTokens.accessToken()))
                .bodyValue(expectedBook)
                .exchange()
                .expectStatus().isForbidden();
    }



    @Test
    void should_update_book_when_put_request() {
    	// given
        var isbn = "1234567892";
        var bookToCreate = Book.of(isbn, "java", "jim", 888.8, "Gotop");
        Book createdBook = webTestClient
                .post()
                .uri("/books")
                .headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken()))
                .bodyValue(bookToCreate)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Book.class).value(book -> assertThat(book).isNotNull())
                .returnResult().getResponseBody();
        var bookToUpdate = new Book(createdBook.id(), createdBook.isbn(), createdBook.name(), createdBook.author(), 1000.0,
                createdBook.publisher(), createdBook.createdDate(), createdBook.lastModifiedDate(), createdBook.createdBy(), createdBook.lastModifiedBy(), createdBook.version());
        // when, then
        webTestClient
                .put()
                .uri("/books/" + isbn)
                .headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken()))
                .bodyValue(bookToUpdate)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Book.class).value(returnedBook -> {
                    assertThat(returnedBook).isNotNull();
                    assertThat(returnedBook.price()).isEqualTo(bookToUpdate.price());
                });
    }

    @Test
    void should_delete_book_when_delete_request() {
    	// given
        var isbn = "1234567893";
        var bookToCreate = Book.of(isbn, "java", "jim", 888.8, "Gotop");
        webTestClient
                .post()
                .uri("/books")
                .headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken()))
                .bodyValue(bookToCreate)
                .exchange()
                .expectStatus().isCreated();
        // when, then
        webTestClient
                .delete()
                .uri("/books/" + isbn)
                .headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken()))
                .exchange()
                .expectStatus().isNoContent();
        webTestClient
                .get()
                .uri("/books/" + isbn)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class).value(errorMessage ->
                        assertThat(errorMessage).isEqualTo("The book with ISBN " + isbn + " was not found.")
                );
    }
    
    private static KeycloakToken authenticateWith(String username, String password, WebClient webClient) {
        return webClient
                .post()
                .body(BodyInserters.fromFormData("grant_type", "password")
                        .with("client_id", "line-test")
                        .with("username", username)
                        .with("password", password)
                )
                .retrieve()
                .bodyToMono(KeycloakToken.class)
                .block();
    }

    private record KeycloakToken(String accessToken) {

        @JsonCreator
        private KeycloakToken(@JsonProperty("access_token") final String accessToken) {
            this.accessToken = accessToken;
        }

    }


}

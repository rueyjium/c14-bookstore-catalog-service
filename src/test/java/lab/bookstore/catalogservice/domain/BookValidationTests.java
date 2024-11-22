package lab.bookstore.catalogservice.domain;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookValidationTests {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

	@Test
	void should_pass_all_validations_when_all_fields_correct() {
		// given
		var book = Book.of("1234567890", "java", "jim", 888.8, "Gotop");
		// when
		Set<ConstraintViolation<Book>> violations = validator.validate(book);
		// then
		assertThat(violations).isEmpty();
	}

	@Test
	void should_fail_validation_when_isbn_empty() {
		// given
		var book = Book.of("", "java", "jim", 888.8, "Gotop");
		// when
		Set<ConstraintViolation<Book>> violations = validator.validate(book);
		List<String> constraintViolationMessages = violations.stream()
				.map(ConstraintViolation::getMessage)
				.collect(Collectors.toList());
		// then
		assertThat(violations).hasSize(2);
		assertThat(constraintViolationMessages)
				.contains("The book ISBN must be defined.")
				.contains("The ISBN format must be valid.");
	}

	@Test
	void should_fail_validation_when_isbn_incorrect() {
		// given
		var book = Book.of("a234567890", "java", "jim", 888.8, "Gotop");
		// when
		Set<ConstraintViolation<Book>> violations = validator.validate(book);
		// then
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage())
				.isEqualTo("The ISBN format must be valid.");
	}

	@Test
	void should_fail_validation_when_name_empty() {
		// given
		var book = Book.of("1234567890", "", "jim", 888.8, "Gotop");
		// when
		Set<ConstraintViolation<Book>> violations = validator.validate(book);
		// then
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage())
				.isEqualTo("The book name must be defined.");
	}

	@Test
	void should_fail_validation_when_author_empty() {
		// given
		var book = Book.of("1234567890", "java", "", 888.8, "Gotop");
		// when
		Set<ConstraintViolation<Book>> violations = validator.validate(book);
		// then
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage())
				.isEqualTo("The book author must be defined.");
	}

	@Test
	void should_fail_validation_when_price_empty() {
		// given
		var book = Book.of("1234567890", "java", "jim", null, "Gotop");
		// when
		Set<ConstraintViolation<Book>> violations = validator.validate(book);
		// then
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage())
				.isEqualTo("The book price must be defined.");
	}

	@Test
	void should_fail_validation_when_price_zero() {
		// given
		var book = Book.of("1234567890", "java", "jim", 0.0, "Gotop");
		// when
		Set<ConstraintViolation<Book>> violations = validator.validate(book);
		// then
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage())
				.isEqualTo("The book price must be greater than zero.");
	}

	@Test
	void should_fail_validation_when_price_negative() {
		// given
		var book = Book.of("1234567890", "java", "jim", -888.8, "Gotop");
		// when
		Set<ConstraintViolation<Book>> violations = validator.validate(book);
		// then
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage())
				.isEqualTo("The book price must be greater than zero.");
	}

	@Test
	void should_pass_validation_when_publisher_empty() {
		// given
		Book book = Book.of("1234567890", "java", "jim", 888.8, null);
		// when
		Set<ConstraintViolation<Book>> violations = validator.validate(book);
		// then
		assertThat(violations).isEmpty();
	}

}

package lab.bookstore.catalogservice.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import lab.bookstore.catalogservice.config.LineProperties;
import lab.bookstore.catalogservice.config.SecurityConfig;

@WebMvcTest({ HomeController.class, LineProperties.class })
@Import(SecurityConfig.class)
class HomeControllerMvcTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void should_get_200_when_call_homepage() throws Exception {
		// when, then
		mockMvc
			.perform(get("/").with(jwt()))
			.andExpect(status().isOk())
			.andExpect(content().string("Welcome to the local book catalog!"));
	}

	@Test
	void should_get_port_when_call_port1() throws Exception {
		// when, then
		mockMvc
			.perform(get("/port1").with(jwt()))
			.andExpect(status().isOk())
			.andExpect(content().string("9001"));
	}

	@Test
	void should_get_port_when_call_port2() throws Exception {
		// when, then
		mockMvc
			.perform(get("/port2").with(jwt()))
			.andExpect(status().isOk())
			.andExpect(content().string("9001"));
	}

}

package hu.flowacademy.qasitespring;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @Slf4j provides us a log variable, so we can do real life logging
 * instead of using System.out.println(...)
 */
@Slf4j
/**
 * JUnit 5 is extendable with custom test supporter stuff
 * For example: SpringExtension will run a Spring Application Context
 * so we can use @Autowired (Dependency Injection) after extending junit with this
 */
@ExtendWith(SpringExtension.class)
/**
 * @SpringBootTest provides us many options, to configure our test
 * For example: start the test app on random port with
 * webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
 */
@SpringBootTest(
// FIXME: commented out, we don't need this right now
//		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
/**
 * @AutoConfigureMockMvc, will provide an instance of MockMvc
 * This will help us to do HTTP requests, it's an HTTP client for testing
 */
// FIXME: use it when it needed
// @AutoConfigureMockMvc
/**
 * Sets the active maven profile to test
 * So we want to use the test environment's config from application.yml
 * It's only needed for integration testing
 */
@ActiveProfiles("test")
class QasiteSpringApplicationTests {

	@Autowired
	// EntityManager must have, if we have a proper database setup
	// The JPA using this interface under the hood
	private EntityManager entityManager;

	@Test
	void entityManagerSet() {
		assertNotNull(entityManager);
	}

}

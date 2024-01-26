package de.nebulit.eventsourcing.testsupport

import com.ninjasquad.springmockk.MockkBean
import de.nebulit.eventsourcing.challenge.slices.agenda.openai.OpenAiConnector
import org.awaitility.Awaitility
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.TestPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.time.Duration

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestPropertySource(properties = [
    "open-ai.token=token"
])
@DirtiesContext
abstract class BaseIntegrationTest {


    @MockkBean(relaxed = true)
    private lateinit var openAiConnector: OpenAiConnector

    companion object {
        @org.testcontainers.junit.jupiter.Container
        private val postgres = PostgreSQLContainer(DockerImageName.parse("postgres")).withReuse(true)
        //@org.testcontainers.junit.jupiter.Container
        //	    private val kafka: KafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"))

//		@org.testcontainers.junit.jupiter.Container
//	    private val kafka: KafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"))

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { postgres.jdbcUrl }
            registry.add("spring.flyway.url") { postgres.jdbcUrl }
            registry.add("spring.datasource.username") { "test" }
            registry.add("spring.datasource.password") { "test" }
            registry.add("spring.flyway.user") { "test" }
            registry.add("spring.flyway.password") { "test" }
        }
    }

}

fun waitForAssertion(block: () -> Unit, duration: Duration = Duration.ofSeconds(15)) {
    Awaitility.await().atLeast(duration).untilAsserted(block)
}

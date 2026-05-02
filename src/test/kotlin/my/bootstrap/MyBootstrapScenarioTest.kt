package my.bootstrap

import io.kotest.matchers.shouldBe
import io.qalipsis.runtime.test.QalipsisTestRunner
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.time.Duration

/**
 * End-to-end test for the bootstrap scenario.
 *
 * A disposable httpbin container is started by Testcontainers, and its URL is injected into
 * the scenario via the `http.server.url` configuration property (see the `@Property` parameter
 * on [MyBootstrapScenario.myBootstrapScenario]). This means the test does not depend on
 * https://httpbin.org being reachable.
 *
 * Requires a working Docker daemon. CI installs one by default.
 */
@Testcontainers
class MyBootstrapScenarioTest {

    @Test
    fun `should execute the scenario against a containerized httpbin`() {
        val baseUrl = "http://${httpbin.host}:${httpbin.firstMappedPort}"

        val exitCode = QalipsisTestRunner
            .withConfiguration("http.server.url=$baseUrl")
            .execute()

        exitCode shouldBe 0
    }

    companion object {

        @Container
        @JvmStatic
        val httpbin: GenericContainer<*> =
            GenericContainer(DockerImageName.parse("kennethreitz/httpbin:latest"))
                .withExposedPorts(80)
                // Block until httpbin actually answers HTTP — protects against the scenario
                // firing requests before the server inside the container is ready.
                .waitingFor(
                    Wait.forHttp("/status/200")
                        .forPort(80)
                        .forStatusCode(200)
                        .withStartupTimeout(Duration.ofSeconds(60))
                )
    }
}

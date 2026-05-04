package my.bootstrap

import io.qalipsis.api.annotations.Property
import io.qalipsis.api.annotations.Scenario
import io.qalipsis.api.executionprofile.stages
import io.qalipsis.api.scenario.scenario
import io.qalipsis.api.steps.returns
import io.qalipsis.api.steps.verify
import io.qalipsis.plugins.http.ConnectionStrategyType
import io.qalipsis.plugins.http.HttpResult
import io.qalipsis.plugins.http.configuration.defaults
import io.qalipsis.plugins.http.http
import io.qalipsis.plugins.http.httpApache
import io.qalipsis.plugins.http.request.HttpMethod
import org.apache.hc.core5.http.ContentType

/**
 * Example scenario that runs out-of-the-box against the public httpbin.org service.
 *
 * Pipeline:
 *   1. Spawn a small population of virtual users ("minions") in two staged ramps.
 *   2. Each minion produces a payload and POSTs it to https://httpbin.org/post.
 *   3. Each minion verifies the HTTP response status (200) so failures show up in the report.
 *
 * Replace the URL, payload and assertion with your own once you have run it once.
 * Browse https://github.com/qalipsis/qalipsis-examples for richer scenarios
 * (Kafka, JDBC, Cassandra, Mongo, gRPC, …) and https://qalipsis.io/docs for the full DSL.
 */
class MyBootstrapScenario {

    @Scenario(name = "my-new-scenario", description = "POSTs to httpbin.org and verifies the response", version = "0.1")
    fun myBootstrapScenario(
        @Property("http.server.url", "https://httpbin.org") httpServerUrl: String,
    ) {
        scenario {
            // Total virtual users that will execute the scenario.
            // Keep small for the first run, then crank it up.
            minionsCount = 10

            // Execution profile = how the load ramps up over time.
            // Two stages: 40 % of the minions over 5 s, then the remaining 60 % over 5 s.
            // The third argument is the resolution (period of each scheduling tick).
            profile {
                stages {
                    stage(40.0, 5_000, 10_000)
                    stage(60.0, 5_000, 10_000)
                }
            }

            // Defaults shared by every HTTP step in this scenario.
            // Override per-step inside `http { connect { url(...) } }` if needed.
            httpApache().defaults {
                connect {
                    connectionStrategy {
                        shared = true
                        strategyType = ConnectionStrategyType.WARMUP
                    }
                    url(url = httpServerUrl)
                }
            }
        }
            // The pipeline starts here. Each step receives the previous step's output.
            .start()
            // Produce one payload per minion. `it` is the StepContext.
            .returns { "Hello from QALIPSIS minion ${it.minionId}" }
            // POST the payload to httpbin's /post echo endpoint.
            .httpApache()
            .http {
                name = "echo-post"
                report { reportErrors = true }
                request { _, body ->
                    simple(HttpMethod.POST, "/post")
                        .body(body, ContentType.TEXT_PLAIN)
                }
            }
            // Verify the HTTP response — throw to mark the iteration as failed.
            // The http step emits HttpResult<INPUT, BODY>; here input is String and body is String.
            // The explicit lambda type helps Kotlin resolve the verify generic.
            .verify { result: HttpResult<String, String> ->
                val code = result.response?.code
                check(code == 200) { "expected HTTP 200, got $code" }
            }.configure {
                name = "verify-http-200"
            }
    }
}

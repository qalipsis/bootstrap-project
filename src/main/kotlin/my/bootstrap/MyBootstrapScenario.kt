package my.bootstrap

import io.qalipsis.api.annotations.Scenario
import io.qalipsis.api.scenario.scenario
import io.qalipsis.api.steps.returns

/**
 * A skeleton of scenario.
 */
class MyBootstrapScenario {

    @Scenario(name = "my-new-scenario", description = "It does something extraordinary", version = "0.1")
    fun myBootstrapScenario() {
        scenario {
            minionsCount = 100
            profile {

            }
        }
            .start()
        // Develop your scenario here.
            .returns(1)
    }
}



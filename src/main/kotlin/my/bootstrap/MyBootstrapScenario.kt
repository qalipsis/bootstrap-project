package my.bootstrap

import io.qalipsis.api.annotations.Scenario
import io.qalipsis.api.executionprofile.stages
import io.qalipsis.api.scenario.scenario

/**
 * A skeleton of scenario.
 */
class MyBootstrapScenario {

    @Scenario("my-scenario")
    fun myBootstrapScenario() {
        scenario {
            minionsCount = 1000
            profile {
                stages {
                    stage(minionsCount = 200, rampUpDurationMs = 4000, totalDurationMs = 10000, resolutionMs = 500)
                    stage(minionsCount = 300, rampUpDurationMs = 6000, totalDurationMs = 20000, resolutionMs = 500)
                    stage(minionsCount = 500, rampUpDurationMs = 10000, totalDurationMs = 30000, resolutionMs = 500)
                }
            }
        }
            .start()
        // Develop your scenario here.
    }
}



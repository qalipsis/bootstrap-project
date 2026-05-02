import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.bmuschko.gradle.docker.tasks.image.DockerPushImage
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import io.qalipsis.gradle.bootstrap.tasks.RunQalipsis

plugins {
    alias(libs.plugins.qalipsis.bootstrap)
    // Uncomment to publish your scenarios to QALIPSIS Cloud (see docs/cloud.md).
    // alias(libs.plugins.qalipsis.cloud)

    alias(libs.plugins.bmuschko.docker)
    alias(libs.plugins.shadow)
}

description = "QALIPSIS - My bootstrap"

// `./scripts/init.sh` will rewrite these for your project.
group = "org.example"
version = "1.0-SNAPSHOT"

qalipsis {
    // You can override the version of QALIPSIS to use by the Gradle plugin.
    //version("1.0.0")

    plugins {
        // Configure here the plugins you want to use.
        // for example: apacheCassandra()
        http()
    }
}

dependencies {
    // Add your own runtime dependencies here.

    testImplementation(testFixtures("io.qalipsis:qalipsis-runtime"))
    // Test stack: JUnit 5 (engine for plain @Test classes) + kotest (assertions + spec engine).
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.assertions.json)
    testImplementation(libs.kotest.runner.junit5)
    // Testcontainers — spin up the mock backend (httpbin) for the integration test.
    testImplementation(libs.testcontainers)
    testImplementation(libs.testcontainers.junit.jupiter)
    // Required at runtime since JUnit Platform 1.10 / Gradle 8.5+.
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks {
    named<ShadowJar>("shadowJar") {
        mergeServiceFiles()
        archiveClassifier.set("qalipsis")
        // QALIPSIS pulls in enough transitive deps that the fat-JAR exceeds the standard
        // 65 535-entry ZIP limit. Zip64 lifts that cap.
        isZip64 = true
    }

    build {
        dependsOn(shadowJar)
    }

    named<RunQalipsis>("qalipsisRunAllScenarios") {
        // Configures the default task to execute all the scenarios.
        //    configuration(
        //        "report.export.junit.enabled" to "true",
        //        "report.export.junit.folder" to project.layout.buildDirectory.dir("test-results/my-new-scenario")
        //            .get().asFile.path
        //    )
    }

    //create("executeMyNewScenario", RunQalipsis::class.java) {
    //    scenarios("my-new-scenario")
    //    configuration(
    //        "report.export.junit.enabled" to "true",
    //        "report.export.junit.folder" to project.layout.buildDirectory.dir("test-results/my-new-scenario")
    //            .get().asFile.path
    //    )
    //}

}

// Docker image — driven by the bmuschko docker-remote-api plugin.
//
// `./scripts/init.sh` will rewrite the image name for your project.
val dockerImageName = "my-company/qalipsis-bootstrap"
val dockerStagingDir = layout.buildDirectory.dir("docker")

val dockerPrepare = tasks.register<Sync>("dockerPrepare") {
    group = "docker"
    description = "Stages the Dockerfile and the installed distribution under build/docker."
    dependsOn("installDist")
    from(layout.buildDirectory.dir("install/${project.name}"))
    from("src/main/docker/Dockerfile")
    into(dockerStagingDir)
}

val dockerBuild = tasks.register<DockerBuildImage>("dockerBuild") {
    group = "docker"
    description = "Builds the Docker image containing the QALIPSIS scenarios."
    dependsOn(dockerPrepare)
    inputDir.set(dockerStagingDir)
    images.add("$dockerImageName:${project.version.toString().lowercase()}")
    images.add("$dockerImageName:latest")
    noCache.set(true)
}

tasks.register<DockerPushImage>("dockerPush") {
    group = "docker"
    description = "Pushes the Docker image to the configured registry."
    dependsOn(dockerBuild)
    images.set(dockerBuild.flatMap { it.images })
}
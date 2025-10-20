import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import io.qalipsis.gradle.bootstrap.tasks.RunQalipsis

plugins {
    id("io.qalipsis.bootstrap") version "0.1.4"

    id("com.palantir.docker") version "0.35.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

description = "QALIPSIS - My bootstrap"

group = "org.example"
version = "1.0-SNAPSHOT"

qalipsis {
    // You can override the version of QALIPSIS to use by the Gradle plugin.
    //version("1.0.0")

    plugins {
        // Configure here the plugins you want to use.
        // for example: apacheCassandra()
    }
}

dependencies {
    // Add your own dependencies.
    //implementation("com.willowtreeapps.assertk:assertk:0.+")
}

tasks {
    named<ShadowJar>("shadowJar") {
        mergeServiceFiles()
        archiveClassifier.set("qalipsis")
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

val shadowJarName = "${project.name}-${project.version}-qalipsis.jar"

docker {
    name = "my-company/qalipsis-bootstrap"
    setDockerfile(project.file("src/main/docker/Dockerfile"))
    noCache(true)
    files("build/libs/$shadowJarName", "src/main/docker/entrypoint.sh")
    buildArgs(mapOf("JAR_NAME" to shadowJarName))
}
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    idea
    java
    application
    kotlin("jvm") version "1.8.21"
    kotlin("kapt") version "1.8.21"

    id("com.palantir.docker") version "0.28.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

description = "QALIPSIS My bootstrap"

group = "org.example"
version = "1.0-SNAPSHOT"

val target = JavaVersion.VERSION_11

java {
    sourceCompatibility = target
    targetCompatibility = target
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        name = "maven-central-snapshots"
        setUrl("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

kapt {
    includeCompileClasspath = true
}

val platformVersion = "0.7.a-SNAPSHOT"
dependencies {
    implementation(platform("io.qalipsis:qalipsis-platform:${platformVersion}"))
    implementation("io.qalipsis:qalipsis-api-processors")
    kapt(platform("io.qalipsis:qalipsis-platform:${platformVersion}"))
    kapt("io.qalipsis:qalipsis-api-processors")

    implementation("io.qalipsis:qalipsis-head")
    implementation("io.qalipsis:qalipsis-factory")
    implementation("io.qalipsis:qalipsis-runtime")

    // Uncomment required plugins
//    implementation("io.qalipsis.plugin:qalipsis-plugin-cassandra")
//    implementation("io.qalipsis.plugin:qalipsis-plugin-elasticsearch")
//    implementation("io.qalipsis.plugin:qalipsis-plugin-graphite")
//    implementation("io.qalipsis.plugin:qalipsis-plugin-influxdb")
//    implementation("io.qalipsis.plugin:qalipsis-plugin-jackson")
//    implementation("io.qalipsis.plugin:qalipsis-plugin-jakarta-ee-messaging")
//    implementation("io.qalipsis.plugin:qalipsis-plugin-jms")
//    implementation("io.qalipsis.plugin:qalipsis-plugin-kafka")
//    implementation("io.qalipsis.plugin:qalipsis-plugin-mail")
//    implementation("io.qalipsis.plugin:qalipsis-plugin-mongodb")
//    implementation("io.qalipsis.plugin:qalipsis-plugin-netty")
//    implementation("io.qalipsis.plugin:qalipsis-plugin-r2dbc-jasync")
//    implementation("io.qalipsis.plugin:qalipsis-plugin-rabbitmq")
//    implementation("io.qalipsis.plugin:qalipsis-plugin-redis-lettuce")
//    implementation("io.qalipsis.plugin:qalipsis-plugin-slack")
//    implementation("io.qalipsis.plugin:qalipsis-plugin-timescaledb")
}

application {
    mainClass.set("io.qalipsis.runtime.Qalipsis")
}

tasks {
    withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_11.majorVersion
            javaParameters = true
        }
    }
    
    named<ShadowJar>("shadowJar") {
        mergeServiceFiles()
        archiveClassifier.set("qalipsis")
    }

    build {
        dependsOn(shadowJar)
    }
}

val shadowJarName = "${project.name}-${project.version}-qalipsis.jar"

docker {
    name = "my-company/qalipsis-bootstrap"
    setDockerfile(project.file("src/main/docker/Dockerfile"))
    noCache(true)
    files("build/libs/$shadowJarName", "src/main/docker/entrypoint.sh")
    buildArgs(mapOf("JAR_NAME" to shadowJarName))
}

task<JavaExec>("runScenarios") {
    group = "application"
    description = "Start a campaign with all the scenarios"
    mainClass.set("io.qalipsis.runtime.Qalipsis")
    maxHeapSize = "256m"
    args("--autostart", "-c", "report.export.console.enabled=true")
    workingDir = projectDir
    classpath = sourceSets["main"].runtimeClasspath
}

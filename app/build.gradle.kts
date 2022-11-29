val platformName: String by project
val platformHost: String by project
val containerBaseName: String by project

plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Use JUnit Jupiter for testing.
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")

    // Use jade from .jar file
    implementation(fileTree("libs").also { it.include("**/*.jar") })
    // Use 2p-kt dependency
    implementation("it.unibo.tuprolog:solve-jvm:0.30.4")
    implementation("it.unibo.tuprolog:solve-classic-jvm:0.30.4")
    implementation("it.unibo.tuprolog:parser-theory-jvm:0.30.4")
    // Use clpfd module
    implementation(project(":clpfd"))
}

java {
    targetCompatibility = JavaVersion.VERSION_17
    sourceCompatibility = JavaVersion.VERSION_17
}


tasks.create<JavaExec>("startPlatform") {
    main = "jade.Boot"
    group = "run"
    sourceSets.main {
        classpath = runtimeClasspath
    }
    args("-gui", "-name", platformName, "-container-name", containerBaseName + "main", "-local-host", platformHost)
}

tasks.create<JavaExec>("startContainer") {
    main = "jade.Boot"
    group = "run"
    sourceSets.main {
        classpath = runtimeClasspath
    }
    args("-container", "-container-name", containerBaseName + System.currentTimeMillis(), "-host", platformHost)
    if (project.hasProperty("agents")) {
        val agents = project.property("agents").toString()
        if (agents.isNotBlank()) {
            args("-agents", agents)
        }
    }
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
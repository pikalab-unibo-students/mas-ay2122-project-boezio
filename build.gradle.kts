plugins {
    java
}

allprojects {

    repositories {
        mavenCentral()
    }

    apply(plugin = "java")

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.20")
        implementation("org.choco-solver:choco-solver:4.10.8")
        testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
        implementation("com.google.guava:guava:30.1.1-jre")
    }
}
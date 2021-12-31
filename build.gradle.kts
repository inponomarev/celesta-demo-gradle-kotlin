plugins {
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.spring") version "1.6.0"
    id("org.springframework.boot") version "2.6.2"
    id("com.wiredforcode.spawn") version "0.8.2"
}

repositories {
    mavenLocal()
    mavenCentral()
    // Use the plugin portal to apply community plugins in convention plugins.
    gradlePluginPortal()
}

dependencies {
    implementation("org.json:json:20211205")
    implementation("ru.curs:celesta-maven-plugin:7.4.11")
    implementation("org.springframework.boot:spring-boot-starter-web:2.6.1")
    implementation("ru.curs:spring-boot-starter-celesta:2.1.53")
    implementation("ru.curs:celesta-system-services:7.4.11")
    implementation("org.projectlombok:lombok:1.18.22")
    implementation("com.h2database:h2:1.4.200")
    implementation("org.postgresql:postgresql:42.3.1")
    implementation("org.springframework.boot:spring-boot-devtools:2.6.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.0")
    implementation("org.springdoc:springdoc-openapi-ui:1.5.13")
    implementation("org.awaitility:awaitility:4.1.1")
    implementation("biz.paluch.logging:logstash-gelf:1.14.1")
    implementation("org.codehaus.janino:janino:3.1.6")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-jackson:2.9.0")
    implementation("org.apache.httpcomponents:httpcore:4.4.14")
    implementation("org.mockito:mockito-core:4.0.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.6.1")
    testImplementation("org.assertj:assertj-core:3.21.0")
    testImplementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    testImplementation("ru.curs:celesta-unit:7.4.11")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("com.github.tomakehurst:wiremock-jre8:2.32.0")
    testImplementation("com.approvaltests:approvaltests:12.3.2")
    testImplementation("com.google.code.gson:gson:2.8.9")
}

val genSources = task("gen-sources") {
    doLast {
        CodeGenerator(project).execute()
        ResourcesGenerator(project).execute()
    }
}

tasks.named("compileKotlin").configure {
    dependsOn(genSources)
}

tasks.withType<Test> {
    useJUnitPlatform {
        excludeTags("integration")
    }
}

val integrationTest = tasks.register<Test>("integrationTest") {
    /*lateinit var process: Process
    doFirst {
        process = ServerRunner(project).run()
        println("Application started, pid=${process.pid()}")
    }*/
    useJUnitPlatform {
        includeTags("integration")
    }
    //  ignoreFailures = true
    /*doLast {
        process.destroy()
        println("Application stopped")
    }*/
}.get()

val runIntegrationTest = task("runIntegrationTest") {
    doLast {
        lateinit var process: Process
        try {
            process = ServerRunner(project).run()
            println("Application started, pid=${process.pid()}")
            integrationTest.executeTests()
        } finally {
            process.destroy()
            println("Application stopped")
        }
    }
}

sourceSets {
    main {
        java {
            srcDir("build/generated-sources")
        }
        resources {
            srcDir("build/generated-resources")
        }
    }
}

tasks.named("build").configure {
    finalizedBy(runIntegrationTest)
}

description = "Celesta Demo App"

package ru.inponomarev.celestademo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(AppSettings::class)
open class App

fun main(args: Array<String>) {
    //NB: System restart does not clean up H2 database, thus
    //leading to wrong integration test results
    System.setProperty("spring.devtools.restart.enabled", "false")
    runApplication<App>(*args)
}
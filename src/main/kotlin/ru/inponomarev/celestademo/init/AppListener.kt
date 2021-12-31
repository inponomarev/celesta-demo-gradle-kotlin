package ru.inponomarev.celestademo.init

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import ru.curs.celesta.SystemCallContext
import ru.inponomarev.celestademo.AppSettings

@Component
open class AppListener(
    private val appSettings: AppSettings,
    private val demoDataInitializer: DemoDataInitializer
) : ApplicationListener<ApplicationReadyEvent> {
    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        if (appSettings.initdemodata == true) {
            demoDataInitializer.initData(SystemCallContext())
        }
        println("Application is ready.")
    }
}

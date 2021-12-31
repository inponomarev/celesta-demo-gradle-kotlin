package ru.inponomarev.celestademo

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "celestademo")
class AppSettings {
    var initdemodata: Boolean? = null
}

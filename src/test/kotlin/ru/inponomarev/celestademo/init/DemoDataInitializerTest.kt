package ru.inponomarev.celestademo.init

import org.junit.jupiter.api.Test
import ru.curs.celesta.CallContext
import ru.curs.celestaunit.CelestaTest

@CelestaTest
class DemoDataInitializerTest {
    private val initializer = DemoDataInitializer()
    @Test
    fun init(ctx: CallContext) {
        //Test initialization over non-empty database
        initializer.initData(ctx)
        ctx.commit()
        initializer.initData(ctx)
    }
}
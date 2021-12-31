package ru.inponomarev.celestademo.init

import org.springframework.stereotype.Component
import ru.curs.celesta.CallContext
import ru.curs.celesta.transaction.CelestaTransaction
import ru.inponomarev.celestademo.CustomerCursor
import ru.inponomarev.celestademo.ItemCursor
import ru.inponomarev.celestademo.ItemOrdersCursor
import ru.inponomarev.celestademo.OrderCursor

@Component
open class DemoDataInitializer {
    @CelestaTransaction
    open fun initData(ctx: CallContext?) {
        println("Initializing demo data...")
        val itemCursor = ItemCursor(ctx).apply {
            id = "123"
            name = "cheese"
            defaultPrice = 3
            tryInsert()
        }
        val customerCursor = CustomerCursor(ctx).apply {
            name = "John Doe"
            tryInsert()
        }

        OrderCursor(ctx).apply {
            customerId = customerCursor.id
            itemId = itemCursor.id
            quantity = 10
            tryInsert()
        }
        println("Demo data initialization complete.")

        ItemOrdersCursor(ctx).apply {
            tryFirst()
            println("----")
            println(orderedQuantity)
            println("----")
        }
    }
}
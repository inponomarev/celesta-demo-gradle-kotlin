package ru.inponomarev.celestademo.dao

import ru.curs.celestaunit.CelestaTest
import ru.inponomarev.celestademo.CustomerCursor
import ru.inponomarev.celestademo.ItemCursor
import org.junit.jupiter.api.BeforeEach
import ru.curs.celesta.CallContext
import ru.inponomarev.celestademo.dto.OrderDto
import ru.inponomarev.celestademo.OrderCursor
import org.approvaltests.Approvals
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

@CelestaTest
class OrderDaoTest {
    private val orderDao = OrderDao()
    private lateinit var customer: CustomerCursor
    private lateinit var item: ItemCursor

    @BeforeEach
    fun setUp(ctx: CallContext?) {
        customer = CustomerCursor(ctx)
        customer.setName("John Doe")
            .setEmail("john@example.com")
            .insert()
        item = ItemCursor(ctx)
        item.setId("12345")
            .setName("cheese")
            .setDefaultPrice(42)
            .insert()
    }

    @Test
    fun orderIsPostedWithDefaultPrice(ctx: CallContext?) {
        //ARRANGE
        val dto = OrderDto(
            customerId = customer.id,
            itemId = item.id,
            quantity = 100
        )

        //ACT
        val result = orderDao.postOrder(ctx, dto)

        //ASSERT
        val orderCursor = OrderCursor(ctx)
        Assertions.assertThat(orderCursor.count()).isEqualTo(1)
        orderCursor.first()
        Assertions.assertThat(orderCursor.price).isEqualTo(item.defaultPrice)
        Assertions.assertThat(orderCursor.quantity).isEqualTo(100)
        Assertions.assertThat(orderCursor.amount).isEqualTo(item.defaultPrice * orderCursor.quantity)
        Approvals.verifyJson(ObjectMapper().writer().writeValueAsString(result))
    }

    @Test
    fun orderedItemsMethodReturnsAggregatedValues(ctx: CallContext?) {
        //ARRANGE
        val item2 = ItemCursor(ctx)
        item2.setId("2").setName("item 2").insert()
        val orderCursor = OrderCursor(ctx)
        orderCursor.setId(null).setItemId(item.id).setCustomerId(customer.id).setQuantity(1).insert()
        orderCursor.setId(null).setItemId(item.id).setCustomerId(customer.id).setQuantity(3).insert()
        orderCursor.setId(null).setItemId(item2.id).setCustomerId(customer.id).setQuantity(5).insert()
        //ACT
        val result = orderDao.getItems(ctx)

        //ASSERT
        Approvals.verifyJson(ObjectMapper().writer().writeValueAsString(result))
    }
}
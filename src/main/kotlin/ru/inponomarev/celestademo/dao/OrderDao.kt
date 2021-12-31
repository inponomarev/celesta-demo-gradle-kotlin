package ru.inponomarev.celestademo.dao

import org.springframework.stereotype.Repository
import ru.curs.celesta.CallContext
import ru.curs.celesta.transaction.CelestaTransaction
import ru.inponomarev.celestademo.ItemCursor
import ru.inponomarev.celestademo.ItemOrdersCursor
import ru.inponomarev.celestademo.ItemViewCursor
import ru.inponomarev.celestademo.OrderCursor
import ru.inponomarev.celestademo.dto.ItemDto
import ru.inponomarev.celestademo.dto.OrderDto

@Repository
open class OrderDao {
    @CelestaTransaction
    open fun postOrder(ctx: CallContext?, orderDTO: OrderDto): OrderDto {
        val orderCursor = OrderCursor(ctx)
        map(orderDTO, orderCursor)
        val item = ItemCursor(ctx)
        item.get(orderDTO.itemId)
        if (orderCursor.price == null) {
            orderCursor.price = item.defaultPrice
        }
        orderCursor.amount = orderCursor.price * (orderDTO.quantity ?: 0)
        orderCursor.insert()
        return map(orderCursor)
    }

    @CelestaTransaction
    open fun getItems(ctx: CallContext?): List<ItemDto> {

        ItemOrdersCursor(ctx).apply {
            tryFirst()
            println("--DTO--")
            println(orderedQuantity)
            println("----")
        }

        val item = ItemViewCursor(ctx)
        val result = arrayListOf<ItemDto>()
        item.forEach { result.add(map(it)) }
        return result
    }

    private fun map(src: ItemViewCursor): ItemDto =
        ItemDto(
            name = src.name,
            id = src.id,
            orderedQuantity = src.orderedQuantity
        )


    //NB: of course this should be generated
    private fun map(src: OrderCursor): OrderDto =
        OrderDto(
            amount = src.amount,
            quantity = src.quantity,
            itemId = src.itemId,
            price = src.price,
            customerId = src.customerId,
            id = src.id
        )

    private fun map(src: OrderDto, dst: OrderCursor) =
        dst.apply {
            amount = src.amount
            customerId = src.customerId
            price = src.price
            quantity = src.quantity
            itemId = src.itemId
            id = src.id
        }

}
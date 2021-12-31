package ru.inponomarev.celestademo.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import ru.inponomarev.celestademo.dto.ItemDto
import ru.inponomarev.celestademo.dto.OrderDto
import javax.servlet.http.HttpServletResponse

/*NB: my strong belief is that controller interface and
all the DTOs should be generated from OpenAPI (Swagger) spec.
*/
internal interface Controller {
    @PostMapping("/api/v1/order")
    fun postOrder(@RequestBody orderDTO: OrderDto, response: HttpServletResponse): OrderDto

    @GetMapping("/api/v1/item")
    fun getItems(response: HttpServletResponse): List<ItemDto?>
}
package ru.inponomarev.celestademo.controller

import org.springframework.web.bind.annotation.RestController
import ru.curs.celesta.SystemCallContext
import ru.inponomarev.celestademo.dao.OrderDao
import ru.inponomarev.celestademo.dto.ItemDto
import ru.inponomarev.celestademo.dto.OrderDto
import javax.servlet.http.HttpServletResponse

@RestController
class ControllerImpl (private val orderDao: OrderDao): Controller {
    override fun postOrder(orderDTO: OrderDto, response: HttpServletResponse): OrderDto {
        return orderDao.postOrder(SystemCallContext(), orderDTO)
    }

    override fun getItems(response: HttpServletResponse): List<ItemDto> {
        return orderDao.getItems(SystemCallContext())
    }
}
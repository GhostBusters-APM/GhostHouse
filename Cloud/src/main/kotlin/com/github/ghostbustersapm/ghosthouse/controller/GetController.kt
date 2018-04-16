package com.github.ghostbustersapm.ghosthouse.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalTime

@RestController
class GetController{

    @GetMapping("testget")
    fun get()="Hello World";

    data class Data(val hour:Int, val power:Int);
    data class Wrapper(val data:List<Data>);
    @GetMapping("historic")
    fun getHistoric(): Wrapper {
        val now = LocalTime.now();
        return Wrapper((0..23).map { Data(it,(Math.random()*20).toInt())})
    }

}
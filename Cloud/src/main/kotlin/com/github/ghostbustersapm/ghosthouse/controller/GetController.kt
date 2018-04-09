package com.github.ghostbustersapm.ghosthouse.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalTime

@RestController
class GetController{

@GetMapping("testget")
fun get()="Hello World";

data class Data(val hour:Int, val power:Int);

@GetMapping("historic")
fun getHistoric():List<Data> {
	val now = LocalTime.now();
	return (0..23).map { Data(it,now.minusHours(it.toLong()).hour)}
}

}
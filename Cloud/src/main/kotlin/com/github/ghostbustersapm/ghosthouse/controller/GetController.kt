package com.github.ghostbustersapm.ghosthouse.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class GetController{


@GetMapping("testget")
fun get()="Hello World"

}
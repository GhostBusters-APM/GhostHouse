package com.github.ghostbustersapm.ghosthouse

import com.github.ghostbustersapm.ghosthouse.controller.GetController
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.beans.factory.annotation.Autowired



@RunWith(SpringRunner::class)
@SpringBootTest
class GhostHouseApplicationTests {

	@Autowired
	private lateinit var controller: GetController

	@Test
	fun contextLoads() {
		assertThat(controller).isNotNull();
	}

}

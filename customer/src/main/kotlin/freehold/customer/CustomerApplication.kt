package freehold.customer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer
import org.springframework.cloud.openfeign.EnableFeignClients

@EnableFeignClients
@EnableEurekaServer
@SpringBootApplication
class CustomerApplication

fun main(args: Array<String>) {
	runApplication<CustomerApplication>(*args)
}

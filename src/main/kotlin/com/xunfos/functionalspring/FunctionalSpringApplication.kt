package com.xunfos.functionalspring

import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.annotation.Id
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyAndAwait
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter
import java.util.concurrent.Executors

@SpringBootApplication
class FunctionalSpringApplication(
    private val customerRepository: CustomerRepository,
    private val doWorkService: DoWorkService,
) {
    @Bean
    fun router() = coRouter {
        GET("/fun/customers") {
            trace("Hello world")
            ServerResponse.ok().bodyAndAwait(customerRepository.findAll())
        }
        GET("/fun/doWork", handler())
    }

    private fun handler(): suspend (ServerRequest) -> ServerResponse = {
        trace("Doing Work")
        doWorkService.doWork()
        ServerResponse.ok().bodyValueAndAwait("Work done")
    }
}

@RestController
class TypicalController(
    private val customerRepository: CustomerRepository,
    private val doWorkService: DoWorkService,
) {
    @GetMapping("/customers")
    suspend fun getCustomers(): Flow<Customer> {
        trace("Getting customers")
        return customerRepository.findAll()
    }

    @GetMapping("/doWork")
    suspend fun doWork(): String {
        trace("Doing Work")
        doWorkService.doWork()
        return "Work Done"
    }
}

/**
 * Service that does some bogus, arbitrary work, to demonstrate the usage of a loom dispatcher.
 */
@Service
class DoWorkService {
    private val loomCoroutineContext =
        Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher() + SupervisorJob()

    suspend fun doWork() = withContext(loomCoroutineContext) {
        repeat(10_000) {
            launch {
                trace("Doing blocking work")
                Thread.sleep(100)
            }
        }
    }
}

interface CustomerRepository : CoroutineCrudRepository<Customer, Int>
data class Customer(@Id val id: Int? = null, val name: String)

fun main(args: Array<String>) {
    runApplication<FunctionalSpringApplication>(*args)
}

fun trace(msg: Any) = println("[${Thread.currentThread().name}] $msg")

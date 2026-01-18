package com.kirndoubleb.labs.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.DynamicPropertyRegistrar
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration(proxyBeanMethods = false)
class TestContainersConfig {

    @Bean
    fun mongoDBContainer(): MongoDBContainer {
        return MongoDBContainer(DockerImageName.parse("mongo:7.0")).apply {
            start()
        }
    }

    @Bean
    fun mongoDbProperties(mongoDBContainer: MongoDBContainer): DynamicPropertyRegistrar {
        return DynamicPropertyRegistrar { registry ->
            registry.add("spring.data.mongodb.uri") { mongoDBContainer.replicaSetUrl }
        }
    }
}

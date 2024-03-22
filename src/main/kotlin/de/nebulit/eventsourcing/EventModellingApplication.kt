package de.nebulit.eventsourcing

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.scheduling.annotation.EnableScheduling

@EnableKafka
@EnableJpaRepositories
@SpringBootApplication
@EnableScheduling
@EntityScan(basePackages = ["de.nebulit"])
class ChallengerApplication

fun main(args: Array<String>) {
    runApplication<ChallengerApplication>(*args)
}

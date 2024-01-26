package de.nebulit.eventsourcing.challenge.persistence

import de.nebulit.eventsourcing.challenge.domain.Challenge
import org.springframework.data.repository.CrudRepository
import java.util.*

interface GoalRepository : CrudRepository<Challenge, Long> {
    fun findByAggregateId(aggregateId: UUID): Challenge?
}

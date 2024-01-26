package de.nebulit.eventsourcing.challenge.slices.challengestart

import de.nebulit.eventsourcing.common.AggregateService
import de.nebulit.eventsourcing.common.toInternalRecord
import de.nebulit.eventsourcing.testsupport.BaseIntegrationTest
import de.nebulit.eventsourcing.testsupport.CommandHandlerBddTestBuilder
import de.nebulit.eventsourcing.challenge.domain.Challenge
import de.nebulit.events.challengestart.ChallengeStarted
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

class StartChallengeCommandHandlerTest : BaseIntegrationTest() {
    private lateinit var testee: StartChallengeCommandHandler

    @Autowired
    private lateinit var aggregateService: AggregateService<Challenge>

    @BeforeEach
    fun setUp() {
        testee = StartChallengeCommandHandler(aggregateService)
    }

    @Test
    fun `raises an error the challenge already exists`() {

        val aggregateId = UUID.randomUUID()

        CommandHandlerBddTestBuilder.take(aggregateId, aggregateService, testee)
            .assumeAggregate(Challenge(aggregateId))
            .givenInternalEvent(ChallengeStarted(aggregateId).toInternalRecord(aggregateId))
            .assumeFailure()
            .whenever(StartChallengeCommand(aggregateId))
            .expectFailure()
    }

    @Test
    fun `creates new challenge if none exists already`() {

        val aggregateId = UUID.randomUUID()

        CommandHandlerBddTestBuilder.take(aggregateId, aggregateService, testee)
            .givenInternalEvent()
            .whenever(StartChallengeCommand(aggregateId))
            .expect(ChallengeStarted().apply {
                this.aggregateId = aggregateId
            })
    }
}

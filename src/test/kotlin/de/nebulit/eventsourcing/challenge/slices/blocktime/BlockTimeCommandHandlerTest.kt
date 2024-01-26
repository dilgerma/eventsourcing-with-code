package de.nebulit.eventsourcing.challenge.slices.blocktime

import de.nebulit.eventsourcing.common.AggregateService
import de.nebulit.eventsourcing.common.toInternalRecord
import de.nebulit.eventsourcing.testsupport.BaseIntegrationTest
import de.nebulit.eventsourcing.testsupport.CommandHandlerBddTestBuilder.Companion.take
import de.nebulit.eventsourcing.testsupport.RandomData
import de.nebulit.eventsourcing.challenge.domain.Challenge
import de.nebulit.events.additem.GoalAdded
import de.nebulit.events.blocktime.SchedulePlanned
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

class BlockTimeCommandHandlerTest : BaseIntegrationTest() {
    private lateinit var testee: BlockTimeCommandHandler

    @Autowired
    private lateinit var aggregateService: AggregateService<Challenge>

    @BeforeEach
    fun setUp() {
        testee = BlockTimeCommandHandler(aggregateService)
    }

    @Test
    fun `starting schedule without prior adding items yields error`() {

        val aggregateId = UUID.randomUUID()
        val timeBlocked = 120

        take(aggregateId, aggregateService, testee)
            .assumeAggregate(Challenge(aggregateId))
            .givenInternalEvent()
            .assumeFailure()
            .whenever(BlockTimeCommand(aggregateId, timeBlocked))
            .expectFailure()
    }

    @Test
    fun `starting schedule with prior adding items`() {

        val aggregateId = UUID.randomUUID()
        val timeBlocked = 120

        take(aggregateId, aggregateService, testee)

            .assumeAggregate(Challenge(aggregateId))

            .givenInternalEvent(
                RandomData.newInstance<GoalAdded> {
                    this.aggregateId = aggregateId
                }.toInternalRecord(aggregateId)
            )

            .whenever(BlockTimeCommand(aggregateId, timeBlocked))

            .expect(SchedulePlanned().apply {
                this.aggregateId = aggregateId
                this.durationInMinutes = timeBlocked
            })
    }
}

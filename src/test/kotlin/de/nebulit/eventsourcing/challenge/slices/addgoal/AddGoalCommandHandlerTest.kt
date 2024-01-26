package de.nebulit.eventsourcing.challenge.slices.addgoal

import de.nebulit.eventsourcing.common.AggregateService
import de.nebulit.eventsourcing.common.toInternalRecord
import de.nebulit.eventsourcing.testsupport.BaseIntegrationTest
import de.nebulit.eventsourcing.testsupport.CommandHandlerBddTestBuilder.Companion.take
import de.nebulit.eventsourcing.testsupport.RandomData
import de.nebulit.eventsourcing.challenge.domain.Challenge
import de.nebulit.events.additem.GoalAdded
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

class AddGoalCommandHandlerTest : BaseIntegrationTest() {

    private lateinit var testee: AddGoalCommandHandler

    @Autowired
    private lateinit var aggregateService: AggregateService<Challenge>

    @BeforeEach
    fun setUp() {
        testee = AddGoalCommandHandler(aggregateService)
    }

    @Test
    fun `adding goals yields goal adde^d`() {
        val aggregateId = UUID.randomUUID()
        val itemId = UUID.randomUUID()
        val description = "description"

        take(aggregateId, aggregateService, testee)

            .assumeAggregate(Challenge(aggregateId))

            .givenInternalEvent()

            .whenever(AddGoalCommand(aggregateId, itemId, description))

            .expect(GoalAdded().apply {
                this.aggregateId = aggregateId
                this.itemId = itemId
                this.description = description
            })
    }

    @Test
    fun `cannot add same id twice`() {
        val aggregateId = UUID.randomUUID()
        val itemId = UUID.randomUUID()
        val description = "description"

        take(aggregateId, aggregateService, testee)
            .assumeAggregate(Challenge(aggregateId))
            .givenInternalEvent(GoalAdded().apply {
                this.aggregateId = aggregateId
                this.itemId = itemId
                this.description = "another description"
            }.toInternalRecord(aggregateId))
            .assumeFailure()
            .whenever(AddGoalCommand(aggregateId, itemId, description))
    }

    @Test
    fun `cannot add same description twice`() {
        val aggregateId = UUID.randomUUID()
        val itemId = UUID.randomUUID()
        val description = "description"

        take(aggregateId, aggregateService, testee)
            .assumeAggregate(Challenge(aggregateId))
            .givenInternalEvent(GoalAdded().apply {
                this.aggregateId = aggregateId
                this.itemId = UUID.randomUUID()
                this.description = description
            }.toInternalRecord(aggregateId))
            .assumeFailure()
            .whenever(AddGoalCommand(aggregateId, itemId, description))
    }

    @Test
    fun `cannot add more than 5 goals`() {
        val aggregateId = UUID.randomUUID()
        val itemId = UUID.randomUUID()
        val description = "description"

        val fiveEvents = (1..5).map {
            RandomData.newInstance<GoalAdded> {
                this.aggregateId = aggregateId
            }.toInternalRecord(aggregateId)
        }.toTypedArray()

        take(aggregateId, aggregateService, testee)
            .assumeAggregate(Challenge(aggregateId))
            .givenInternalEvent(*fiveEvents)
            .assumeFailure()
            .whenever(AddGoalCommand(aggregateId, itemId, description))
    }
}

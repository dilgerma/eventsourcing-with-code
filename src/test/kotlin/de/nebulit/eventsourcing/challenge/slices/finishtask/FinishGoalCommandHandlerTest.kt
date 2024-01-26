package de.nebulit.eventsourcing.challenge.slices.finishtask

import de.nebulit.eventsourcing.common.AggregateService
import de.nebulit.eventsourcing.common.toInternalRecord
import de.nebulit.eventsourcing.testsupport.BaseIntegrationTest
import de.nebulit.eventsourcing.testsupport.CommandHandlerBddTestBuilder.Companion.take
import de.nebulit.eventsourcing.testsupport.RandomData
import de.nebulit.eventsourcing.challenge.domain.Challenge
import de.nebulit.events.finishgoal.TaskFinished
import de.nebulit.events.itemscheduler.TaskScheduled
import de.nebulit.events.schedule.TaskPlanned
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

class FinishGoalCommandHandlerTest : BaseIntegrationTest() {
    private lateinit var testee: FinishGoalCommandHandler

    @Autowired
    private lateinit var aggregateService: AggregateService<Challenge>

    @BeforeEach
    fun setUp() {
        testee = FinishGoalCommandHandler(aggregateService)
    }

    @Test
    fun `raises an error if we try to finish a goal that it not active`() {

        val aggregateId = UUID.randomUUID()
        val itemId = UUID.randomUUID()


        take(aggregateId, aggregateService, testee)
            .assumeAggregate(Challenge(aggregateId))
            .givenInternalEvent()
            .assumeFailure()
            .whenever(FinishTaskCommand(aggregateId, itemId))
            .expectFailure()
    }

    @Test
    fun `allows to finish a goal that is active`() {

        val aggregateId = UUID.randomUUID()
        val itemId = UUID.randomUUID()

        take(aggregateId, aggregateService, testee)
            .assumeAggregate(Challenge(aggregateId))
            .givenInternalEvent(
                RandomData.newInstance<TaskPlanned> {
                    this.aggregateId = aggregateId
                    this.itemId = itemId
                }.toInternalRecord(aggregateId),
                RandomData.newInstance<TaskScheduled> {
                    this.aggregateId = aggregateId
                    this.itemId = itemId
                }.toInternalRecord(aggregateId),
            )
            .whenever(FinishTaskCommand(aggregateId, itemId))
            .expect(TaskFinished().apply {
                this.aggregateId = aggregateId
                this.itemId = itemId
            })
    }
}

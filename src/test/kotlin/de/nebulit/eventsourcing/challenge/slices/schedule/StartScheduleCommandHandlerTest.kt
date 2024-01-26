package de.nebulit.eventsourcing.challenge.slices.schedule

import de.nebulit.eventsourcing.common.AggregateService
import de.nebulit.eventsourcing.common.toInternalRecord
import de.nebulit.eventsourcing.testsupport.BaseIntegrationTest
import de.nebulit.eventsourcing.testsupport.CommandHandlerBddTestBuilder.Companion.take
import de.nebulit.eventsourcing.testsupport.RandomData
import de.nebulit.eventsourcing.challenge.domain.Challenge
import de.nebulit.eventsourcing.challenge.slices.agenda.StartScheduleCommand
import de.nebulit.eventsourcing.challenge.slices.agenda.StartScheduleCommandHandler
import de.nebulit.events.additem.GoalAdded
import de.nebulit.events.blocktime.SchedulePlanned
import de.nebulit.events.schedule.ScheduleStarted
import de.nebulit.events.schedule.TaskPlanned
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalTime
import java.util.*

class StartScheduleCommandHandlerTest : BaseIntegrationTest() {

    private lateinit var testee: StartScheduleCommandHandler

    @Autowired
    private lateinit var aggregateService: AggregateService<Challenge>

    @BeforeEach
    fun setUp() {
        testee = StartScheduleCommandHandler(aggregateService)
    }

    @Test
    fun `starting schedule with prior adding items`() {

        val aggregateId = UUID.randomUUID()
        val itemId = UUID.randomUUID()

        take(aggregateId, aggregateService, testee)
            .assumeAggregate(Challenge(aggregateId))
            .givenInternalEvent(
                GoalAdded().apply {
                    this.aggregateId = aggregateId
                    this.itemId = itemId
                    this.description = "foo"
                }.toInternalRecord(aggregateId),
                RandomData.newInstance<SchedulePlanned> {
                    this.aggregateId = aggregateId
                }.toInternalRecord(aggregateId),
                RandomData.newInstance<TaskPlanned> {
                    this.aggregateId = aggregateId
                    this.itemId = itemId
                    this.starttime = LocalTime.now()
                }.toInternalRecord(aggregateId),

                )
            .whenever(StartScheduleCommand(aggregateId))
            .expect(ScheduleStarted().apply {
                this.aggregateId = aggregateId
            })
    }
}

package de.nebulit.eventsourcing.challenge.slices.taskscheduler

import de.nebulit.eventsourcing.common.AggregateService
import de.nebulit.eventsourcing.common.DelegatingCommandHandler
import de.nebulit.eventsourcing.common.persistence.EventsEntityRepository
import de.nebulit.eventsourcing.common.toInternalRecord
import de.nebulit.eventsourcing.testsupport.BaseIntegrationTest
import de.nebulit.eventsourcing.testsupport.ProcessorBddTestBuilder.Companion.take
import de.nebulit.eventsourcing.testsupport.RandomData
import de.nebulit.eventsourcing.challenge.domain.Challenge
import de.nebulit.eventsourcing.challenge.slices.schedule.ChallengeTaskScheduler
import de.nebulit.events.blocktime.SchedulePlanned
import de.nebulit.events.itemscheduler.TaskDiscarded
import de.nebulit.events.itemscheduler.TaskScheduled
import de.nebulit.events.schedule.ScheduleStarted
import de.nebulit.events.schedule.TaskPlanned
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalTime
import java.util.*

class ChallengeTaskSchedulerTest : BaseIntegrationTest() {


    private lateinit var testee: ChallengeTaskScheduler

    @Autowired
    private lateinit var eventsEntityRepository: EventsEntityRepository

    @Autowired
    private lateinit var aggregateService: AggregateService<Challenge>

    @Autowired
    private lateinit var delegatingCommandHandler: DelegatingCommandHandler<Challenge>

    @BeforeEach
    fun setUp() {
        testee = ChallengeTaskScheduler(eventsEntityRepository, aggregateService, delegatingCommandHandler)
    }

    @Test
    fun `starting schedule with one item ready to be scheduled and no prior active items`() {

        val aggregateId = UUID.randomUUID()
        val itemId = UUID.randomUUID()

        take(aggregateId, aggregateService, testee)
            .assumeAggregates(Challenge(aggregateId))
            .givenInternalEvent(
                RandomData.newInstance<TaskPlanned> {
                    this.aggregateId = aggregateId
                    this.itemId = itemId
                    this.starttime = LocalTime.now().minusMinutes(1)
                    this.durationInMinutes=5
                }.toInternalRecord(aggregateId),
                ScheduleStarted().apply {
                    this.aggregateId = aggregateId
                }.toInternalRecord(aggregateId)
            )
            .wheneverProcessorInvoked()
            .expect(aggregateId, TaskScheduled().apply {
                this.aggregateId = aggregateId
                this.itemId = itemId
            })
    }

    @Test
    fun `starting schedule with one item ready to be scheduled and one active item`() {

        val aggregateId = UUID.randomUUID()
        val itemId = UUID.randomUUID()
        val scheduledItemId = UUID.randomUUID()

        take(aggregateId, aggregateService, testee)
            .assumeAggregates(Challenge(aggregateId))
            .givenInternalEvent(
                SchedulePlanned().apply {
                    this.aggregateId = aggregateId
                }.toInternalRecord(aggregateId),
                RandomData.newInstance<TaskPlanned> {
                    this.aggregateId = aggregateId
                    this.itemId = itemId
                    this.starttime = LocalTime.now().minusMinutes(1)
                    durationInMinutes=5
                }.toInternalRecord(aggregateId),
                TaskScheduled().apply {
                    this.aggregateId = aggregateId
                    this.itemId = scheduledItemId
                }.toInternalRecord(aggregateId),
                ScheduleStarted().apply {
                    this.aggregateId = aggregateId
                }.toInternalRecord(aggregateId)
            )
            .wheneverProcessorInvoked()
            .expect(aggregateId, TaskScheduled().apply {
                this.aggregateId = aggregateId
                this.itemId = itemId
            }, TaskDiscarded().apply {
                this.aggregateId = aggregateId
                this.itemId = scheduledItemId
            })
    }
}

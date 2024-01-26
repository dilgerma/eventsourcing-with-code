package de.nebulit.eventsourcing.challenge.slices.agenda

import com.ninjasquad.springmockk.MockkBean
import de.nebulit.events.additem.GoalAdded
import de.nebulit.eventsourcing.common.AggregateService
import de.nebulit.eventsourcing.common.DelegatingCommandHandler
import de.nebulit.eventsourcing.common.persistence.EventsEntityRepository
import de.nebulit.eventsourcing.common.toInternalRecord
import de.nebulit.eventsourcing.testsupport.BaseIntegrationTest
import de.nebulit.eventsourcing.testsupport.ProcessorBddTestBuilder.Companion.take
import de.nebulit.eventsourcing.testsupport.RandomData
import de.nebulit.eventsourcing.challenge.domain.Challenge
import de.nebulit.events.blocktime.SchedulePlanned
import de.nebulit.events.schedule.ScheduleStarted
import de.nebulit.events.schedule.TaskPlanned
import de.nebulit.eventsourcing.challenge.slices.agenda.openai.ScheduleResponse
import de.nebulit.eventsourcing.challenge.slices.agenda.openai.ScheduleTask
import de.nebulit.eventsourcing.challenge.slices.agenda.openai.TaskScheduleCalculator
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

class AgendaSchedulerTest : BaseIntegrationTest() {


    private lateinit var testee: AgendaScheduleProcessor

    @Autowired
    private lateinit var eventsEntityRepository: EventsEntityRepository

    @Autowired
    private lateinit var aggregateService: AggregateService<Challenge>

    @Autowired
    private lateinit var delegatingCommandHandler: DelegatingCommandHandler<Challenge>

    @MockkBean(relaxed = true)
    private lateinit var scheduleCalculator: TaskScheduleCalculator

    @BeforeEach
    fun setUp() {
        testee = AgendaScheduleProcessor(
            eventsEntityRepository,
            aggregateService,
            delegatingCommandHandler,
            scheduleCalculator
        )
    }

    @Test
    fun `starting schedule with one added goal and 2 tasks returned from API`() {

        val aggregateId = UUID.randomUUID()
        val goalId = UUID.randomUUID()
        val description1 = "debug description"
        val description2 = "debug description2"

        every { scheduleCalculator.calculateSchedule(any(), any()) } returns ScheduleResponse(
            listOf(
                ScheduleTask("12:00", description1, 5),
                ScheduleTask("12:00", description2, 5)
            )
        )

        take(aggregateId, aggregateService, testee)
            .assumeAggregates(Challenge(aggregateId))
            .givenInternalEvent(
                RandomData.newInstance<GoalAdded> {
                    this.aggregateId = aggregateId
                    this.itemId = goalId
                }.toInternalRecord(aggregateId),
                RandomData.newInstance<SchedulePlanned> {
                    this.aggregateId = aggregateId
                }.toInternalRecord(aggregateId),
            )
            .wheneverProcessorInvoked()
            .expect(aggregateId, ScheduleStarted().apply {
                this.aggregateId = aggregateId
            })
            .expect(aggregateId) { events ->
                //cannot match against event, as we do not know the taskId at this point
                assertThat(events.filter { it.value is TaskPlanned }).hasSize(2)
                assertThat(events.filter { it.value is TaskPlanned }
                    .map { (it.value as TaskPlanned).description.toString() }).containsExactlyInAnyOrder(
                    description1, description2
                )
            }
    }

    @Test
    fun `starting schedule with one added goal and no tasks returned from API`() {

        val aggregateId = UUID.randomUUID()
        val goalId = UUID.randomUUID()

        every { scheduleCalculator.calculateSchedule(any(), any()) } returns ScheduleResponse(
            emptyList()
        )

        take(aggregateId, aggregateService, testee)
            .assumeAggregates(Challenge(aggregateId))
            .givenInternalEvent(
                RandomData.newInstance<GoalAdded> {
                    this.aggregateId = aggregateId
                    this.itemId = goalId
                }.toInternalRecord(aggregateId),
                RandomData.newInstance<SchedulePlanned> {
                    this.aggregateId = aggregateId
                }.toInternalRecord(aggregateId),
            )
            .assumeFailure()
            .wheneverProcessorInvoked()
            .expectFailure()
    }

}

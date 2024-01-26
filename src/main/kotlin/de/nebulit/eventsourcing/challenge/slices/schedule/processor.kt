package de.nebulit.eventsourcing.challenge.slices.schedule

import de.nebulit.eventsourcing.common.AggregateService
import de.nebulit.eventsourcing.common.DelegatingCommandHandler
import de.nebulit.eventsourcing.common.Processor
import de.nebulit.eventsourcing.common.persistence.EventsEntityRepository
import de.nebulit.eventsourcing.common.persistence.InternalEvent
import de.nebulit.eventsourcing.challenge.domain.Challenge
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.LocalTime


@Component
class ChallengeTaskScheduler(
        val eventsEntityRepository: EventsEntityRepository,
        val aggregateService: AggregateService<Challenge>,
        val commandHandler: DelegatingCommandHandler<Challenge>
) : Processor<ActivePlannedTasksReadModel> {

    @Scheduled(fixedDelayString = "10000")
    override fun process() {
        var allEvents = findTodaysEvents()
        val activeSchedules = ActiveSchedulesReadModel().applyEvents(allEvents)

        activeSchedules.activeSchedules.forEach { aggregateId ->
            val events = aggregateService.findEventsByAggregateId(aggregateId)
            val activeSchedulesAndItems = applyEvents(events)
            if (activeSchedulesAndItems.plannedTasks.isNotEmpty()) {
                commandHandler.handle(ScheduleNextTaskCommand(aggregateId))
            }

        }

    }

    override fun applyEvents(events: List<InternalEvent>): ActivePlannedTasksReadModel {
        return ActivePlannedTasksReadModel().applyEvents(events)
    }

    private fun findTodaysEvents() = eventsEntityRepository.findAllByCreatedBetween(
            LocalDateTime.now().toLocalDate().atStartOfDay(),
            LocalDateTime.now().toLocalDate().atTime(LocalTime.MAX)
    )


}

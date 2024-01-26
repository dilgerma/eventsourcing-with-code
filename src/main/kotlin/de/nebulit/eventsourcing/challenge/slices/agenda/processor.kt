package de.nebulit.eventsourcing.challenge.slices.agenda

import de.nebulit.eventsourcing.common.AggregateService
import de.nebulit.eventsourcing.common.DelegatingCommandHandler
import de.nebulit.eventsourcing.common.Processor
import de.nebulit.eventsourcing.common.persistence.EventsEntityRepository
import de.nebulit.eventsourcing.common.persistence.InternalEvent
import de.nebulit.eventsourcing.challenge.domain.Challenge
import de.nebulit.eventsourcing.challenge.slices.agenda.openai.TaskScheduleCalculator
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.LocalTime


@Component
class AgendaScheduleProcessor(
        val eventsEntityRepository: EventsEntityRepository,
        val aggregateService: AggregateService<Challenge>,
        val commandHandler: DelegatingCommandHandler<Challenge>,
        val goalScheduleCalculator: TaskScheduleCalculator
) : Processor<PlannedGoalsToBeProcessedReadModel> {

    @Scheduled(fixedDelayString = "2000")
    override fun process() {
        val activeSchedules = ActiveSchedulesToBeProcessed().applyEvents(findTodaysEvents())

        activeSchedules.activeSchedulesToBeProcessed.forEach { it ->
            var events = aggregateService.findEventsByAggregateId(it)
            val goalsToBePlanned = PlannedGoalsToBeProcessedReadModel().applyEvents(events)


            var preparedSchedule = goalScheduleCalculator.calculateSchedule(
                    goalsToBePlanned.goalsToBePlanned.map { item -> item.description },
                    goalsToBePlanned.timeAvailable ?: 0
            )

            var tasks = preparedSchedule.schedule.map { item -> NewTasks(LocalTime.parse(item.time), item.description, item.points) }
            commandHandler.handle(PlanNewTasksCommand(it, tasks))
            commandHandler.handle(StartScheduleCommand(it))
        }
    }


    override fun applyEvents(events: List<InternalEvent>): PlannedGoalsToBeProcessedReadModel {
        return PlannedGoalsToBeProcessedReadModel().applyEvents(events)
    }

    private fun findTodaysEvents() = eventsEntityRepository.findAllByCreatedBetween(
            LocalDateTime.now().toLocalDate().atStartOfDay(),
            LocalDateTime.now().toLocalDate().atTime(LocalTime.MAX)
    )


}

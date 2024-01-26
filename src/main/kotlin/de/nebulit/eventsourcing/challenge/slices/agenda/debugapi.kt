package de.nebulit.eventsourcing.challenge.slices.agenda


import de.nebulit.eventsourcing.common.AggregateService
import de.nebulit.eventsourcing.common.toInternalRecord
import de.nebulit.eventsourcing.challenge.domain.Challenge
import de.nebulit.eventsourcing.challenge.ui.PageModel
import de.nebulit.events.additem.GoalAdded
import de.nebulit.events.blocktime.SchedulePlanned
import de.nebulit.events.schedule.ScheduleStarted
import de.nebulit.events.schedule.TaskPlanned
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.lang.IllegalStateException
import java.time.LocalTime
import java.util.*


@RestController
class AgendaDebugResource(
    var aggregateService: AggregateService<Challenge>
) {

    @PostMapping(path = [apiPath])
    @CrossOrigin
    fun api(
        @PathVariable("aggregateId") aggregateId: UUID,
    ): PageModel {

        var aggregate = aggregateService.findByAggregateId(aggregateId)
            ?: throw IllegalStateException("Aggregate $aggregateId not found")
        aggregate.events.addAll(
            listOf(GoalAdded().apply {
                this.aggregateId = aggregateId
                this.itemId = UUID.randomUUID()
                this.description = "debug goal"
            }.toInternalRecord(aggregateId),
                SchedulePlanned().apply {
                    this.aggregateId = aggregateId
                    this.durationInMinutes = 120
                }.toInternalRecord(aggregateId),
                TaskPlanned().apply {
                    this.aggregateId = aggregateId
                    this.itemId = UUID.randomUUID()
                    this.description = "debug description 1"
                    this.durationInMinutes=25
                    this.starttime = LocalTime.now().plusMinutes(5)
                    this.points = 5
                }.toInternalRecord(aggregateId),
                TaskPlanned().apply {
                    this.aggregateId = aggregateId
                    this.itemId = UUID.randomUUID()
                    this.durationInMinutes=25
                    this.starttime = LocalTime.now().plusMinutes(1)
                    this.description = "debug description 2"
                    this.points = 5
                }.toInternalRecord(aggregateId),
                ScheduleStarted().apply {
                    this.aggregateId = aggregateId
                    this.durationInMinutes = 120
                }.toInternalRecord(aggregateId)
            )
        )
        aggregateService.persist(aggregate)
        return PageModel(aggregateId).applyEvents(aggregateService.findEventsByAggregateId(aggregateId))
    }

    companion object {
        const val apiPath: String = "/challenge/debug/agenda/{aggregateId}"
    }
}

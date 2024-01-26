package de.nebulit.eventsourcing.challenge.slices.addgoal


import de.nebulit.eventsourcing.common.AggregateService
import de.nebulit.eventsourcing.common.toInternalRecord
import de.nebulit.eventsourcing.challenge.domain.Challenge
import de.nebulit.eventsourcing.challenge.ui.PageModel
import de.nebulit.events.additem.GoalAdded
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.lang.IllegalStateException
import java.util.*


@RestController
class AddGoalDebugResource(
    var aggregateService: AggregateService<Challenge>
) {

    @PostMapping(path = [apiPath])
    @CrossOrigin
    fun api(
        @PathVariable("aggregateId") aggregateId: UUID,
    ): PageModel {

        var aggregate = aggregateService.findByAggregateId(aggregateId)
            ?: throw IllegalStateException("Aggregate $aggregateId not found")
        aggregate.events.add(
            GoalAdded().apply {
                this.aggregateId= aggregateId
                this.itemId = UUID.randomUUID()
                this.description = "debug goal"
            }.toInternalRecord(aggregateId)
        )
        aggregateService.persist(aggregate)
        return PageModel(aggregateId).applyEvents(aggregateService.findEventsByAggregateId(aggregateId))
    }

    companion object {
        const val apiPath: String = "/challenge/debug/addgoal/{aggregateId}"
    }
}

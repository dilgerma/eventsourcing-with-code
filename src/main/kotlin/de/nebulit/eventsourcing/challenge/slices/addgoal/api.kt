package de.nebulit.eventsourcing.challenge.slices.addgoal

import de.nebulit.eventsourcing.common.AggregateService
import de.nebulit.eventsourcing.common.DelegatingCommandHandler
import de.nebulit.eventsourcing.challenge.domain.Challenge
import de.nebulit.eventsourcing.challenge.ui.PageModel
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*


@RestController
class AddGoalResource(
    val delegatingCommandHandler: DelegatingCommandHandler<Challenge>,
    var aggregateService: AggregateService<Challenge>
) {

    @PostMapping(path = [apiPath])
    @CrossOrigin
    fun api(
        @PathVariable("aggregateId") aggregateId: UUID,
        @RequestParam("description") description: String
    ): PageModel {
        delegatingCommandHandler.handle(AddGoalCommand(aggregateId = aggregateId, description = description))
        return PageModel(aggregateId).applyEvents(aggregateService.findEventsByAggregateId(aggregateId))
    }

    companion object {
        const val apiPath: String = "/todo/{aggregateId}/item"
    }
}



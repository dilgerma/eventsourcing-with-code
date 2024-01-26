package de.nebulit.eventsourcing.challenge.slices.finishtask

import de.nebulit.eventsourcing.common.AggregateService
import de.nebulit.eventsourcing.common.DelegatingCommandHandler
import de.nebulit.eventsourcing.challenge.domain.Challenge
import de.nebulit.eventsourcing.challenge.ui.PageModel
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class FinishTimeResource(
    val aggregateService: AggregateService<Challenge>,
    val delegatingCommandHandler: DelegatingCommandHandler<Challenge>,
) {

    @PostMapping(path = [apiPath])
    @CrossOrigin
    fun finishTask(
        @PathVariable("aggregateId") aggregateId: UUID,
        @PathVariable("taskId") taskId: UUID
    ): PageModel {
        val command = FinishTaskCommand(aggregateId, taskId)
        delegatingCommandHandler.handle(command)
        return PageModel(aggregateId).applyEvents(aggregateService.findEventsByAggregateId(command.aggregateId))
    }

    companion object {
        const val apiPath: String = "/challenge/{aggregateId}/finish/{taskId}"
    }
}

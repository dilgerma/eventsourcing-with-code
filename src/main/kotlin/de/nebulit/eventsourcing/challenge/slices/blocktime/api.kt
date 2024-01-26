    package de.nebulit.eventsourcing.challenge.slices.blocktime

    import de.nebulit.eventsourcing.common.AggregateService
    import de.nebulit.eventsourcing.common.DelegatingCommandHandler
    import de.nebulit.eventsourcing.challenge.domain.Challenge
    import de.nebulit.eventsourcing.challenge.ui.PageModel
    import jakarta.servlet.http.HttpServletResponse
    import org.springframework.web.bind.annotation.*
    import java.util.*

    @RestController
    class BlockTimeResource(
            val aggregateService: AggregateService<Challenge>,
            val delegatingCommandHandler: DelegatingCommandHandler<Challenge>,
    ) {

        @PostMapping(path = [apiPath])
        @CrossOrigin
        fun startChallenge(
                @PathVariable("aggregateId") aggregateId: UUID,
                @RequestParam("minutes") minutes: Int,
                response: HttpServletResponse): PageModel {
            val command = BlockTimeCommand(aggregateId, minutes)
            delegatingCommandHandler.handle(command)
            return PageModel(aggregateId).applyEvents(aggregateService.findEventsByAggregateId(command.aggregateId))
        }

        companion object {
            const val apiPath: String = "/challenge/{aggregateId}/time"
        }
    }
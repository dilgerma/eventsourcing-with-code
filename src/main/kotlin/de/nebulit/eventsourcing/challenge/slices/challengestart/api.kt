package de.nebulit.eventsourcing.challenge.slices.challengestart

import de.nebulit.eventsourcing.common.AggregateService
import de.nebulit.eventsourcing.common.DelegatingCommandHandler
import de.nebulit.eventsourcing.challenge.domain.Challenge
import de.nebulit.eventsourcing.challenge.ui.PageModel
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
class StartChallengeResource(
    val aggregateService: AggregateService<Challenge>,
    val delegatingCommandHandler: DelegatingCommandHandler<Challenge>,
        ) {


    @GetMapping(path = [apiPath])
    @CrossOrigin
    fun getChallenge(response: HttpServletResponse, @RequestParam("challengeId") challengeId: UUID): PageModel {

        var events = aggregateService.findEventsByAggregateId(challengeId)
        var activeChallenges = ActiveChallengesReadModel().applyEvents(events)
        if (activeChallenges.activeChallenges.contains(challengeId)) {
            return PageModel(challengeId).applyEvents(events)
        } else {
            return PageModel(challengeId)
        }
    }

    @PostMapping(path = [apiPath])
    @CrossOrigin
    fun startChallenge(response: HttpServletResponse): PageModel {
        val command = StartChallengeCommand()
        delegatingCommandHandler.handle(command)
        var cookie = Cookie("challengeId", command.aggregateId.toString())
        cookie.secure = false
        cookie.isHttpOnly=false
        cookie.path = "/"
        response.addCookie(cookie)
        return PageModel(command.aggregateId).applyEvents(aggregateService.findEventsByAggregateId(command.aggregateId))
    }

    companion object {
        const val apiPath: String = "/challenge/start"
    }
}

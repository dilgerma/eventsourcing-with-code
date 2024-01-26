package de.nebulit.eventsourcing.challenge.slices.addgoal

import de.nebulit.eventsourcing.common.ReadModel
import de.nebulit.eventsourcing.common.persistence.InternalEvent
import de.nebulit.events.additem.GoalAdded
import java.util.*


class PlannedGoalsReadModel : ReadModel<PlannedGoalsReadModel> {

    var plannedGoals = emptyList<PlannedGoal>()

    override fun applyEvents(events: List<InternalEvent>): PlannedGoalsReadModel {
        val plannedGoals = (events.map { it.value }.mapNotNull {
            when (it) {
                is GoalAdded -> PlannedGoal(it.itemId, it.description.toString())
                else -> null
            }
        })
        this.plannedGoals = plannedGoals
        return this
    }

    data class PlannedGoal(val id: UUID, val description: String)
}




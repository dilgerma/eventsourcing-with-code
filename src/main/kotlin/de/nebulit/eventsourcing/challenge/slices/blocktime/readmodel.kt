package de.nebulit.eventsourcing.challenge.slices.blocktime

import de.nebulit.eventsourcing.common.ReadModel
import de.nebulit.eventsourcing.common.persistence.InternalEvent
import de.nebulit.events.additem.GoalAdded
import de.nebulit.events.blocktime.SchedulePlanned
import java.util.*


class BlockedTimeReadModel : ReadModel<BlockedTimeReadModel> {

    var timeBlocked: Int = 0
    var items = mutableMapOf<UUID, String>()

    override fun applyEvents(events: List<InternalEvent>): BlockedTimeReadModel {
        events.forEach {
            when (it.value) {
                is SchedulePlanned -> timeBlocked = (it.value as SchedulePlanned).durationInMinutes
                is GoalAdded -> items[(it.value as GoalAdded).itemId] = (it.value as GoalAdded).description.toString()
            }
        }
        return this
    }
}

package de.nebulit.eventsourcing.challenge.slices.addgoal

import de.nebulit.eventsourcing.common.Command
import java.util.*


data class AddGoalCommand(override var aggregateId: UUID, val id:UUID=UUID.randomUUID(), var description:String) : Command

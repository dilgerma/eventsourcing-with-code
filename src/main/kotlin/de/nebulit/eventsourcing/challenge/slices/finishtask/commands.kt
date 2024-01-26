package de.nebulit.eventsourcing.challenge.slices.finishtask

import de.nebulit.eventsourcing.common.Command
import java.util.*


data class FinishTaskCommand(override var aggregateId: UUID, val itemId:UUID) : Command

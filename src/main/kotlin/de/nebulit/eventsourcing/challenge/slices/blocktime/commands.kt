package de.nebulit.eventsourcing.challenge.slices.blocktime

import de.nebulit.eventsourcing.common.Command
import java.util.*


data class BlockTimeCommand(override var aggregateId: UUID, val minutes:Int) : Command

package de.nebulit.eventsourcing.challenge.slices.challengestart

import de.nebulit.eventsourcing.common.Command
import java.util.*


data class StartChallengeCommand(override var aggregateId: UUID = UUID.randomUUID()) : Command

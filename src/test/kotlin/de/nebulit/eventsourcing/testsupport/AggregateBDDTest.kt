package de.nebulit.eventsourcing.testsupport

import de.nebulit.eventsourcing.common.*
import de.nebulit.eventsourcing.common.persistence.InternalEvent


class ReadModelBddTestBuilder<T : EventState<T>>(val readModel: T) {

    fun given(vararg events: InternalEvent): ReadModelBddTestBuilder<T> {
        this.readModel.applyEvents(events.toList())
        return this
    }

    fun expect(
        block: ReadModelBddTestBuilder<T>.(agg: T) -> Unit
    ): ReadModelBddTestBuilder<T> {
        this.block(readModel)
        return this
    }

    companion object {
        // Extension function to create an instance of FluentInterfaceBuilder
        inline fun <reified T : ReadModel<T>> take(readModel: T): ReadModelBddTestBuilder<T> {
            return ReadModelBddTestBuilder(readModel)
        }
    }
}




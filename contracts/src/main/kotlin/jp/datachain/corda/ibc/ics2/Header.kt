package jp.datachain.corda.ibc.ics2

import ibc.core.client.v1.Client
import net.corda.core.serialization.CordaSerializable

@CordaSerializable
interface Header {
    fun clientType(): ClientType
    fun getHeight(): Client.Height
    fun validateBasic()
}

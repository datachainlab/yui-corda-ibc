package jp.datachain.corda.ibc.ics24

import ibc.core.client.v1.Client.Height
import jp.datachain.corda.ibc.clients.corda.CordaConsensusState
import jp.datachain.corda.ibc.contracts.Ibc
import jp.datachain.corda.ibc.ics23.CommitmentPrefix
import jp.datachain.corda.ibc.states.IbcState
import jp.datachain.corda.ibc.types.Timestamp
import jp.datachain.corda.ibc.types.Version
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.StateRef
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party

@BelongsToContract(Ibc::class)
data class Host constructor (
        override val participants: List<AbstractParty>,
        override val baseId: StateRef,
        val notary: Party,
        val clientIds: List<Identifier>,
        val connIds: List<Identifier>,
        val portChanIds: List<Pair<Identifier, Identifier>>
) : IbcState {
    override val id = Identifier("host")

    constructor(genesisAndRef: StateAndRef<Genesis>) : this(
            genesisAndRef.state.data.participants,
            genesisAndRef.ref,
            genesisAndRef.state.notary,
            emptyList(),
            emptyList(),
            emptyList()
    )

    fun getCurrentHeight() = Height.getDefaultInstance()

    fun getStoredRecentConsensusStateCount() = 1

    fun getConsensusState(height: Height) : CordaConsensusState {
        require(height == getCurrentHeight())
        return CordaConsensusState(Timestamp(0), height, baseId, notary.owningKey)
    }

    fun getCommitmentPrefix() = CommitmentPrefix(baseId.toString().toByteArray(charset = Charsets.US_ASCII))

    fun currentTimestamp() = Timestamp(0)

    fun getCompatibleVersions() = listOf(Version(""))
    fun pickVersion(versions: Collection<Version>) = versions.single()

    fun addClient(id: Identifier) : Host {
        require(!clientIds.contains(id))
        return copy(clientIds = clientIds + id)
    }

    fun addConnection(id: Identifier) : Host {
        require(!connIds.contains(id))
        return copy(connIds = connIds + id)
    }

    fun addPortChannel(portId: Identifier, chanId: Identifier) : Host {
        val portChanId = Pair(portId, chanId)
        require(!portChanIds.contains(portChanId))
        return copy(portChanIds = portChanIds + portChanId)
    }
}
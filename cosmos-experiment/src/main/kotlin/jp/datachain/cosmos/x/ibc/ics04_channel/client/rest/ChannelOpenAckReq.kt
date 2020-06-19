package jp.datachain.cosmos.x.ibc.ics04_channel.client.rest

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jp.datachain.corda.ibc.cosmos.CosmosRequest
import jp.datachain.corda.ibc.cosmos.ReqPath
import jp.datachain.cosmos.types.rest.BaseReq
import jp.datachain.cosmos.x.ibc.ics23_commitment.types.MerkleProof

@ReqPath("ibc/ports/%s/channels/%s/open-ack")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class ChannelOpenAckReq(
        val counterpartyVersion: String,
        val proofTry: MerkleProof,
        val proofHeight: String /*uint64*/
) : CosmosRequest {
        override var baseReq: BaseReq? = null
}
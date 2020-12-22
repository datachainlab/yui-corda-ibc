package jp.datachain.corda.ibc.ics26

import ibc.core.client.v1.Client.Height
import jp.datachain.corda.ibc.ics23.CommitmentProof
import jp.datachain.corda.ibc.ics24.Identifier
import jp.datachain.corda.ibc.ics25.Handler
import java.security.PublicKey

data class HandleChanOpenConfirm(
        val portIdentifier: Identifier,
        val channelIdentifier: Identifier,
        val proofAck: CommitmentProof,
        val proofHeight: Height
): DatagramHandler {
    override fun execute(ctx: Context, signers: Collection<PublicKey>) {
        Handler.chanOpenConfirm(
                ctx,
                portIdentifier,
                channelIdentifier,
                proofAck,
                proofHeight)
    }
}
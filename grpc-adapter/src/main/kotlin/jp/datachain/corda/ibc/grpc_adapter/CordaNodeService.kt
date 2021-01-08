package jp.datachain.corda.ibc.grpc_adapter

import io.grpc.stub.StreamObserver
import jp.datachain.corda.ibc.conversion.into
import jp.datachain.corda.ibc.grpc.*
import net.corda.client.rpc.CordaRPCClient
import net.corda.core.utilities.NetworkHostAndPort

class CordaNodeService(host: String, port: Int, username: String, password: String): NodeServiceGrpc.NodeServiceImplBase() {
    private val ops = CordaRPCClient(NetworkHostAndPort(host, port))
            .start(username, password)
            .proxy

    override fun partiesFromName(request: Operation.PartiesFromNameRequest, responseObserver: StreamObserver<Operation.PartiesFromNameResponse>) {
        val parties = ops.partiesFromName(request.name, request.exactMatch)
        val response = Operation.PartiesFromNameResponse.newBuilder()
                .addAllParties(parties.map{it.into()})
                .build()
        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }
}
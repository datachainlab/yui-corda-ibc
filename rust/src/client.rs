use super::generated::ibc;
use super::util;
use super::Result;

use ibc::core::client::v1 as ibc_client;
use ibc::lightclients::corda::v1 as ibc_corda;

use prost_types::Any;

async fn connect_tx(
    endpoint: String,
) -> Result<ibc_client::msg_client::MsgClient<tonic::transport::Channel>> {
    let client = ibc_client::msg_client::MsgClient::connect(endpoint).await?;
    Ok(client)
}

async fn connect_query(
    endpoint: String,
) -> Result<ibc_client::query_client::QueryClient<tonic::transport::Channel>> {
    let client = ibc_client::query_client::QueryClient::connect(endpoint).await?;
    Ok(client)
}

pub async fn create_client(
    endpoint: String,
    client_id: String,
    client_state: Any,
    consensus_state: Any,
) -> Result<()> {
    let mut client = connect_tx(endpoint).await?;
    client
        .create_client(ibc_client::MsgCreateClient {
            client_id,
            client_state: Some(client_state),
            consensus_state: Some(consensus_state),
            signer: String::default(),
        })
        .await?;
    Ok(())
}

pub async fn create_corda_client(
    endpoint: String,
    client_id: String,
    counterparty_base_hash: String,
    counterparty_notary_key: String,
) -> Result<()> {
    let client_state = ibc_corda::ClientState { id: client_id };
    let any_client_state = util::pack_any(
        "/ibc.lightclients.corda.v1.ClientState".to_owned(),
        &client_state,
    )?;
    let client_id = client_state.id;

    let any_consensus_state = util::pack_any(
        "/ibc.lightclients.corda.v1.ConsensusState".to_owned(),
        &ibc_corda::ConsensusState {
            base_id: Some(util::hex_to_base_id(&counterparty_base_hash)?),
            notary_key: Some(util::hex_to_public_key(&counterparty_notary_key)?),
        },
    )?;

    create_client(endpoint, client_id, any_client_state, any_consensus_state).await
}

pub async fn query_client_state(
    endpoint: String,
    client_id: String,
) -> Result<ibc_client::QueryClientStateResponse> {
    let mut client = connect_query(endpoint).await?;
    let response = client
        .client_state(ibc_client::QueryClientStateRequest { client_id })
        .await?;
    Ok(response.into_inner())
}

pub async fn query_consensus_state(
    endpoint: String,
    client_id: String,
    version_number: u64,
    version_height: u64,
    latest_height: bool,
) -> Result<ibc_client::QueryConsensusStateResponse> {
    let mut client = connect_query(endpoint).await?;
    let response = client
        .consensus_state(ibc_client::QueryConsensusStateRequest {
            client_id,
            version_number,
            version_height,
            latest_height,
        })
        .await?;
    Ok(response.into_inner())
}

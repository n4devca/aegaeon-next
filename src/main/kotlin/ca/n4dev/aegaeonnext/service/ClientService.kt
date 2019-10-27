package ca.n4dev.aegaeonnext.service

import ca.n4dev.aegaeonnext.model.entities.Client
import ca.n4dev.aegaeonnext.model.entities.ClientFlow
import ca.n4dev.aegaeonnext.model.entities.ClientScope
import ca.n4dev.aegaeonnext.model.repositories.ClientRepository
import ca.n4dev.aegaeonnext.service.dto.ClientDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.UnsupportedOperationException

/**
 *
 * ClientService.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Oct 22 - 2019
 *
 */


fun clientToClientDto(client: Client, clientScopes: List<ClientScope>, clientFlows: List<ClientFlow>) : ClientDto {

    val scopeMap = clientScopes.associate { Pair(it.scopeCode, it.id) }
    val flowMap = clientFlows.associate { Pair(it.flow, it.id) }

    return ClientDto(client.id, client.publicId, client.secret, client.name, client.logoUrl, scopeMap, flowMap);
}

@Service
class ClientService(val clientRepository: ClientRepository) {

    @Transactional(readOnly = true)
    fun getById(id: Long) : ClientDto? {
        return clientRepository.getClientById(id)?.let { loadClientInfo(it) }
    }

    @Transactional(readOnly = true)
    fun getByPublicId(publicId: String) : ClientDto? {
        return clientRepository.getClientByPublicId(publicId)?.let { loadClientInfo(it) }
    }

    @Transactional
    fun create(clientDto: ClientDto) : ClientDto {
        throw UnsupportedOperationException("Not implemented yet")
    }

    @Transactional
    fun update(id: Long, clientDto: ClientDto) : ClientDto {
        throw UnsupportedOperationException("Not implemented yet")
    }

    private fun loadClientInfo(client: Client) : ClientDto? {

        var clientScopes = emptyList<ClientScope>();
        var clientFlows = emptyList<ClientFlow>();

        client.id?.let {
            clientScopes = clientRepository.getClientScopesByClientId(it)
            clientFlows = clientRepository.getClientFlowByClientId(it)
        }

        return clientToClientDto(client, clientScopes, clientFlows)
    }
}
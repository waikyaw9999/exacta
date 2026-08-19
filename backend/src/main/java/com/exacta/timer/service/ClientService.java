package com.exacta.timer.service;

import com.exacta.timer.dto.client.ClientRequest;
import com.exacta.timer.dto.client.ClientResponse;
import com.exacta.timer.entity.Client;
import com.exacta.timer.exception.ConflictException;
import com.exacta.timer.exception.ResourceNotFoundException;
import com.exacta.timer.repository.ClientRepository;
import com.exacta.timer.repository.ProjectRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientService {

    private final ClientRepository clientRepository;
    private final ProjectRepository projectRepository;

    public List<ClientResponse> findAll() {
        return clientRepository.findAllByOrderByNameAsc().stream().map(this::toResponse).toList();
    }

    public ClientResponse findById(Long id) {
        return toResponse(getById(id));
    }

    @Transactional
    public ClientResponse create(ClientRequest request) {
        Client saved = clientRepository.save(toEntity(request));
        log.info("Created client {}", saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public ClientResponse update(Long id, ClientRequest request) {
        Client client = getById(id);
        client.setName(request.name().trim());
        client.setContactEmail(normalizeOptionalEmail(request.contactEmail()));
        client.setCompany(request.company().trim());
        return toResponse(client);
    }

    @Transactional
    public void delete(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client not found: " + id);
        }
        if (projectRepository.existsByClientId(id)) {
            throw new ConflictException("Cannot delete a client who has projects");
        }
        clientRepository.deleteById(id);
        log.info("Deleted client {}", id);
    }

    public Client getById(Long id) {
        return clientRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found: " + id));
    }

    private Client toEntity(ClientRequest request) {
        return Client.builder()
                .name(request.name().trim())
                .contactEmail(normalizeOptionalEmail(request.contactEmail()))
                .company(request.company().trim())
                .build();
    }

    private ClientResponse toResponse(Client client) {
        return new ClientResponse(
                client.getId(), client.getName(), client.getContactEmail(), client.getCompany());
    }

    private String normalizeOptionalEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return email.trim().toLowerCase();
    }
}

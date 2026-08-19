package com.exacta.timer.service;

import com.exacta.timer.dto.project.ProjectRequest;
import com.exacta.timer.dto.project.ProjectResponse;
import com.exacta.timer.entity.Client;
import com.exacta.timer.entity.Project;
import com.exacta.timer.entity.ProjectStatus;
import com.exacta.timer.exception.ConflictException;
import com.exacta.timer.exception.ResourceNotFoundException;
import com.exacta.timer.repository.ProjectRepository;
import com.exacta.timer.repository.TimeEntryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TimeEntryRepository timeEntryRepository;
    private final ClientService clientService;

    public List<ProjectResponse> findAll(Long clientId, ProjectStatus status) {
        return projectRepository.search(clientId, status).stream().map(this::toResponse).toList();
    }

    public ProjectResponse findById(Long id) {
        return toResponse(getByIdWithClient(id));
    }

    @Transactional
    public ProjectResponse create(ProjectRequest request) {
        Client client = clientService.getById(request.clientId());
        Project saved = projectRepository.save(Project.builder()
                .name(request.name().trim())
                .client(client)
                .status(request.status())
                .build());
        log.info("Created project {} for client {}", saved.getId(), client.getId());
        return toResponse(saved);
    }

    @Transactional
    public ProjectResponse update(Long id, ProjectRequest request) {
        Project project = getByIdWithClient(id);
        project.setName(request.name().trim());
        project.setStatus(request.status());
        if (!project.getClient().getId().equals(request.clientId())) {
            project.setClient(clientService.getById(request.clientId()));
        }
        return toResponse(project);
    }

    @Transactional
    public void delete(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project not found: " + id);
        }
        if (timeEntryRepository.existsByProjectId(id)) {
            throw new ConflictException("Cannot delete a project that has time entries");
        }
        projectRepository.deleteById(id);
        log.info("Deleted project {}", id);
    }

    public Project getByIdWithClient(Long id) {
        return projectRepository
                .findByIdWithClient(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));
    }

    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getClient().getId(),
                project.getClient().getName(),
                project.getStatus());
    }
}

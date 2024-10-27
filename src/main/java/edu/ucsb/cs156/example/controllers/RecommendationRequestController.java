package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.RecommendationRequest;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.RecommendationRequestRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.time.LocalDateTime;

@Tag(name = "RecommendationRequest")
@RequestMapping("/api/recommendationrequest")
@RestController
@Slf4j
public class RecommendationRequestController extends ApiController {
    
    @Autowired
    RecommendationRequestRepository recommendationRequestRepository;

    // GET ALL 
    @Operation(summary = "List all recommendation requests")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<RecommendationRequest> allRecommendationRequests() {
        return recommendationRequestRepository.findAll();
    }

    // POST (Create a new recommendation request)
    @Operation(summary = "Create a new recommendation request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public RecommendationRequest postRecommendationRequest(
            @Parameter(name = "requesterEmail") @RequestParam String requesterEmail,
            @Parameter(name = "professorEmail") @RequestParam String professorEmail,
            @Parameter(name = "explanation") @RequestParam String explanation,
            @Parameter(name = "dateRequested", description = "Date when recommendation was requested (ISO format)") 
                @RequestParam("dateRequested") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateRequested,
            @Parameter(name = "dateNeeded", description = "Date when recommendation is needed (ISO format)") 
                @RequestParam("dateNeeded") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateNeeded,
            @Parameter(name = "done") @RequestParam Boolean done) {

        log.info("Creating Recommendation Request for requesterEmail={}, professorEmail={}", requesterEmail, professorEmail);

        RecommendationRequest recommendationRequest = RecommendationRequest.builder()
                .requesterEmail(requesterEmail)
                .professorEmail(professorEmail)
                .explanation(explanation)
                .dateRequested(dateRequested)
                .dateNeeded(dateNeeded)
                .done(done)
                .build();

        return recommendationRequestRepository.save(recommendationRequest);
    }

    // GET a recommendation request by ID
    @Operation(summary = "Get a recommendation request by ID")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public RecommendationRequest getById(
            @Parameter(name = "id", description = "ID of the recommendation request") @RequestParam Long id) {

        return recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));
    }

    // UPDATE (Modify an existing recommendation request)
    @Operation(summary = "Update an existing recommendation request by ID")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public RecommendationRequest updateRecommendationRequest(
            @Parameter(name = "id", description = "ID of the recommendation request to update") @RequestParam Long id,
            @RequestBody @Valid RecommendationRequest incoming) {

        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        recommendationRequest.setRequesterEmail(incoming.getRequesterEmail());
        recommendationRequest.setProfessorEmail(incoming.getProfessorEmail());
        recommendationRequest.setExplanation(incoming.getExplanation());
        recommendationRequest.setDateRequested(incoming.getDateRequested());
        recommendationRequest.setDateNeeded(incoming.getDateNeeded());
        recommendationRequest.setDone(incoming.getDone());

        return recommendationRequestRepository.save(recommendationRequest);
    }

    // DELETE a recommendation request by ID
    @Operation(summary = "Delete a recommendation request by ID")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteRecommendationRequest(
            @Parameter(name = "id", description = "ID of the recommendation request to delete") @RequestParam Long id) {

        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        recommendationRequestRepository.delete(recommendationRequest);
        return genericMessage("Recommendation Request with id %s deleted".formatted(id));
    }
}

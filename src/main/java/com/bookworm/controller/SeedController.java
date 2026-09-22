package com.bookworm.controller;

import com.bookworm.service.SeedService;
import com.bookworm.service.SeedService.SeedResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin-only endpoint to re-execute the sample-data.sql seed script
 * against the active H2 datasource.
 *
 * <p>The seed script uses MERGE semantics — it is safe to call multiple times.
 * On startup the same script is already applied automatically via
 * {@code spring.sql.init.data-locations}; this endpoint allows re-seeding
 * without restarting the application.
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin")
public class SeedController {

    private final SeedService seedService;

    @PostMapping("/seed")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary     = "Re-execute the sample-data.sql seed script (admin only)",
        description = "Reads sample-data.sql from the classpath and runs every statement "
                    + "via JDBC. Safe to call repeatedly — uses MERGE semantics."
    )
    public SeedResult seed() {
        return seedService.execute();
    }
}

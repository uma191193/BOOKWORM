package com.bookworm.service;

public interface SeedService {

    /**
     * Executes every statement in {@code sample-data.sql} against the active
     * datasource.  Uses {@code MERGE} semantics so it is safe to call multiple
     * times — existing rows are updated rather than duplicated.
     *
     * @return a human-readable summary of what was executed
     */
    SeedResult execute();

    record SeedResult(int statementsExecuted, String message) {}
}

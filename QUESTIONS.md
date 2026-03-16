# Questions

Here are 2 questions related to the codebase. There's no right or wrong answer - we want to understand your reasoning.

## Question 1: API Specification Approaches

When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded everything directly. 

What are your thoughts on the pros and cons of each approach? Which would you choose and why?

**Answer:**
```txt
Pros of API-first (OpenAPI YAML generated code):
- Single source of truth: Guarantees that the code implementation matches the documented contract.
- Promotes parallel development: Client/Frontend teams can start building against the specification immediately.
- Easier API governance: API design reviews can happen before a single line of code is written.

Cons of API-first:
- Tooling overhead: Requires maintaining build plugins and managing code-generation mappings.
- Less flexibility: Harder to apply framework-specific annotations directly to generated interfaces/DTOs.

Pros of Code-first (Direct coding):
- Faster initial development: No overhead of writing YAML and grappling with code generators.
- Full access to framework features: Easy to fully leverage Quarkus/JAX-RS annotations.

Cons of Code-first:
- Documentation drift: API documentation relies on developers keeping annotations updated; it's easy for the code and docs to fall out of sync.

Choice:
I would choose the API-first (OpenAPI generation) approach for production systems. It ensures strict adherence to the API contract and provides reliable documentation for consumers. While the initial setup takes more time, it pays off significantly at scale by preventing breaking changes and allowing independent teams to integrate reliably.
```

---

## Question 2: Testing Strategy

Given the need to balance thorough testing with time and resource constraints, how would you prioritize tests for this project? 

Which types of tests (unit, integration, parameterized, etc.) would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
```txt
Prioritization Strategy:
1. Unit Tests (Highest Priority): I would focus heavily on the Domain layer (Use Cases and Entities). These tests are incredibly fast, deterministic, and validate the core business rules (e.g., capacity limits, validations). I would use Mockito to mock adapters.
2. Integration Tests (High Priority): Crucial for testing the Adapters layer. I would prioritize integration tests for:
   - Repository layer: Verifying complex queries, database constraints, and optimistic locking behavior (using Testcontainers for parity with production DB).
   - Event processing/Transactions: Ensuring that external system synchronization respects transactional boundaries (like the Legacy system sync).
3. API / E2E Tests (Medium Priority): A few critical-path tests using RestAssured to verify that endpoint mapping, JSON serialization, and ExceptionMappers are correctly wired.

Effective coverage over time:
- Parameterized Tests: I would extensively use JUnit Parameterized tests in the domain layer to test various edge cases (e.g., testing multiple combinations of warehouse capacity overlaps) without bloating the test suite with duplicated code.
- Concurrency Tests: Keep targeted concurrency tests for any transactional critical paths (optimistic lock validations), but minimize them elsewhere to reduce flakiness and long build times.
- CI/CD Enforcement: Integrate tools like JaCoCo in the pipeline, enforcing strict branch coverage rules specifically on the `domain` packages.
```


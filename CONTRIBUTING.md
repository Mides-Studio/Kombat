# Contributing to Kombat

Thanks for helping improve Kombat. Keep changes focused, modular, and easy to
review.

## Before you start

- Search existing issues before opening a duplicate.
- Open an issue first for large features or public API changes.
- Do not include generated build output, server data, or third-party binaries.
- Keep platform-specific code out of `api` and `core`.

## Development

The full build requires JDK 25. The Gradle configuration emits Java 21 bytecode
for the API, core, Bukkit, and Folia modules.

```bash
./gradlew clean build
```

Add tests for behavioral changes. A pull request should pass the complete build
and `git diff --check`.

## Commit messages

Use Conventional Commits:

```text
feat(scope): add a user-facing capability
fix(scope): correct broken behavior
docs: improve project documentation
test(scope): cover existing behavior
refactor(scope): restructure without behavior changes
ci: change automation
chore: perform repository maintenance
```

Use an imperative, lowercase subject and keep each commit limited to one logical
change.

## Pull requests

Describe the problem, the solution, the affected platforms, and how the change
was verified. By submitting a contribution, you confirm that you have the right
to provide it and permit the copyright holder to incorporate it into Kombat
under the repository's license.

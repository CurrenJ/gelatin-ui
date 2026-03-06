---
name: publish-maven-central
description: This skill should be used when the user asks to "publish to Maven Central", "upload to Maven Central", "release to Maven Central", "create Maven bundle", "upload bundle to Sonatype", "publish the mod", or wants to build and publish a new version of gelatin-ui to central.sonatype.com.
version: 1.0.0
---

# Publish to Maven Central

Publishes gelatin-ui to Maven Central via the Sonatype Central Portal.

## Overview

The build system uses Gradle tasks defined in `build.gradle` to:
1. Clean the local staging repo
2. Build and sign all subproject artifacts (common, fabric, neoforge)
3. Bundle them into a zip for upload
4. Upload the bundle to central.sonatype.com

## Credentials

Credentials are resolved in this order (first found wins):

| Priority | Source | Keys |
|----------|--------|------|
| 1 | Env var | `CENTRAL_TOKEN` (bearer token) |
| 2 | Env vars | `CENTRAL_USERNAME` + `CENTRAL_PASSWORD` |
| 3 | `local.properties` | `centralUsername` + `centralPassword` |
| 4 | `local.properties` | `sonartypeUsername` + `sonartypePassword` |

If none are present, the upload task will fail with a clear error. Ask the user to check `local.properties` or set env vars before proceeding.

## Publishing Type

Controls whether the release goes live automatically or waits for manual review:

- `USER_MANAGED` (default) — bundle is staged at central.sonatype.com; user must click "Publish" in the UI
- `AUTOMATIC` — bundle is published immediately without manual review

Set via:
- Env var: `CENTRAL_PUBLISHING_TYPE=AUTOMATIC`
- `local.properties`: `centralPublishingType=AUTOMATIC`
- Or ask the user which mode they want before running

## Gradle Tasks

| Task | Purpose |
|------|---------|
| `cleanCentralLocalRepo` | Deletes `build/central-repo` and `build/central-bundles` |
| `publishAllToCentralLocal` | Builds, signs, and publishes all subprojects to the local staging repo |
| `verifyCentralRepoCurrentVersion` | Fails if the staging repo contains stale versions |
| `createCentralBundle` | Zips `build/central-repo` into `build/central-bundles/<name>-<version>.zip` |
| `uploadToCentralPortal` | Uploads the bundle zip to central.sonatype.com |

Running `uploadToCentralPortal` triggers all prior tasks automatically via `dependsOn`.

## Workflow

### Standard release (USER_MANAGED — requires manual publish in Sonatype UI)

```bash
./gradlew uploadToCentralPortal
```

### Fully automatic release

```bash
CENTRAL_PUBLISHING_TYPE=AUTOMATIC ./gradlew uploadToCentralPortal
```

### Step-by-step (for debugging)

```bash
./gradlew cleanCentralLocalRepo
./gradlew publishAllToCentralLocal
./gradlew createCentralBundle
./gradlew uploadToCentralPortal
```

## GPG Signing

Artifacts are signed using `gpg` via `useGpgCmd()`. The executable is configured in `gradle.properties`:

```
signing.gnupg.executable=/opt/homebrew/bin/gpg
```

If signing fails, check that the GPG agent is running and the key is available:
```bash
gpg --list-secret-keys
```

## Before Publishing

1. Confirm `mod_version` in `gradle.properties` is correct and not already published
2. Ensure credentials are configured in `local.properties` or as env vars
3. Confirm GPG key is available (`gpg --list-secret-keys`)
4. Decide on `USER_MANAGED` vs `AUTOMATIC` publishing type

## After Publishing (USER_MANAGED)

Visit https://central.sonatype.com/publishing to review and manually publish the staged bundle.

## Troubleshooting

**"No Central credentials found"** — Set `centralUsername`/`centralPassword` (or `sonartypeUsername`/`sonartypePassword`) in `local.properties`, or export `CENTRAL_TOKEN` as an env var.

**"Aggregated repo includes versions other than X"** — Run `./gradlew cleanCentralLocalRepo` then retry.

**Signing error** — Run `gpg --list-secret-keys` to verify the key exists; restart `gpg-agent` if needed.

**Upload curl error** — Check network connectivity and that credentials are valid at central.sonatype.com.

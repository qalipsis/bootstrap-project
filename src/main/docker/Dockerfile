# Pin to a specific tag to keep image builds reproducible.
# Bump this when you upgrade the QALIPSIS version in build.gradle.kts / gradle.properties.
ARG QALIPSIS_BASE_TAG=snapshot-alpine
FROM ghcr.io/qalipsis/base:${QALIPSIS_BASE_TAG}

ADD lib /app/libs
RUN chmod -R 644 /app/libs

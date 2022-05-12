FROM registry.access.redhat.com/ubi8/ubi:8.6 as builder
RUN curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- -y
ENV PATH "/root/.cargo/bin:$PATH"
RUN rustup target add wasm32-unknown-unknown
RUN dnf install gcc -y
WORKDIR /usr/src/filter
COPY src ./src
COPY Cargo.toml Cargo.lock ./
RUN cargo build --target wasm32-unknown-unknown

FROM envoyproxy/envoy:v1.21-latest
COPY config /config
COPY --from=builder /usr/src/filter/target /target

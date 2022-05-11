Requirements
------------

A Rust installation capable of building for the `wasm32-unknown-unknown` target. Using `rustup`

```shell
rustup target add wasm32-unknown-unknown
```

Build instructions
------------------

```shell
cargo build --target wasm32-unknown-unknown
```

Running with Envoy
------------------

Ensure you have an Envoy installation which supports WASM plugins

```shell
envoy -c config/envoy-filter.yaml
```

This will create an Envoy proxy listening on `0.0.0.0:8082` and forwarding requests to `localhost:8080`


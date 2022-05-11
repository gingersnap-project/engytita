use log::*;
use proxy_wasm::traits::*;
use proxy_wasm::types::*;
use stretto::Cache;

proxy_wasm::main! {{
    proxy_wasm::set_log_level(LogLevel::Trace);
    proxy_wasm::set_root_context(|_| -> Box<dyn RootContext> { 
        Box::new(EngytitaRoot {}) 
    });
}}

struct EngytitaRoot {}

impl Context for EngytitaRoot {}

impl RootContext for EngytitaRoot {
    fn on_configure(&mut self, _: usize) -> bool {
        if let Some(config_bytes) = self.get_plugin_configuration() {
            // TODO: handle configuration
        }
        true
    }

    fn get_type(&self) -> Option<ContextType> {
        Some(ContextType::HttpContext)
    }

    fn create_http_context(&self, _: u32) -> Option<Box<dyn HttpContext>> {
        Some(Box::new(EngytitaHttp {
            //cache: HashMap::new(),
            cache: Cache::new(12960, 1e6 as i64).unwrap()
        }))
    }
}

struct EngytitaHttp {
    cache: Cache<String, String>
}

impl Context for EngytitaHttp {}

impl HttpContext for EngytitaHttp {
    fn on_http_request_headers(&mut self, _: usize, _: bool) -> Action {
        // Print the headers
        /*for (name, value) in &self.get_http_request_headers() {
            info!("{}: {}", name, value);
        }*/
        info!("Cache size {}", self.cache.len());
        match self.get_http_request_header(":path") {
            Some(path) => {
                match path.strip_prefix("/airports/") {
                    Some(airport) => {
                        info!("Request {}", airport);
                        let airport = airport.to_owned();
                        match self.cache.get(&airport) {
                            Some(cached) => {
                                info!("Cached {}", airport);
                                // We have cached it
                                self.send_http_response(
                                    200,
                                    vec![("X-Powered-By", "Engytita")],
                                    Some(b"Cached!\n"),
                                );
                                Action::Pause
                            }
                            _ => {
                                // Not cached yet
                                info!("Caching {}", airport);
                                self.cache.insert(airport, "Cached!\n".to_owned(),1);
                                Action::Continue
                            }
                        }
                        
                    }
                    _ => Action::Continue
                }
            }
            _ => Action::Continue,
        }
    }

    fn on_http_response_body(&mut self, body_size: usize, end_of_stream: bool) -> Action {
        if !end_of_stream {
            return Action::Pause;
        }
        if let Some(body_bytes) = self.get_http_response_body(0, body_size) {
            let body_str = String::from_utf8(body_bytes).unwrap();
            info!("Body: {}", body_str);
        }
        return Action::Continue;
    }

    fn on_http_response_headers(&mut self, _: usize, _: bool) -> Action {
        for (name, value) in &self.get_http_response_headers() {
            info!("{}: {}", name, value);
        }
        match self.get_http_response_header(":path") {
            Some(path) => {
                match path.strip_prefix("/airports/") {
                    Some(airport) => {
                        info!("Response {}", airport);
                        Action::Continue
                    }
                    _ => Action::Continue
                }
            }
            _ => Action::Continue,
        }
    }
}

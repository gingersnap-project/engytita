use log::*;
use proxy_wasm::traits::*;
use proxy_wasm::types::*;
use std::collections::HashMap;
use std::sync::{Mutex, Arc, RwLock};

proxy_wasm::main! {{
    proxy_wasm::set_log_level(LogLevel::Trace);
    proxy_wasm::set_root_context(|_| -> Box<dyn RootContext> { 
        Box::new(EngytitaRoot {
            cache: Arc::new(RwLock::new(HashMap::new()))
        }) 
    });
}}

struct HttpResponse {
    headers: Vec<(String, String)>,
    body: String
}

struct EngytitaRoot {
    cache: Arc<RwLock<HashMap<String, HttpResponse>>>
}

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
            cache: Arc::clone(&self.cache),
            key: None,
            headers: None
        }))
    }
}

struct EngytitaHttp {
    cache: Arc<RwLock<HashMap<String, HttpResponse>>>,
    key: Option<String>,
    headers: Option<Vec<(String, String)>>
}

impl Context for EngytitaHttp {}

impl HttpContext for EngytitaHttp {
    fn on_http_request_headers(&mut self, _: usize, _: bool) -> Action {
        match self.get_http_request_header(":path") {
            Some(path) => {
                match path.strip_prefix("/airports/") {
                    Some(airport) => {
                        info!("Request {}", airport);
                        let cache = self.cache.read().expect("RwLock poisoned");
                        if let Some(cached) = cache.get(airport) {
                            info!("Using cached {}", airport);
                            Action::Continue
                        } else {
                            drop(cache);
                            self.key.replace(airport.to_owned());
                            Action::Continue
                        }
                        
                    }
                    _ => Action::Continue
                }
            }
            _ => Action::Continue,
        }
    }

    fn on_http_response_headers(&mut self, _: usize, _: bool) -> Action {
        match self.key.as_ref() {
            Some(key) => {
                // The key matched, let's save the headers
                self.headers.replace(self.get_http_response_headers().clone());
                Action::Continue
            }
            _ => Action::Continue
        }    
    }

    fn on_http_response_body(&mut self, body_size: usize, end_of_stream: bool) -> Action {
        match self.key.as_ref() {
            Some(key) => {
                if !end_of_stream {
                    // We need more data!
                    return Action::Pause;
                }
                if let Some(body_bytes) = self.get_http_response_body(0, body_size) {
                    let body_str = String::from_utf8(body_bytes).unwrap();
                    info!("{} Body: {}", key, body_str);
                    // TODO: store headers and body in the cache
                }
                Action::Continue
            }
            _ => {
                // The key in this request didn't match our cache rule, just let it through as-is
                info!("Passthrough");
                Action::Continue
            }

        }
    }
}

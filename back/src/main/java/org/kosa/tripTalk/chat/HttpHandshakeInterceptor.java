package org.kosa.tripTalk.chat; // 또는 적절한 패키지명

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class HttpHandshakeInterceptor implements HandshakeInterceptor {
  
  public HttpHandshakeInterceptor() {
    System.out.println("🚀 HttpHandshakeInterceptor 인스턴스 생성됨");
  }
  
  @Override
  public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
      WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
    System.out.println(">>> Handshake Interceptor beforeHandshake called");
    
    if (request instanceof ServletServerHttpRequest) {
      String query = ((ServletServerHttpRequest) request).getServletRequest().getQueryString();
      if (query != null && query.contains("token=")) {
          String token = null;
          for (String param : query.split("&")) {
              if (param.startsWith("token=")) {
                  token = param.substring("token=".length());
                  break;
              }
          }
          if (token != null) {
              attributes.put("token", token);  // 세션에 토큰 저장
          }
      }
  }
  return true;
  }

  @Override
  public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
      WebSocketHandler wsHandler, Exception exception) {
    System.out.println(">>> Handshake Interceptor afterHandshake called");
    
  }
}
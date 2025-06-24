package org.kosa.tripTalk;

import org.kosa.tripTalk.jwt.JwtUtil;
import org.kosa.tripTalk.product.ProductService;
import org.kosa.tripTalk.user.UserService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class JwtTestConfig {
	
	@Bean
	JwtUtil jwtUtil() {
		return Mockito.mock(JwtUtil.class);
	}
	
	@Bean
    UserService userService() {
        return Mockito.mock(UserService.class);
    }
	
	@Bean
    ProductService productService() {
        return Mockito.mock(ProductService.class);
    }
}

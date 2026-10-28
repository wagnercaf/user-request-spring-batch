package com.devsuperior.user_request_sb.writer;

import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.devsuperior.user_request_sb.DTO.UserDTO;

@Configuration
public class InsertUserDataDBWriterConfig {
	@Bean
	public ItemWriter<UserDTO> insertUserDataDBWriter(){
		return users -> users.forEach(System.out::println);
	}
}

package com.devsuperior.user_request_sb.reader;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.devsuperior.user_request_sb.DTO.UserDTO;
import com.devsuperior.user_request_sb.domain.ResponseUser;

@Configuration
public class FetchUserDataReaderConfig implements ItemReader<UserDTO> {
	
	public static Logger logger = LoggerFactory.getLogger(FetchUserDataReaderConfig.class);

	private final String BASE_URL = "http://localhost:8081";
	private RestTemplate restTemplate = new RestTemplate();

	private int page = 0;

	private List<UserDTO> users = new ArrayList<>();
	private int userIndex = 0;
	
	@Value("$chunkSize")
	private int chunkSize;
	
	@Value("$pageSize")
	private int pageSize;
				
	@Override
	public @Nullable UserDTO read() throws Exception {
		UserDTO user;
		if (userIndex < users.size())
			user = users.get(userIndex);
		else
			user = null;
		
		userIndex++;
		
		return  user;
	}

	private List<UserDTO> fechUserDataFromAPI() {
		
		String uri = BASE_URL + "/clients/pagedData?page=%d&size=%d";
		
		logger.info("[READE STEP] Fetching data ...");
		logger.info("[READE STEP] Request uri: " + String.format(uri,getPage(), pageSize));
		
		ResponseEntity<ResponseUser> response = restTemplate.exchange(String.format(uri, getPage(), pageSize), HttpMethod.GET, null,
				new ParameterizedTypeReference<ResponseUser>() {
				});

		List<UserDTO> result = response.getBody().getContent();
		return result;
	}

	public int getPage() {
		return page;
	}
	
	public void incrementPage() {
		this.page++;
	}
	
	@BeforeChunk
	public void beforeChunk(ChunkContext context) {
		for (int i= 0; i < chunkSize;i += pageSize) {
			users.addAll(fechUserDataFromAPI());
		}
	}
	
	@AfterChunk
	public void afterChunck(ChunkContext context) {
		logger.info("Final chunck");
		incrementPage();
		userIndex = 0;
		users= new ArrayList<>();
	}
}

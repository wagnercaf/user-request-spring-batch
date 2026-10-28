package com.devsuperior.user_request_sb.step;

import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.devsuperior.user_request_sb.DTO.UserDTO;

@Configuration
public class fetchUserDataAndStoreDBStepConfig {
	
	//private PlatformTransactionManager transactionManager;
	
	@Value("${chunkSize}")
	private int chunkSize;
	
	@Bean
	public Step fetchUserDataAndStoreDBStep(
			ItemReader<UserDTO> fetchUserDataReader ,
			ItemWriter<UserDTO> insertUserDataDBWriter, 
			JobRepository jobRepository,
			PlatformTransactionManager transactionManager) {
		
		return new StepBuilder("fetchUserDataAndStoreDBStep", jobRepository)
				.<UserDTO, UserDTO>chunk(chunkSize, transactionManager)
				.reader(fetchUserDataReader)
				.writer(insertUserDataDBWriter)
				.build();
	}

}

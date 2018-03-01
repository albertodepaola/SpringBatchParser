package com.albertodepaola.fileconsumersb.batch;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.file.transform.PatternMatchingCompositeLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.albertodepaola.fileconsumersb.model.ClientLine;
import com.albertodepaola.fileconsumersb.model.LogLine;
import com.albertodepaola.fileconsumersb.model.LogLineItemProcessor;
import com.albertodepaola.fileconsumersb.model.SalesLine;
import com.albertodepaola.fileconsumersb.model.SellerLine;
import com.albertodepaola.fileconsumersb.model.SellerLineItemProcessor;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	public DataSource dataSource;

	// tag::readerwriterprocessor[]
	@Bean
	public FlatFileItemReader<LogLine> reader() {
		FlatFileItemReader<LogLine> reader = new FlatFileItemReader<>();
		
		reader.setResource(new ClassPathResource("sales.dat"));
		PatternMatchingCompositeLineMapper pmclm = new PatternMatchingCompositeLineMapper<>();
		Map<String, LineTokenizer> tokenizers = new HashMap<>();
		
		
		
		LineTokenizer lt = new PatternMatchingCompositeLineTokenizer() {
			{
				
			}
		};
		
		LineTokenizer sellerTokenizer = new DelimitedLineTokenizer("ç") {
			{
				setNames(new String[] {"type", "cpf", "name", "salary"});
			}
		};
		
		LineTokenizer clientTokenizer = new DelimitedLineTokenizer("ç") {
			{
				setNames(new String[] {"type", "cnpj", "name", "businessType"});
			}
		};
		
		LineTokenizer salesTokenizer = new DelimitedLineTokenizer("ç") {
			{
				setNames(new String[] {"type", "saleId", "saleItems", "saleSellerName"});
			}
		};
		
		
		tokenizers.put("001*", sellerTokenizer);
		tokenizers.put("002*", clientTokenizer);
		tokenizers.put("003*", salesTokenizer);
		
		pmclm.setTokenizers(tokenizers);
		
		Map<String, FieldSetMapper<? extends LogLine>> fieldSetMappers = new HashMap<>();
		
		FieldSetMapper<SellerLine> sellerFieldSetMapper = new BeanWrapperFieldSetMapper<SellerLine>() {
			{
				setTargetType(SellerLine.class);
			}
		};
		
		FieldSetMapper<ClientLine> clientFieldSetMapper = new BeanWrapperFieldSetMapper<ClientLine>() {
			{
				setTargetType(ClientLine.class);
			}
		};
		
		FieldSetMapper<SalesLine> saleFieldSetMapper = new BeanWrapperFieldSetMapper<SalesLine>() {
			{
				setTargetType(SalesLine.class);
			}
		};
		
		fieldSetMappers.put("001*", sellerFieldSetMapper);
		fieldSetMappers.put("002*", clientFieldSetMapper);
		fieldSetMappers.put("003*", saleFieldSetMapper);
		
		pmclm.setFieldSetMappers(fieldSetMappers);
		
		reader.setLineMapper(pmclm);
		
		
		/*
		reader.setLineMapper(new DefaultLineMapper<LogLine>() {
			{
				setLineTokenizer(new DelimitedLineTokenizer("ç") {
					{
						setNames(new String[] { "firstName", "lastName" });
					}
				});
				setFieldSetMapper(new BeanWrapperFieldSetMapper<LogLine>() {
					{
						setTargetType(LogLine.class);
					}
				});
			}
		});
		*/
		return reader;
	}

	@Bean
	public LogLineItemProcessor processor() {
		return new LogLineItemProcessor();
	}
	
	@Bean
	public SellerLineItemProcessor sellerProcessor() {
		return new SellerLineItemProcessor();
	}

	@Bean
	public JdbcBatchItemWriter<LogLine> writer() {
		
		JdbcBatchItemWriter<LogLine> writer = new JdbcBatchItemWriter<LogLine>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<LogLine>());
		writer.setSql("INSERT INTO logline (type, sellerName, clientName, saleItems) VALUES (:type, :sellerName, :clientName, :saleItems)");
		writer.setDataSource(dataSource);
		return writer;
	}
	
	@Bean
	public JdbcBatchItemWriter<SellerLine> sellerWriter() {
		
		JdbcBatchItemWriter<SellerLine> writer = new JdbcBatchItemWriter<SellerLine>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<SellerLine>());
		writer.setSql("INSERT INTO logline (type) VALUES (:type)");
		writer.setDataSource(dataSource);
		return writer;
	}
	// end::readerwriterprocessor[]

	// tag::jobstep[]
	@Bean
	public Job importUserJob(JobCompletionNotificationListener listener) {
		return jobBuilderFactory.get("importUserJob").incrementer(new RunIdIncrementer())
				.listener(listener)
				.flow(step1())
				.end()
				.build();
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").<LogLine, LogLine>chunk(10)
				.reader(reader())
				.processor(processor())
				//.processor(sellerProcessor())
				//.writer(sellerWriter())
				.writer(writer())
				
				.build();
	}
	// end::jobstep[]

}

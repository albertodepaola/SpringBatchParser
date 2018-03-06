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
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.albertodepaola.fileconsumersb.model.LogLine;
import com.albertodepaola.fileconsumersb.model.LogLineItemProcessor;
import com.albertodepaola.fileconsumersb.model.SellerLineItemProcessor;


public class BatchConfiguration2 {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	public DataSource dataSource;

	
	protected static class SellerFieldSetMapper implements FieldSetMapper<LogLine> {
	    public LogLine mapFieldSet(FieldSet fieldSet) {
	    	LogLine sellerLine = new LogLine();

	        sellerLine.setType(fieldSet.readString(0));
	        sellerLine.setCpf(fieldSet.readString(1));
	        sellerLine.setSellerName(fieldSet.readString(2));
	        sellerLine.setSallary(fieldSet.readString(3));
	        
	        return sellerLine;
	    }
	}
	
	protected static class ClientFieldSetMapper implements FieldSetMapper<LogLine> {
		public LogLine mapFieldSet(FieldSet fieldSet) {
			LogLine sellerLine = new LogLine();
			
			sellerLine.setType(fieldSet.readString(0));
			sellerLine.setCnpj(fieldSet.readString(1));
			sellerLine.setClientName(fieldSet.readString(2));
			sellerLine.setBusinessType(fieldSet.readString(3));
			
			return sellerLine;
		}
	}
	
	protected static class SaleFieldSetMapper implements FieldSetMapper<LogLine> {
		public LogLine mapFieldSet(FieldSet fieldSet) {
			LogLine sellerLine = new LogLine();
			
			sellerLine.setType(fieldSet.readString(0));
			sellerLine.setSaleId(fieldSet.readString(1));
			sellerLine.setSaleItems(fieldSet.readString(2));
			sellerLine.setSellerName(fieldSet.readString(3));
			
			return sellerLine;
		}
	}
	
	// tag::readerwriterprocessor[]
	@Bean
	public FlatFileItemReader<? extends LogLine> reader() {
		FlatFileItemReader<LogLine> reader = new FlatFileItemReader<LogLine>();
		reader.setResource(new ClassPathResource("sales.dat"));
		PatternMatchingCompositeLineMapper<LogLine> pmclm = new PatternMatchingCompositeLineMapper<>();
		Map<String, LineTokenizer> tokenizers = new HashMap<>();
		
		
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
		
		Map<String, FieldSetMapper<LogLine>> fieldSetMappers = new HashMap<>();
				
		fieldSetMappers.put("001*", new SellerFieldSetMapper());
		fieldSetMappers.put("002*", new ClientFieldSetMapper());
		fieldSetMappers.put("003*", new SaleFieldSetMapper());
		
		pmclm.setFieldSetMappers(fieldSetMappers);
		
		reader.setLineMapper(pmclm);
		
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
	// end::readerwriterprocessor[]

	// tag::jobstep[]
	@Bean
	public Job importUserJob(JobCompletionNotificationListener listener) {
		return jobBuilderFactory.get("importUserJob").incrementer(new RunIdIncrementer())
//				.listener(listener)
				
				.flow(step1())
				.end()
				.build();
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").<LogLine, LogLine>chunk(10)
				.reader(reader())
				.processor(processor())
				.writer(writer())
				
				.build();
	}
	// end::jobstep[]

}

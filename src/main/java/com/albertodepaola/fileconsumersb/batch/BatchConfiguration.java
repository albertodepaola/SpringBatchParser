package com.albertodepaola.fileconsumersb.batch;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.classify.BackToBackPatternClassifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.RowMapper;

import com.albertodepaola.fileconsumersb.model.ClientLine;
import com.albertodepaola.fileconsumersb.model.LogLine;
import com.albertodepaola.fileconsumersb.model.LogLineClassifier;
import com.albertodepaola.fileconsumersb.model.LogLineItemProcessor;
import com.albertodepaola.fileconsumersb.model.SaleLine;
import com.albertodepaola.fileconsumersb.model.SellerLine;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	private static final Logger log = LoggerFactory.getLogger(BatchConfiguration.class);
	
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
		public SaleLine mapFieldSet(FieldSet fieldSet) {
			SaleLine sellerLine = new SaleLine();
			
			sellerLine.setType(fieldSet.readString(0));
			sellerLine.setSaleId(fieldSet.readString(1));
			sellerLine.setSaleItems(fieldSet.readString(2));
			sellerLine.setSellerName(fieldSet.readString(3));
			
			return sellerLine;
		}
	}
	
	@Configuration
	public static class Step1Config {
		
		// tag::readerwriterprocessor[]
		@Bean
		public FlatFileItemReader<LogLine> reader() {

			FlatFileItemReader<LogLine> reader = new FlatFileItemReader<>();

			reader.setResource(new ClassPathResource("sales.dat"));
			PatternMatchingCompositeLineMapper pmclm = new PatternMatchingCompositeLineMapper<>();
			Map<String, LineTokenizer> tokenizers = new HashMap<>();

			LineTokenizer sellerTokenizer = new DelimitedLineTokenizer("ç") {
				{
					setNames(new String[] { "type", "cpf", "name", "sallary" });
				}
			};

			LineTokenizer clientTokenizer = new DelimitedLineTokenizer("ç") {
				{
					setNames(new String[] { "type", "cnpj", "name", "businessType" });
				}
			};

			LineTokenizer salesTokenizer = new DelimitedLineTokenizer("ç") {
				{
					setNames(new String[] { "type", "saleId", "saleItems", "saleSellerName" });
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
		public JdbcBatchItemWriter<LogLine> jdbcWriter(DataSource dataSource) {

			JdbcBatchItemWriter<LogLine> writer = new JdbcBatchItemWriter<LogLine>();
			writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<LogLine>());
			writer.setSql(
					"INSERT INTO logline (type, sellerName, cpf, sallary, cnpj, clientName, businessType, saleId, saleItems, saleSellerName) VALUES (:type, :sellerName, :cpf, :sallary, :cnpj, :clientName, :businessType, :saleId, :saleItems, :saleSellerName)");

			writer.setDataSource(dataSource);
			return writer;
		}
		
		@Bean
		public ItemWriter<? super LogLine> compositeWriter(DataSource dataSource) {
			// BackToBackPatternClassifier<LogLine, ItemWriter<? extends LogLine>>
			// classifier = new BackToBackPatternClassifier<>();
			BackToBackPatternClassifier classifier = new BackToBackPatternClassifier<>();
			classifier.setRouterDelegate(new LogLineClassifier());
			classifier.setMatcherMap(new HashMap<String, ItemWriter<? extends LogLine>>() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				{
					put("001", insertJdbcSellerBatchItemWriter(null));
					put("002", insertJdbcClientBatchItemWriter(null));
					put("003", insertJdbcSaleBatchItemWriter(null));
				}
			});

			ClassifierCompositeItemWriter<? super LogLine> writer = new ClassifierCompositeItemWriter<>();
			writer.setClassifier(classifier);

			return writer;
		}

		@Bean
		public ItemWriter<? extends LogLine> insertJdbcSellerBatchItemWriter(DataSource dataSource) {
			JdbcBatchItemWriter<SellerLine> writer = new JdbcBatchItemWriter<SellerLine>();
			writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<SellerLine>());
			writer.setSql("INSERT INTO seller (type, sellerName, cpf, sallary) VALUES (:type, :sellerName, :cpf, :sallary)");
	
			writer.setDataSource(dataSource);
			return writer;
		}
		
		@Bean
		public ItemWriter<? extends LogLine> insertJdbcClientBatchItemWriter(DataSource dataSource) {
			JdbcBatchItemWriter<ClientLine> writer = new JdbcBatchItemWriter<ClientLine>();
			writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<ClientLine>());
			writer.setSql("INSERT INTO client (type, cnpj, clientName, businessType) VALUES (:type, :cnpj, :clientName, :businessType)");
		
			writer.setDataSource(dataSource);
			return writer;
		}
		
		@Bean
		public ItemWriter<? extends LogLine> insertJdbcSaleBatchItemWriter(DataSource dataSource) {
			SaleLineJdbcBatchItemWriter writer = new SaleLineJdbcBatchItemWriter();
//			JdbcBatchItemWriter<SellerLine> writer = new JdbcBatchItemWriter<SellerLine>();
			log.info("Sale writer");
			writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<SaleLine>());
			writer.setSql("INSERT INTO sale (sale_id, type, saleId, saleItems, saleSellerName, saleAmount) VALUES (:sale_id, :type, :saleId, :saleItems, :sellerName, :saleAmount)");
			
			writer.setDataSource(dataSource);
			return writer;
		}
	}
	
	@Configuration
	public static class Step2Config {
		
		@Bean
		ItemReader <Integer> jdbcReader(DataSource dataSource) {
			return new JdbcCursorItemReaderBuilder<Integer>()
					.dataSource(dataSource)
					.name("client-count-jdbc-reader")
					.sql("select count(*) as count from client")
					.rowMapper((rs, i) -> rs.getInt("count"))
					.build();
		}
		
		@Bean
		ItemWriter <Integer> fileWriter(@Value("${output}")Resource resource ) {
			
			return new FlatFileItemWriterBuilder<Integer>()
					.name("count-seller-writer")
					.resource(resource)
					.append(true)
					.lineAggregator(new DelimitedLineAggregator<Integer>() {
						{
							setDelimiter(",");
							setFieldExtractor(integerValue -> {
								return new Object[] {integerValue};
							});
						}
					})
					.build();
		}
	}
	
	@Configuration
	public static class Step3Config {
		
		@Bean
		ItemReader <Integer> clientCountJdbcReader(DataSource dataSource) {
			return new JdbcCursorItemReaderBuilder<Integer>()
					.dataSource(dataSource)
					.name("seller-count-jdbc-reader")
					.sql("select count(type) as count from seller")
					.rowMapper((rs, i) -> rs.getInt("count"))
					.build();
		}
		
		@Bean
		ItemWriter <Integer> clientCountFileWriter(@Value("${output}")Resource resource ) {
			
			return new FlatFileItemWriterBuilder<Integer>()
					.name("count-seller-writer")
					.resource(resource)
					.append(true)
					.lineAggregator(new DelimitedLineAggregator<Integer>() {
						{
							setDelimiter(",");
							setFieldExtractor(integerValue -> {
								return new Object[] {integerValue};
							});
						}
					})
					.build();
		}
	}
	
	@Configuration
	public static class Step4Config {
		
		@Bean
		ItemReader<ClientLine> clientJdbcReader(DataSource dataSource) {
			return new JdbcCursorItemReaderBuilder<ClientLine>().dataSource(dataSource).name("seller-count-jdbc-reader")
					.sql("select * from client")
					.rowMapper((rs, i) -> {
						ClientLine client = new ClientLine();
						client.setClientName(rs.getString("clientName"));
						client.setBusinessType(rs.getString("businessType"));
						client.setCnpj(rs.getString("cnpj"));
						client.setType(rs.getString("type"));
						return client;
					}).build();
		}
		
		@Bean
		ItemWriter <ClientLine> clientFileWriter(@Value("${output}")Resource resource ) {
			
			return new FlatFileItemWriterBuilder<ClientLine>()
					.name("count-seller-writer")
					.resource(resource)
					.append(true)
					.lineAggregator(new DelimitedLineAggregator<ClientLine>() {
						{
							setDelimiter(",");
							setFieldExtractor(client -> {
								return new Object[] {client.getType(), client.getBusinessType(), client.getClientName(), client.getCnpj()};
							});
						}
					})
					.build();
		}
	}
	@Configuration
	public static class Step5Config {
		
		@Bean
		ItemReader<SaleLine> saleJdbcReader(DataSource dataSource) {
			return new JdbcCursorItemReaderBuilder<SaleLine>().dataSource(dataSource).name("seller-count-jdbc-reader")
					.sql("select * from sale")
					.rowMapper((rs, i) -> {
						SaleLine sale = new SaleLine();
						sale.setType(rs.getString("type"));
						sale.setSellerName(rs.getString("saleSellerName"));
						sale.setSale_id(rs.getLong("sale_id"));
						sale.setSaleId(rs.getString("saleId"));
						sale.setSaleAmount(rs.getBigDecimal("saleAmount"));
						sale.setSaleItems(rs.getString("saleItems"));
						return sale;
					}).build();
		}
		
		@Bean
		ItemWriter <SaleLine> saleFileWriter(@Value("${output}")Resource resource ) {
			
			return new FlatFileItemWriterBuilder<SaleLine>()
					.name("count-seller-writer")
					.resource(resource)
					.append(true)
					.lineAggregator(new DelimitedLineAggregator<SaleLine>() {
						{
							setDelimiter(",");
							setFieldExtractor(sale -> {
								return new Object[] {sale.getType(), sale.getSale_id(), sale.getSaleId(), sale.getSellerName(), sale.getSaleAmount(), sale.getSaleItems()};
							});
						}
					})
					.build();
		}
	}
	// end::readerwriterprocessor[]

	// tag::jobstep[]
	@Bean
	public Job importUserJob(JobCompletionNotificationListener listener, 
			Step1Config step1Config,
			Step2Config step2Config,
			Step3Config step3Config) {
		return jobBuilderFactory.get("importUserJob").incrementer(new RunIdIncrementer())
				.listener(listener)
				.flow(step1(null))
				.next(step2(null))
				.next(step3(null))
				.next(step5(null))
				.end()
				.build();
	}

	@Bean
	public Step step1(Step1Config step1Config) {
		return stepBuilderFactory.get("step1").<LogLine, LogLine>chunk(10)
				.reader(step1Config.reader())
				.processor(step1Config.processor())
				//.processor(sellerProcessor())
				//.writer(sellerWriter())
				.writer(step1Config.compositeWriter(null))
				
				.build();
	}
	
	@Bean 
	public Step step2(Step2Config step2Config) {
		return stepBuilderFactory.get("step2").<Integer, Integer>chunk(100)
				.reader(step2Config.jdbcReader(null))
				.writer(step2Config.fileWriter(null))
				.build();
	}
	
	@Bean 
	public Step step3(Step3Config step3Config) {
		return stepBuilderFactory.get("step3").<Integer, Integer>chunk(100)
				.reader(step3Config.clientCountJdbcReader(null))
				.writer(step3Config.clientCountFileWriter(null))
				.build();
	}
	
	@Bean 
	public Step step5(Step5Config step5Config) {
		return stepBuilderFactory.get("step5").<SaleLine, SaleLine>chunk(100)
				.reader(step5Config.saleJdbcReader(null))
				.writer(step5Config.saleFileWriter(null))
				.build();
	}
	
	// end::jobstep[]

}

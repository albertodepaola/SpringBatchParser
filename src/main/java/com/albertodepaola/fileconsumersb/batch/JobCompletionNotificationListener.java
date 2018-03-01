package com.albertodepaola.fileconsumersb.batch;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.albertodepaola.fileconsumersb.model.LogLine;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

	private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("!!! JOB FINISHED! Time to verify the results");

			List<LogLine> results = jdbcTemplate.query("SELECT type, sellerName, clientName, saleItems FROM logline", new RowMapper<LogLine>() {
				@Override
				public LogLine mapRow(ResultSet rs, int row) throws SQLException {
					LogLine logLine = new LogLine(rs.getString(1));
					logLine.setSellerName(rs.getString("sellerName"));
					logLine.setClientName(rs.getString("clientName"));
					logLine.setSaleItems(rs.getString("saleItems"));
					return logLine;
				}
			});

			for (LogLine logLine : results) {
				log.info("Found <" + logLine + "> in the database.");
			}

		}
	}
}
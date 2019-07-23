package com.example.springbootmailservice;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@SpringBootApplication
@EnableScheduling
public class SpringbootMailServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootMailServiceApplication.class, args);
	}

}

@RestController
@Log4j2
class MailController {
	@Autowired
	UserDetailRepositery repo;

	@GetMapping("/")
	public String getStart() {
		return "Hello SpringBoot";
	}

	@GetMapping("/getById/{id}")
	public UserDetails getStart(@PathVariable Long id) {
		log.info("By Using Simple Query={}", repo.findByDateValue(id));
		return repo.findById(id).get();
	}

	// every one minute it will execute
	/*
	 * https://en.wikipedia.org/wiki/Cron for more info about cron
	 */
	@GetMapping("schedularMailtask")
	@Scheduled(cron = "0 * * * * ?")
	public void schedularMethod() {
		log.info("Schedular is runnign -> {}", LocalDate.now());
	}

}

@Component
class MailSendService {
	@Autowired
	JavaMailSender emailSender;

	public void sendMail(String to, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);
		emailSender.send(message);
	}

}

interface UserDetailRepositery extends JpaRepository<UserDetails, Long> {
	Optional<UserDetails> findById(Long id);

	@Query("select u from UserDetails u where u.date >='2019-07-20' and u.date<='2019-07-22'")
	UserDetails findByDateValue(Long emailAddress);
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
class UserDetails {
	@Id
	private Long id;
	private String emailId;
	private Date date;
}

package com.studywithmochi.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class StudywithmochiBackendApplication {

	public static void main(String[] args) {

		// Session times are stored without a timezone, so the whole app must use
		// one fixed zone. This also makes "today" / "this week" stats follow the
		// user's calendar day. Override with the APP_TIMEZONE env variable.
		String timezone = System.getenv().getOrDefault(
				"APP_TIMEZONE",
				"Asia/Kolkata"
		);

		TimeZone.setDefault(TimeZone.getTimeZone(timezone));

		SpringApplication.run(StudywithmochiBackendApplication.class, args);
	}

}

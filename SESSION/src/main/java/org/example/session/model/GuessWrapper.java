package org.example.session.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GuessWrapper {

	@NotBlank(message = "Guess can't be blank")
	String guess;
}

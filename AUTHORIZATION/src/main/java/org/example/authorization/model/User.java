package org.example.authorization.model;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class User {
    Long id;
    String login;
}

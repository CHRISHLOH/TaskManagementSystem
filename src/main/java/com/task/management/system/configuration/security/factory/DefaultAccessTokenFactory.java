package com.task.management.system.configuration.security.factory;

import com.task.management.system.configuration.security.Token;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;

@Setter
public class DefaultAccessTokenFactory implements Function<Token, Token> {

    private Duration tokenTtl = Duration.ofHours(5);

    @Override
    public Token apply(Token token) {
        var now = Instant.now();
        return new Token(token.id(), token.subject(),
                token.authorities().stream()
                        .filter(authority -> authority.startsWith("GRANT_"))
                        .map(authority -> authority.replace("GRANT_", ""))
                        .toList(), now, now.plus(this.tokenTtl));
    }

}

package com.github.shemhazai.mprw.utils;

import com.github.shemhazai.mprw.domain.Token;
import com.github.shemhazai.mprw.domain.User;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@Scope("singleton")
public class AuthenticationManager {

    private List<Token> authenticatedTokens;

    public AuthenticationManager() {
        authenticatedTokens = new ArrayList<>();
    }

    public Token createAndRegisterToken(User user) {
        Token token = generateToken(user);
        authenticatedTokens.add(token);
        return token;
    }

    public synchronized Token generateToken(User user) {
        Token token = new Token();
        token.setToken(generateTokenHash(user));
        token.setEmail(user.getEmail());
        token.setExpireDate(dateAfterOneHour());
        return token;
    }

    private Date dateAfterOneHour() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        return calendar.getTime();
    }

    private String generateTokenHash(User user) {
        String tokenString = buildTokenString(user);
        HashGenerator hasher = new HashGenerator();
        return hasher.hash(tokenString);
    }

    private String buildTokenString(User user) {
        StringBuilder builder = new StringBuilder();
        builder.append(user.getEmail());
        builder.append(new Date());
        return builder.toString();
    }

    public boolean isTokenRegistered(Token token) {
        return authenticatedTokens.contains(token);
    }

    private static final int FIFTEEN_MINUTES = 900000;

    @Scheduled(fixedDelay = FIFTEEN_MINUTES)
    public void deleteExpiredTokens() {
        List<Token> expiredTokens = new ArrayList<>();
        Date now = new Date();
        authenticatedTokens.forEach((token) -> {
            if (now.after(token.getExpireDate()))
                expiredTokens.add(token);
        });
        authenticatedTokens.removeAll(expiredTokens);
    }
}

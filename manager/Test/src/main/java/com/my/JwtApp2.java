package com.my;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class JwtApp2 {

    private static final String SECRET = "your-256-bit-secret";
    private static final String SECRET2 = "your-256-bit-secret2";
    private static final long EXPIRATION_TIME = 86400000; // 24 hours

    public static String createJWT(String subject) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);

        return JWT.create()
                .withClaim("name","张三")
                .withSubject(subject)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(algorithm);
    }

    public static void main(String[] args) {
        String jwt = createJWT("user123");

        System.out.println(jwt);

        DecodedJWT decodedJWT = verifyJWT(jwt);
        Claim name = decodedJWT.getClaim("name");
        String s = name.asString();
        System.out.println("Generated JWT: " + jwt);
    }

    public static DecodedJWT verifyJWT(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            DecodedJWT decode = JWT.decode(token);
            return decode;
//            JWTVerifier verifier = JWT.require(algorithm).build();
//            return verifier.verify(token);
        } catch (JWTVerificationException exception) {
            // Invalid signature/claims
            System.err.println("JWT verification failed: " + exception.getMessage());
            return null;
        }
    }
}

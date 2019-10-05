package co.edu.unisabana.config;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil implements Serializable {

	private static final long serialVersionUID = -2550185165626007488L;

	public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

	@Value("${jwt.secret}")
	private String secret;
	
	/**
	 * Retrieves the user from the session token an returns it
	 * @param token
	 * @return UserName from token
	 */
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}
	/**
	 * Returns the expiration date from the token
	 * @param token
	 * @return Date of expiration
	 */
	//retrieve expiration date from jwt token
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}
	/**
	 * Retrieves the claim inside the JWT token
	 * @param <T>
	 * @param token
	 * @param claimsResolver
	 * @return 
	 */
	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}
	/**
	 * Gets the secret key that was used to sing the token in order to extract the information inside it
	 * @param token
	 * @return
	 */
	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}
	/**
	 * Checks if the JWT token has already expired, in order to do that it retrieves the date and compares it
	 * to the current date and time.
	 * This method returns the boolean result of the compare operation
	 * @param token
	 * @return Boolean
	 */
	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}
	/**
	 * Generates a unique token based in the username, password, the claims and the secret key
	 * @param userDetails
	 * @return
	 */
	//generate token for user
	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		return doGenerateToken(claims, userDetails.getUsername());
	}


	/**
	 * Gets the user information and generates a token based on the Issuer, Expiration date, subject Id, and user.
	 * In order to make the token URL-safe it is signed using HS512 algorithm and a secret key
	 * This method always returns a compact and unique token for each session
	 * @param claims
	 * @param subject
	 * @return String 
	 */
	private String doGenerateToken(Map<String, Object> claims, String subject) {

		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}
	/**
	 * Validates the token to match a user session based on the user and password of the user
	 * This method returns the result of the comparison of the token's user, and the current user and the
	 * expiration date
	 * @param token
	 * @param userDetails
	 * @return Boolean
	 */
	//validate token
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
}

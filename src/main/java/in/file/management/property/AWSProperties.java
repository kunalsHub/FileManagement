/**
 * 
 */
package in.file.management.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * @author cropdata-user
 *
 */
@Component
@Data
public class AWSProperties {

	@Value("${aws.access.key}")
	private String accessKey;
	
	@Value("${aws.secret.key}")
	private String secretKey;
	
	@Value("${aws.bucket.name}")
	private String bucketName;
	
	@Value("${home.dir}")
	private String homeDir;

}

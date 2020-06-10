/**
 * 
 */
package in.file.management.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.amazonaws.services.redshift.model.BucketNotFoundException;
import com.amazonaws.services.s3.model.AmazonS3Exception;

/**
 * @author cropdata-user
 *
 */
@ControllerAdvice
public class S3ExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler({ BucketAlreadyExistException.class })
	public ResponseEntity<Object> handleBucketAlreadyExistException(final BucketAlreadyExistException ex,
			final WebRequest request) {
		Map<String, Object> res = new HashMap<>();
		res.put("status", false);
		res.put("message", "CONFLICT");
		res.put("error", ex.getMessage());
		return new ResponseEntity<Object>(res, new HttpHeaders(), HttpStatus.CONFLICT);
	}

	@ExceptionHandler({ AmazonS3Exception.class })
	public ResponseEntity<Object> handleAmazonS3Exception(final AmazonS3Exception ex, final WebRequest request) {
		Map<String, Object> res = new HashMap<>();
		res.put("status", false);
		res.put("message", "aws exception occured");
		res.put("error", ex.getMessage());
		return new ResponseEntity<Object>(res, new HttpHeaders(), HttpStatus.FAILED_DEPENDENCY);
	}

	@ExceptionHandler({ BucketNotFoundException.class })
	public ResponseEntity<Object> handleBucketNotFoundException(final BucketNotFoundException ex,
			final WebRequest request) {
		Map<String, Object> res = new HashMap<>();
		res.put("status", false);
		res.put("message", "NOT FOUND");
		res.put("error", ex.getMessage());
		return new ResponseEntity<Object>(res, new HttpHeaders(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler({ FileUploadException.class })
	public ResponseEntity<Object> handleFileUploadException(final FileUploadException ex, final WebRequest request) {
		Map<String, Object> res = new HashMap<>();
		res.put("status", false);
		res.put("message", "upload faild");
		res.put("error", ex.getMessage());
		return new ResponseEntity<Object>(res, new HttpHeaders(), HttpStatus.EXPECTATION_FAILED);
	}

	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Object> handleAll(final Exception ex, final WebRequest request) {
		Map<String, Object> res = new HashMap<>();
		res.put("status", false);
		res.put("message", "Internal Server Error");
		res.put("error", ex.getMessage());
		return new ResponseEntity<Object>(res, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

}

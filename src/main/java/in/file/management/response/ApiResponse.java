/**
 * 
 */
package in.file.management.response;

import org.springframework.stereotype.Component;

/**
 * @author cropdata-user
 *
 */
@Component
public class ApiResponse {

	public Api response(boolean success, String message, String error) {
		Api api = new Api();
		api.setSuccess(success);
		if (success) {
			api.setMessage(message);
		} else {
			api.setError(error);
		}
		return api;
	}

}

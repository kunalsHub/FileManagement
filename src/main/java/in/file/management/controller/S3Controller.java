/**
 * 
 */
package in.file.management.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import in.file.management.response.Api;
import in.file.management.service.AmazonS3Service;

/**
 * @author cropdata-user
 *
 */
@RestController
@RequestMapping("/aws/s3/v1.0/")
public class S3Controller {

	@Autowired
	AmazonS3Service amazonS3Service;

	@PostMapping("create-bucket")
	public Api createBucket(String bucketName) {
		return this.amazonS3Service.createNewBucket(bucketName);
	}

	@DeleteMapping("delete-bucket")
	public Api deleteBucket(String bucketName) {
		return this.amazonS3Service.deleteBucket(bucketName);
	}

	@GetMapping("bucket-list")
	public List<Bucket> listBuckets() {
		return amazonS3Service.listBuckets();

	}

	@PostMapping("/uploadFile")
	public ResponseEntity<String> uploadFile(MultipartFile file, String dirPath) {
		String uploadFile = this.amazonS3Service.uploadFile(file, dirPath);
		return ResponseEntity.status(HttpStatus.OK).body(uploadFile);
	}

	@GetMapping("bucket-summary")
	public List<S3ObjectSummary> listBucketSummary() {
		return this.amazonS3Service.bucketSummaries();
	}

	@GetMapping("/get-file")
	public ResponseEntity<Resource> getFile(@RequestParam String bucketName,
			@RequestParam(required = false) String keyName, HttpServletResponse response, HttpServletRequest request) {
		File downloadFile = amazonS3Service.downloadFile(bucketName, keyName);

		Resource resource = new FileSystemResource(downloadFile.getAbsoluteFile());
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);

	}

	@PostMapping("copy-to")
	public Api copyTo(String fromBucket, String fromKey, String toBucket, String toKey) {
		return this.amazonS3Service.copyTo(fromBucket, fromKey, toBucket, toKey);

	}

}

/**
 * 
 */
package in.file.management.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.redshift.model.BucketNotFoundException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import in.file.management.constant.AWS;
import in.file.management.exception.BucketAlreadyExistException;
import in.file.management.property.AWSProperties;
import in.file.management.response.Api;
import in.file.management.response.ApiResponse;
import in.file.management.utils.FileUtils;

/**
 * @author cropdata-user
 *
 */
@Service
public class AmazonS3Service {

	private static final Logger log = LoggerFactory.getLogger(AmazonS3Service.class);

	@Autowired
	AmazonS3 amazonS3;

	@Autowired
	AWSProperties awsProperties;

	@Autowired
	ApiResponse apiResponse;

	public Api createNewBucket(String bucketName) {
		if (this.amazonS3.doesBucketExistV2(bucketName)) {
			throw new BucketAlreadyExistException("Bucket Already Exist");
		} else {
			try {
				this.amazonS3.createBucket(bucketName);
				return apiResponse.response(true, "bucket create successfully", null);
			} catch (AmazonS3Exception e) {
				return apiResponse.response(false, null, e.getMessage());
			}
		}
	}// Create New Bucket

	public List<Bucket> listBuckets() {
		return this.amazonS3.listBuckets();
	}// List Buckets

	public Api deleteBucket(String bucketName) {
		if (amazonS3.doesBucketExistV2(bucketName)) {
			this.amazonS3.deleteBucket(bucketName);
			return apiResponse.response(true, bucketName + " bucket deleted", null);
		} else {
			throw new BucketNotFoundException("Bucket Not found");
		}
	}// Delete an Bucket

	public String uploadFile(MultipartFile file, String dirPath) {
		String downloadUrl = null;
		File multipartToFile = FileUtils.multipartToFile(file);
		try {
			amazonS3.putObject(AWS.BUCKET_MY_BUCKET_CONTENT + dirPath, multipartToFile.getName(), multipartToFile);
			downloadUrl = AWS.ENDPOINT_MY_BUCKET_CONTENT + multipartToFile.getName();
		} catch (Exception e) {
			try {
				throw new FileUploadException(e.getMessage());
			} catch (FileUploadException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return downloadUrl;
	}// Upload an Object

	public List<S3ObjectSummary> bucketSummaries() {
		List<S3ObjectSummary> s3ObjectSummaries = new ArrayList<>();
		ListObjectsV2Result result = amazonS3.listObjectsV2(AWS.BUCKET_MY_BUCKET_CONTENT);
		for (S3ObjectSummary summary : result.getObjectSummaries()) {
			s3ObjectSummaries.add(summary);
		}
		return s3ObjectSummaries;
	}// List Objects

	public File downloadFile(String bucketName, String keyName) {
		File file = null;
		OutputStream fos = null;
		try {
			S3Object s3Object = amazonS3.getObject(bucketName, keyName);
			S3ObjectInputStream s3is = s3Object.getObjectContent();
			file = new File(keyName);
			String fileNameFromAbsulatePath = FileUtils.getFileNameFromAbsulatePath(keyName);
			file = new File(this.awsProperties.getHomeDir() + "/amazon/s3/" + fileNameFromAbsulatePath);
			file.mkdirs();
			if (file.exists()) {
				file.delete();
			} else {
				file.createNewFile();
			}
			fos = new FileOutputStream(file.getAbsolutePath());

			byte[] read_buf = new byte[2048];
			int read_len = 0;
			while ((read_len = s3is.read(read_buf)) > 0) {
				fos.write(read_buf, 0, read_len);
			}
			s3is.close();
			fos.close();
		} catch (AmazonServiceException e) {
			System.err.println(e.getErrorMessage());
			System.exit(1);
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		return file;
	}// Download an Object

	public Api copyTo(String fromBucket, String fromKey, String toBucket, String toKey) {
		try {
			amazonS3.copyObject(fromBucket, fromKey, toBucket, toKey);
			return apiResponse.response(true, "copy succeed", null);
		} catch (AmazonServiceException e) {
			return apiResponse.response(false, null, e.getMessage());
		}
	}// Copy, Move, or Rename Objects

	public Api deleteObjectFromBucket(String bucketName, String keyName) {
		try {
			amazonS3.deleteObject(bucketName, keyName);
			return apiResponse.response(true, keyName + " object deleted", null);
		} catch (AmazonServiceException e) {
			return apiResponse.response(true, null, e.getMessage());
		}
	}// Delete an Object

	public Api deleteObjectsFromBucket(String bucketName, String[] keys) {
		if (keys.length < 2) {
			return apiResponse.response(true, keys + "please select 2 or more keys to delete", null);
		}
		try {
			DeleteObjectsRequest dor = new DeleteObjectsRequest(bucketName).withKeys(keys);
			amazonS3.deleteObjects(dor);
			return apiResponse.response(true, keys + " objects deleted", null);
		} catch (Exception e) {
			return apiResponse.response(false, null, e.getMessage());
		}
	}// Delete Multiple Objects at Once
}

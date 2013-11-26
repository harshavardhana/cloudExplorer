package s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CanonicalGrantee;
import com.amazonaws.services.s3.model.EmailAddressGrantee;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.Owner;
import com.amazonaws.services.s3.model.Permission;
import java.net.URL;

public class acl {

    void setACLpublic(String object, String access_key, String secret_key, String endpoint, String bucket) {
        try {
            AWSCredentials credentials = new BasicAWSCredentials(access_key, secret_key);
            AmazonS3 s3Client = new AmazonS3Client(credentials);
            s3Client.setEndpoint(endpoint);
            s3Client.setObjectAcl(bucket, object, CannedAccessControlList.PublicRead);
        } catch (Exception setACLpublic) {
            System.out.print("\nException occured in ACL");
        }
    }

    void setACLprivate(String object, String access_key, String secret_key, String endpoint, String bucket) {
        try {
            AWSCredentials credentials = new BasicAWSCredentials(access_key, secret_key);
            AmazonS3 s3Client = new AmazonS3Client(credentials);
            s3Client.setEndpoint(endpoint);
            s3Client.setObjectAcl(bucket, object, CannedAccessControlList.Private);
        } catch (Exception setACLpublic) {
            System.out.print("\nException occured in ACL");
        }
    }

    String setACLurl(String object, String access_key, String secret_key, String endpoint, String bucket) {
        String URL = null;
        try {
            AWSCredentials credentials = new BasicAWSCredentials(access_key, secret_key);
            AmazonS3 s3Client = new AmazonS3Client(credentials);
            s3Client.setEndpoint(endpoint);
            java.util.Date expiration = new java.util.Date();
            long milliSeconds = expiration.getTime();
            milliSeconds += 1000 * 60 * 60; // Add 1 hour.
            expiration.setTime(milliSeconds);
            GeneratePresignedUrlRequest generatePresignedUrlRequest
                    = new GeneratePresignedUrlRequest(bucket, object);
            generatePresignedUrlRequest.setMethod(HttpMethod.GET);
            generatePresignedUrlRequest.setExpiration(expiration);
            URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
            URL = ("Pre-Signed URL = " + url.toString());
        } catch (Exception setACLpublic) {
            System.out.print("\nException occured in ACL");
        }
        return URL;
    }
}

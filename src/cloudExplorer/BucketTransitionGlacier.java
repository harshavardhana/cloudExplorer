/**
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package cloudExplorer;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration.Transition;
import java.util.ArrayList;
import java.util.List;
import static cloudExplorer.NewJFrame.jTextArea1;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.StorageClass;

public class BucketTransitionGlacier implements Runnable {

    NewJFrame mainFrame;
    String what = null;
    String access_key = null;
    String bucket = null;
    String endpoint = null;
    String secret_key = null;
    String destination = null;
    String version = null;
    Thread transition;
    String days;
    String prefix = null;
    Boolean disabled = false;

    public void calibrate() {
        try {
            jTextArea1.setCaretPosition(jTextArea1.getLineStartOffset(jTextArea1.getLineCount() - 1));
        } catch (Exception e) {
        }
    }

    BucketTransitionGlacier(String Aaccess_key, String Asecret_key, String Abucket, String Aendpoint, String Adays, String Aprefix, Boolean Adisabled) {

        access_key = Aaccess_key;
        secret_key = Asecret_key;
        bucket = Abucket;
        endpoint = Aendpoint;
        days = Adays;
        prefix = Aprefix;
        disabled = Adisabled;
    }

    public void run() {
        AWSCredentials credentials = new BasicAWSCredentials(access_key, secret_key);
        AmazonS3 s3Client = new AmazonS3Client(credentials,
                new ClientConfiguration());
        s3Client.setS3ClientOptions(S3ClientOptions.builder().setPathStyleAccess(false).build());
        s3Client.setEndpoint(endpoint);
        int converted_days = 0;
        if (!disabled) {
            converted_days = Integer.parseInt(days);
        }

        Transition transToArchive = new Transition()
                .withDays(converted_days)
                .withStorageClass(StorageClass.Glacier);

        BucketLifecycleConfiguration.Rule ruleArchiveAndExpire = null;
        if (!disabled) {
            ruleArchiveAndExpire = new BucketLifecycleConfiguration.Rule()
                    .withPrefix(prefix)
                    .withTransition(transToArchive)
                    // .withExpirationInDays(converted_days + 1)
                    .withStatus(BucketLifecycleConfiguration.ENABLED.toString());
        } else {
            ruleArchiveAndExpire = new BucketLifecycleConfiguration.Rule()
                    .withPrefix(prefix)
                    .withTransition(transToArchive)
                    //.withExpirationInDays(100)
                    .withStatus(BucketLifecycleConfiguration.DISABLED.toString());
        }
        List<BucketLifecycleConfiguration.Rule> rules = new ArrayList<BucketLifecycleConfiguration.Rule>();
        rules.add(ruleArchiveAndExpire);

        try {
            BucketLifecycleConfiguration configuration = new BucketLifecycleConfiguration()
                    .withRules(rules);
            s3Client.setBucketLifecycleConfiguration(bucket, configuration);
        } catch (Exception get) {
            mainFrame.jTextArea1.append("\n" + get.getMessage());
        }
        if (!disabled) {
            mainFrame.jTextArea1.append("\nSent request to set bucket life cycle to tier to Glacier after: " + converted_days + " day(s). Please observe for any errors.");
        } else {
            mainFrame.jTextArea1.append("\nSent request to disable the bucket life cycle. Please observe for any errors.");
        }
        calibrate();
    }

    void startc(String Aaccess_key, String Asecret_key, String Abucket, String Aendpoint, String Adays, String Aprefix, Boolean Adisabled) {
        transition = new Thread(new BucketTransitionGlacier(Aaccess_key, Asecret_key, Abucket, Aendpoint, Adays, Aprefix, Adisabled));
        transition.start();
    }

    void stop() {
        transition.stop();
        mainFrame.jTextArea1.setText("\nDownload completed or aborted.\n");
    }

}

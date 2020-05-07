package nl.xservices.plugins;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import java.lang.IllegalStateException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.security.cert.CertificateException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;

public class SSLCertificateChecker extends CordovaPlugin {

  private static final String ACTION_CHECK_EVENT = "check";
  private static final String ACTION_GET_FINGERPRINT_EVENT = "getFingerprint";
  private static char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
  private static final String DEFAULT_ALGORITHM = "SHA-1";

  @Override
  public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
    if (ACTION_CHECK_EVENT.equals(action)) {

      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          try {
            final String serverURL = args.getString(0);
            final JSONArray allowedFingerprints = args.getJSONArray(2);
            final String algorithm = args.optString(3, DEFAULT_ALGORITHM);
            final String serverCertFingerprint = getFingerprint(serverURL, algorithm);
            for (int j=0; j<allowedFingerprints.length(); j++) {
              if (allowedFingerprints.get(j).toString().equalsIgnoreCase(serverCertFingerprint)) {
                callbackContext.success("CONNECTION_SECURE");
                return;
              }
            }
            callbackContext.error("CONNECTION_NOT_SECURE. Details: No matching fingerprint");
          } catch (Exception e) {
            // callbackContext.error("CONNECTION_NOT_SECURE");
            callbackContext.error("CONNECTION_FAILED: " + e.toString());
          }
        }
      });

      return true;

	} else if(ACTION_GET_FINGERPRINT_EVENT.equals(action)) {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          try {
            final String serverURL = args.getString(0);
            final String algorithm = args.optString(1, DEFAULT_ALGORITHM);
            final String serverCertFingerprint = getFingerprint(serverURL, algorithm);
            callbackContext.success(serverCertFingerprint);
          } catch (Exception e) {
            callbackContext.error("CERTIFICATE_READ_ERROR: " + e.toString());
          }
        }
      });
      return true;
    } else {
      callbackContext.error("sslCertificateChecker." + action + " is not a supported function. Did you mean '" + ACTION_CHECK_EVENT + "'?");
      return false;
    }
  }

  private static String getFingerprint(String httpsURL, String algorithm) 
    throws IOException, SocketTimeoutException, SSLPeerUnverifiedException, IllegalStateException, NoSuchAlgorithmException, CertificateEncodingException {

    // throws IOException
    final HttpsURLConnection con = (HttpsURLConnection) new URL(httpsURL).openConnection();
    con.setConnectTimeout(10000);
    // throws IOException, SocketTimeoutException
    con.connect();

    // throws SSLPeerUnverifiedException, IllegalStateException

    // SSLPeerUnverifiedException Indicates that the peer's identity has not been verified.
    // When the peer was not able to identify itself. 
    // For example:
    // no certificate, 
    // the particular cipher suite being used does not support authentication,
    // no peer authentication was established during SSL handshaking.

    // IllegalStateException: If this method is called before the connection has been established.
    // Signals that a method has been invoked at an illegal or inappropriate time. 
    // In other words, the Java environment or Java application is not in an appropriate state for the requested operation.
    final Certificate cert = con.getServerCertificates()[0];

    // throws NoSuchAlgorithmException
    final MessageDigest md = MessageDigest.getInstance(algorithm);

    // cert.getEncoded throws CertificateEncodingException, md.update does not throw 
    // This is thrown whenever an error occurs while attempting to encode a certificate.
    md.update(cert.getEncoded());

    // md.digest() does not throw
    return dumpHex(md.digest());

  }

  private static String dumpHex(byte[] data) {
    final int n = data.length;
    final StringBuilder sb = new StringBuilder(n * 3 - 1);
    for (int i = 0; i < n; i++) {
      if (i > 0) {
        sb.append(' ');
      }
      sb.append(HEX_CHARS[(data[i] >> 4) & 0x0F]);
      sb.append(HEX_CHARS[data[i] & 0x0F]);
    }
    return sb.toString();
  }
}

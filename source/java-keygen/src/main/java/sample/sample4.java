package sample;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.FileInputStream;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import java.security.Key;
import java.security.PrivateKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.PublicKey;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import java.util.Base64;

public class sample4
{
    static private Base64.Encoder encoder = Base64.getEncoder();

    static private void writeBinary(OutputStream out,Key key)
	throws java.io.IOException
    {
	out.write(key.getEncoded());
    }

    static public void main(String[] args) throws Exception
    {
	if ( args.length != 4 ) {
	    System.err.println("verify digital signature.");
	    System.err.println("usage: java algo pubKeyFile dataFile signFile");
	    System.exit(1);
	}

	int index = 0;
	String algo = args[index]; index++;
	String keyFile = args[index]; index++;
	String dataFile = args[index]; index++;
	String signFile = args[index]; index++;

	/* Read the public key bytes */
	Path path = Paths.get(keyFile);
	byte[] bytes = Files.readAllBytes(path);

	/* Generate public key. */
	X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
	KeyFactory kf = KeyFactory.getInstance("RSA");
	PublicKey pub = kf.generatePublic(ks);

	Signature sign = Signature.getInstance("SHA256withRSA");
	sign.initVerify(pub);

	InputStream in = null;
	try {
	    in = new FileInputStream(dataFile);
	    byte[] buf = new byte[2048];
	    int len;
	    while ((len = in.read(buf)) != -1) {
		sign.update(buf, 0, len);
	    }
	} finally {
	    if ( in != null ) in.close();
	}

	/* Read the signature bytes */
	path = Paths.get(signFile);
	bytes = Files.readAllBytes(path);
	System.out.println(dataFile + ": Signature " +
			   (sign.verify(bytes) ? "OK" : "Not OK"));
    }
}

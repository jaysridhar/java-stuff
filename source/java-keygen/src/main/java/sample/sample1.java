package sample;

import java.io.Writer;
import java.io.FileWriter;
import java.io.OutputStreamWriter;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import java.util.Base64;

public class sample1
{
    static private Base64.Encoder encoder = Base64.getEncoder();

    static private void writeBase64(Writer out,Key key)
	throws java.io.IOException
    {
	byte[] buf = key.getEncoded();
	out.write(encoder.encodeToString(buf));
	out.write("\n");
    }

    static public void main(String[] args) throws Exception
    {
	if ( args.length == 0 ) {
	    System.err.println("usage: java algo keySize [outFile]");
	    System.exit(1);
	}

	int index = 0;
	String algo = args[index]; index++;
	int keySize = Integer.parseInt(args[index]); index++;
	String outFile = null;
	if ( index < args.length ) outFile = args[index]; index++;

	KeyPairGenerator kpg = KeyPairGenerator.getInstance(algo);

	/* initialize with keySize: typically 2048 for RSA */
	kpg.initialize(keySize);
	KeyPair kp = kpg.generateKeyPair();

	Writer out = null;
	try {
	    if ( outFile != null ) out = new FileWriter(outFile + ".key");
	    else out = new OutputStreamWriter(System.out);

	    System.err.println("Private key format: " +
			       kp.getPrivate().getFormat());
	    out.write("-----BEGIN RSA PRIVATE KEY-----\n");
	    writeBase64(out, kp.getPrivate());
	    out.write("-----END RSA PRIVATE KEY-----\n");

	    if ( outFile != null ) {
		out.close();
		out = new FileWriter(outFile + ".pub");
	    }

	    System.err.println("Public key format: " +
			       kp.getPublic().getFormat());
	    out.write("-----BEGIN RSA PUBLIC KEY-----\n");
	    writeBase64(out, kp.getPublic());
	    out.write("-----END RSA PUBLIC KEY-----\n");
	} finally {
	    if ( out != null ) out.close();
	}
    }
}

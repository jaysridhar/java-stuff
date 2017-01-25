package sample;

import java.io.Writer;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.FileOutputStream;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import java.util.Base64;

public class sample2
{
    static private Base64.Encoder encoder = Base64.getEncoder();

    static private void writeBinary(OutputStream out,Key key)
	throws java.io.IOException
    {
	out.write(key.getEncoded());
    }

    static public void main(String[] args) throws Exception
    {
	if ( args.length != 3 ) {
	    System.err.println("usage: java algo keySize outFile");
	    System.exit(1);
	}

	int index = 0;
	String algo = args[index]; index++;
	int keySize = Integer.parseInt(args[index]); index++;
	String outFile = args[index]; index++;

	KeyPairGenerator kpg = KeyPairGenerator.getInstance(algo);

	/* initialize with keySize: typically 2048 for RSA */
	kpg.initialize(keySize);
	KeyPair kp = kpg.generateKeyPair();

	OutputStream out = null;
	try {
	    System.err.println("Private key format: " +
			       kp.getPrivate().getFormat());
	    out = new FileOutputStream(outFile + ".key");
	    writeBinary(out, kp.getPrivate());
	    out.close();

	    System.err.println("Public key format: " +
			       kp.getPublic().getFormat());
	    out = new FileOutputStream(outFile + ".pub");
	    writeBinary(out, kp.getPublic());
	} finally {
	    if ( out != null ) out.close();
	}
    }
}

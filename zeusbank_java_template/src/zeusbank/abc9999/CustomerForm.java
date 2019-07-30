package zeusbank.abc9999;

import com.ibm.cics.server.*;
import zeusbank.util.*;
import java.io.UnsupportedEncodingException;

public class CustomerForm {
	private final static String EBCDIC = "037";
	private final static String ASCII = "iso-8859-1";

	public static void main(String args[]) {
		Task t = Task.getTask();
		if (t == null) {
			System.out.println("abc9999: failed to get task");
			return;
		}
	
		// Implement your customer form program instead of this stub
		try {
			HttpRequest req = HttpRequest.getHttpRequestInstance();
			String prog = t.getProgramName();
			String msg = "abc9999: program " + prog + " is a stub";
			System.out.println(msg);
			HttpResponse resp = new HttpResponse();
			Document doc = new Document();
			doc.createText(msg);
			resp.setMediaType("text/plain");
			resp.sendDocument(doc, (short)200, "OK", ASCII);
		} catch (Exception e) {
			t.out.println("abc9999 CustomerForm error:");
			e.printStackTrace(t.out);
		}
	}
}


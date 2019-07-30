package zeusbank.abc9999;

import com.ibm.cics.server.*;
import zeusbank.util.*;
 
public class AFCheck
{
    public static void main(CommAreaHolder CAH)
    {
        Task t = Task.getTask();
        if (t == null) {
		System.err.println("abc9999: Can't get Task");
		return;
        }
        
	// Implement your BTS Process program instead of this stub
	try {
		String prog = t.getProgramName();
		t.out.println("abc9999: program " + prog + " is a stub");
        } catch(Exception e) {
        	t.out.println("abc9999: error:");
        	e.printStackTrace(t.out);
        }
    }
}

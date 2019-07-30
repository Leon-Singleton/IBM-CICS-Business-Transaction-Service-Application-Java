package zeusbank.util;

// 17 March 2016. mbeattie. "V2" API with _v2 suffices stripped now that there's no clash with the original V1.


import com.ibm.cics.server.*;
import java.io.UnsupportedEncodingException;

public class BTS {
	private String ccsid;
	private Channel largs;
	
	public BTS(String args_channel_name) throws ChannelErrorException {
		largs = Task.getTask().createChannel(args_channel_name);
		ccsid = System.getProperty("com.ibm.cics.jvmserver.local.ccsid");
	}

	// Private implementation methods
	private String call_zjgpcont(String target, String name) throws CicsException, UnsupportedEncodingException {
		Container target_ctr = largs.createContainer("TARGET");
		target_ctr.put(target.getBytes(ccsid));
		Container name_ctr = largs.createContainer("NAME");
		name_ctr.put(name.getBytes(ccsid));
		Program prog = new Program();
		prog.setName("ZJGPCONT");
		try {
			prog.link(largs);
			Container value_ctr = largs.getContainer("VALUE");
			String value = new String(value_ctr.get(), ccsid);
			value_ctr.delete();
			return value;
		} finally {
			target_ctr.delete();
			name_ctr.delete();
		}
	}
		
	private void call_zjppcont(String target, String name, String value) throws CicsException, UnsupportedEncodingException {
		Container target_ctr = largs.createContainer("TARGET");
		target_ctr.put(target.getBytes(ccsid));
		Container name_ctr = largs.createContainer("NAME");
		name_ctr.put(name.getBytes(ccsid));
		Container value_ctr = largs.createContainer("VALUE");
		value_ctr.put(value.getBytes(ccsid));
		Program prog = new Program();
		prog.setName("ZJPPCONT");
		try {
			prog.link(largs);
		} finally {
			target_ctr.delete();
			name_ctr.delete();
			value_ctr.delete();
		}
	}
	
	// Following are the public API methods.
	// These need to be called on a BTS object constructed near the start of the main() method of a
	// task program in the form
	//   BTS bts = new BTS("LARGS");
	// This will create channel LARGS for the lifetime of the program that will be used to pass arguments to and
	// from the wrapper programs invoked by this class.
	// The API calls can then be invoked by, for example:
	//   String foo = bts.get_process_container(name);
	// Errors are reported by causing a program ABEND (with the first of the four characters being "Z") and
	// logging to stdout the numeric error response code (RESP) and additional error response code (RESP2) from
	// the underlying EXEC CICS API call. These are documented in the CICS Application Programming Reference manual.
   	
	// Does EXEC CICS GET CONTAINER(name) PROCESS
	// Returns the contents of the (BIT-valued) container as a String
	public String get_process_container(String name) throws CicsException, UnsupportedEncodingException {
		return call_zjgpcont("P", name);
	}
	
	// Does EXEC CICS GET CONTAINER(name) ACQPROCESS
	// Returns the contents of the (BIT-valued) container as a String
	public String get_acqprocess_container(String name) throws CicsException, UnsupportedEncodingException {
		return call_zjgpcont("A", name);
	}
	
	// Does EXEC CICS PUT CONTAINER(name) FROM() PROCESS
	public void put_process_container(String name, String value) throws CicsException, UnsupportedEncodingException {
		call_zjppcont("P", name, value);
	}

	// Does EXEC CICS PUT CONTAINER(name) FROM() ACQPROCESS
	public void put_acqprocess_container(String name, String value) throws CicsException, UnsupportedEncodingException {
		call_zjppcont("A", name, value);
	}

	// Does EXEC CICS RETRIEVE REATTACH EVENT(event) and returns the right-blank-stripped event as a String. 
	// Returns "*END" for RESP(END) and "*INVREQ" for RESP(INVREQ)
	public String retrieve_reattach_event() throws CicsException, UnsupportedEncodingException {
		Program prog = new Program();
		prog.setName("ZJRETREV");
		prog.link(largs);
		Container event_ctr = largs.getContainer("EVENT");
		String event = new String(event_ctr.get(), ccsid);
		event_ctr.delete();
		return event;
	}

	// Does EXEC CICS DEFINE INPUT EVENT(event)
	public void define_input_event(String event) throws CicsException, UnsupportedEncodingException {
		Container event_ctr = largs.createContainer("EVENT");
		event_ctr.put(event.getBytes(ccsid));
		Program prog = new Program();
		prog.setName("ZJDEFIEV");
		try {
			prog.link(largs);
		} finally {
			event_ctr.delete();
		}
	}
	
	// Does EXEC CICS DELETE EVENT(ev) 
	public void delete_event(String event) throws CicsException, UnsupportedEncodingException {
		Container event_ctr = largs.createContainer("EVENT");
		event_ctr.put(event.getBytes(ccsid));
		Program prog = new Program();
		prog.setName("ZJDELEEV");
		try {
			prog.link(largs);
		} finally {
			event_ctr.delete();
		}
	}
	
	// Does EXEC CICS DEFINE TIMER(timer) EVENT(event) AFTER DAYS(d) HOURS(h) MINUTES(m) SECONDS(s)
	// The total_seconds argument must be specified in seconds and is broken down into the
	// appropriate arguments to DAYS, HOURS, MINUTES and SECONDS for the underlying EXEC CICS API
	public void define_timer_after(String timer, String event, int total_seconds)
			throws CicsException, UnsupportedEncodingException {
		Container timer_ctr = largs.createContainer("TIMER");
		timer_ctr.put(timer.getBytes(ccsid));
		Container event_ctr = largs.createContainer("EVENT");
		event_ctr.put(event.getBytes(ccsid));
		Container seconds_ctr = largs.createContainer("SECONDS");
		seconds_ctr.put(Integer.toString(total_seconds).getBytes(ccsid));
		Program prog = new Program();
		prog.setName("ZJDEFTIM");
		try {
			prog.link(largs);
		} finally {
			timer_ctr.delete();
			event_ctr.delete();
			seconds_ctr.delete();
		}
	}

	// Does EXEC CICS DEFINE TIMER(timer) AFTER DAYS(d) HOURS(h) MINUTES(m) SECONDS(s) 
	// The total_seconds argument must be specified in seconds and is broken down into the
	// appropriate arguments to DAYS, HOURS, MINUTES and SECONDS for the underlying EXEC CICS API
	public void define_timer_after(String timer, int total_seconds) throws CicsException, UnsupportedEncodingException {
		define_timer_after(timer, timer, total_seconds);
	}
	
	// Does EXEC CICS DELETE TIMER(timer) 
	public void delete_timer(String timer) throws CicsException, UnsupportedEncodingException {
		Container timer_ctr = largs.createContainer("TIMER");
		timer_ctr.put(timer.getBytes(ccsid));
		Program prog = new Program();
		prog.setName("ZJDELTIM");
		try {
			prog.link(largs);
		} finally {
			timer_ctr.delete();
		}
	}

	// Does EXEC CICS ASSIGN PROCESS(proc)
	// Returns proc as a String 
	public String get_process_name() throws CicsException, UnsupportedEncodingException {
		Program prog = new Program();
		prog.setName("ZJGPROC");
		prog.link(largs);
		Container proc_ctr = largs.getContainer("PROCESS");
		String proc = new String(proc_ctr.get(), ccsid);
		proc_ctr.delete();
		return proc;
	}

	// Does EXEC CICS ACQUIRE PROCESS(process) PROCESSTYPE(processtype)
	public void acquire_process(String process, String processtype)
			throws CicsException, UnsupportedEncodingException {
		Container process_ctr = largs.createContainer("PROCESS");
		process_ctr.put(process.getBytes(ccsid));
		Container processtype_ctr = largs.createContainer("PROCESSTYPE");
		processtype_ctr.put(processtype.getBytes(ccsid));
		Program prog = new Program();
		prog.setName("ZJACQPRO");
		try {
			prog.link(largs);
		} finally {
			process_ctr.delete();
			processtype_ctr.delete();
		}
	}
	
	// Does EXEC CICS RUN ACQPROCESS ASYNCHRONOUS INPUTEVENT(inputevent)
	public void run_acqprocess_asynchronous(String inputevent)
			throws CicsException, UnsupportedEncodingException {
		Container inputevent_ctr = largs.createContainer("INPUTEVENT");
		inputevent_ctr.put(inputevent.getBytes(ccsid));
		Program prog = new Program();
		prog.setName("ZJRUNACQ");
		try {
			prog.link(largs);
		} finally {
			inputevent_ctr.delete();
		}
	}
}
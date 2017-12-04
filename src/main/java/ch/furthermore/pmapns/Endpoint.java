package ch.furthermore.pmapns;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ProcessBuilder.Redirect;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Pre-cond:
 * <pre>
 * gem install nomad-cli
 * </pre>
 * 
 * Sample:
 * <pre>
 * $ curl --data-binary @'/Users/chrigi/Desktop/Zertifikate Dev Push.pem' -H "Content-Type: application/octet-stream" http://localhost:6060/certs
 * {"id":"24d688c4-39d1-4753-99ea-3d2b6a1e7b8a"} 
 * 
 * $ curl -d '{"tokens":["your-apns-token"],"certId":"24d688c4-39d1-4753-99ea-3d2b6a1e7b8a"}' -H "Content-Type: application/json" http://localhost:6060/recipients
 * {"id":"23ca16ed-6521-4326-a98d-a0387659c37f"}
 * 
 * $ curl -d "{\"message\":\"hallo -c ! \\\" welt\nfoo\"}" -H "Content-Type: application/json" http://localhost:6060/recipients/23ca16ed-6521-4326-a98d-a0387659c37f
 * {"success":"true"}
 * </pre>
 */
@RestController
public class Endpoint {
	private final static Logger log = LoggerFactory.getLogger(Endpoint.class);
	
	@Value("${apnBinary:/usr/bin/apn}")
	private String apnBinary;
	
	@Autowired
	private Storage storage;
	
	@RequestMapping(path="/certs", method=RequestMethod.POST, consumes="application/octet-stream", produces="application/json")
	@ResponseBody
	public NewIdResponse createCert(InputStream in) throws IOException {
		String id = storage.insertBinary(in, ".pem");
		
		log.info("Cert created: {}", id);
		
		return new NewIdResponse(id);
	}
	
	@RequestMapping(path="/recipients", method=RequestMethod.POST, consumes="application/json", produces="application/json")
	@ResponseBody
	public NewIdResponse createRecipient(@RequestBody Recipient recipient) throws IOException {
		String id = storage.insert(recipient);
		
		log.info("Recipient created: {}", id);
		
		return new NewIdResponse(id);
	}
	
	@RequestMapping(path="/recipients/{recipientId}", method=RequestMethod.POST, consumes="application/json", produces="application/json")
	@ResponseBody
	public Map<String,String> sendMessage(@PathVariable("recipientId") String recipientId, @RequestBody Map<String,String> params) throws IOException, InterruptedException {
		String message = params.get("message");
		
		Recipient recipient = storage.load(Recipient.class, recipientId);
		
		for (String tokenId : recipient.getTokens()) {
			Process p = new ProcessBuilder(apnBinary,
						"push",
						tokenId,
						"-c",
						storage.file(recipient.getCertId(), ".pem").getAbsolutePath(),
						"-m",
						message
					)
					.redirectError(Redirect.INHERIT)
					.redirectOutput(Redirect.INHERIT)
					.start();
			p.waitFor();
		}
		
		Map<String,String> result = new HashMap<>();
		result.put("success", "true");
		return result;
	}
}

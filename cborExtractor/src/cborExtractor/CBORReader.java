package cborExtractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.cbor.CBORParser;

/**
 * 
 * Read a CBOR file containing multiple pages and write the info about pages in a separate folder.  
 * 
 * @author vaio
 *
 */
public class CBORReader {
	
	public CBORReader() throws IOException{
		
	}
	
	
	@SuppressWarnings("deprecation")
	public void singleCBORtoMultipleHTML(String cborFilePath, String outputDirectory) throws IOException{
		
		// read the CBOR File sequentially
		CBORFactory myFactory = new CBORFactory();
		FileInputStream fis = new FileInputStream(cborFilePath);
		ObjectMapper mapper = new ObjectMapper(myFactory);
		
		CBORParser myCborParser = myFactory.createParser(fis);
		// read the whole stored data as a JsonNode
		JsonNode documentNode = mapper.readValue(myCborParser, JsonNode.class);
		
		
		JsonNode objectNode = documentNode.get("Objects");
		Iterator<JsonNode> documentIterator = objectNode.elements();
		
		
		
		String allFieldsPath =  outputDirectory + "allFields";
		(new File(allFieldsPath)).mkdir();
		String onlySourcePath = outputDirectory + "source";
		(new File(onlySourcePath)).mkdir();
		
		while(documentIterator.hasNext())
		{
			JsonNode nextdocument = documentIterator.next();
			URL urlForThis = new URL(URLDecoder.decode(nextdocument.get("url").textValue()));
			System.out.println("Writing " + nextdocument.get("url").textValue());
			
			JsonNode currentResponse = nextdocument.get("response");
			JsonNode currentContent = currentResponse.get("body");
			
			
			
			// we got the Document. Now write the data output and the source output
			String URLHost = urlForThis.getHost();
			
			if(!(new File(allFieldsPath + File.separator + URLHost)).exists())
				(new File(allFieldsPath + File.separator + URLHost)).mkdir();
			
			if(!(new File(onlySourcePath + File.separator + URLHost)).exists())
				(new File(onlySourcePath + File.separator + URLHost)).mkdir();
			
			
			File allFieldsFile = new File(allFieldsPath + File.separator + URLHost + File.separator + URLEncoder.encode(urlForThis.toString()));
			JSONObject documentObject = new JSONObject(nextdocument.toString());
			
			PrintWriter myWriter = new PrintWriter(allFieldsFile);
			myWriter.write(documentObject.toString());
			myWriter.close();
			
			File onlySourceFile = new File(onlySourcePath + File.separator + URLHost + File.separator + URLEncoder.encode(urlForThis.toString()));
			myWriter = new PrintWriter(onlySourceFile);
			myWriter.write(currentContent.toString());
			myWriter.close();
		}
		
	}

}

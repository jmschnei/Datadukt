package de.dfki.slt.datadukt.conversion;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class ELGTokenParser {

	public static String getTokenForService(String srvId) {
		try {			
			/**
Connection connection = Jsoup.connect(blogUrl);
connection.userAgent("Mozilla");
connection.timeout(5000);
connection.cookie("cookiename", "val234");
connection.cookie("cookiename", "val234");
connection.referrer("http://google.com");
connection.header("headersecurity", "xyz123");
Document docCustomConn = connection.get();
			 */

			String blogUrl = "https://live.european-language-grid.eu/catalogue/#/resource/service/tool/"+srvId;
			Document doc = Jsoup.connect(blogUrl).get();

			Elements codes = doc.select("code");

			codes.forEach(cd -> System.out.println("code: " + cd.html()));


			//			HttpResponse<String> response = Unirest.get("").asString();
			//			
			//			if(response.getStatus()==200) {
			//				System.out.println(response.getBody());
			//				//TODO Parse HTML and get code elements.
			//			
			//				
			//				
			//				
			//			}
			//			else {
			//				
			//			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	protected List<String> cookies;
	protected HttpsURLConnection conn;

	protected final String USER_AGENT = "Mozilla/5.0";

	public static void main2(String[] args) throws Exception {
		String srvId = "487";
		//String url = "https://accounts.google.com/ServiceLoginAuth";
		String url = "https://live.european-language-grid.eu/auth/realms/ELG/protocol/openid-connect/auth?client_id=react-client&redirect_uri=https%3A%2F%2Flive.european-language-grid.eu%2Fcatalogue%2F%23%2Fsearch%2FASR&state=ae0215c5-9da8-48f4-8c23-fd4a3403a78b&response_mode=fragment&response_type=code&scope=openid&nonce=387a852e-e9d8-4b33-8711-0b84423a88cc";
		String url3 = "https://live.european-language-grid.eu/auth/realms/ELG/login-actions/authenticate?session_code=Q3BWflaQ_0AJyj8MuLpD7UEiPoUQw-H7fCojq9iwtV0&execution=9060cd0f-add4-435b-b03c-ec7bcec5f277&client_id=angular-frontend&tab_id=ELPYIiwKR7E";
		String url2 = "https://live.european-language-grid.eu/auth/realms/ELG/login-actions/authenticate?session_code=hdPyeivoHBIy6QcLLRTLc7BaYNClXG7NWcBzuh0efIw&execution=9060cd0f-add4-435b-b03c-ec7bcec5f277&client_id=react-client&tab_id=j9qDZbzq294";
		String gmail = "https://live.european-language-grid.eu/catalogue/#/resource/service/tool/"+srvId;

		ELGTokenParser http = new ELGTokenParser();

		// make sure cookies is turn on
		CookieHandler.setDefault(new CookieManager());

		// 1. Send a "GET" request, so that you can extract the form's data.
		String page = http.GetPageContent(url);
//		String postParams = http.getFormParams(page, "username@gmail.com", "password");
		String postParams = http.getFormParams(page, "julian.moreno_schneider@dfki.de", "passwd");

		// TODO I also have to extract the URL for sending the FORM Data.
//		String url2 = http.getFormURL(page);
		
		// 2. Construct above post's content and then send a POST request for
		// authentication
		http.sendPost(url2, postParams);

		// TODO We have to return the executed JAVASCRIPT Website, because it not, we only get javascript code and not the website itself with the code and everything.

		System.setProperty("webdriver.gecko.driver", "/Users/julianmorenoschneider/Downloads/gecko/geckodriver_macos");
//		WebDriver driver;
//	    driver = new FirefoxDriver();
//	    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
//
//	    driver.get(gmail);
//	    String htmlContent = driver.getPageSource();
//
//	    System.out.println(htmlContent);
	    
		
		System.out.println(http.getCookies());

	    String uri = gmail;
	    WebDriver driver2 = new FirefoxDriver();	    
	    
	    String baseurl=uri.toString();
	    driver2.get(baseurl);
	    String str = driver2.getPageSource();
	    driver2.close();
	    System.out.println(str);
	    //stream = new ByteArrayInputStream(str.getBytes());

//		// 3. success then go to gmail.
//		String result = http.GetPageContent(gmail);
//		System.out.println(result);
//		
//		Document doc = Jsoup.parse(result);
//		Elements elements = doc.getElementsByTag("script");
//		
//		elements.forEach(cd -> System.out.println("script: " + cd.html()));
//
//		String e = elements.get(0).html();
//	
//	    ScriptEngineManager manager = new ScriptEngineManager();
//	    ScriptEngine engine = manager.getEngineByName("javascript");
//
//	    Object o = engine.eval(e);
//	    System.out.println(o);
	    
	}

	public static void main(String[] args) throws Exception {
		ELGTokenParser etp = new ELGTokenParser();
//		System.out.println(etp.getELGServiceToken("487"));
		String refreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJlNTJmMmMxYi01N2Y1LTQxYjEtOGJkZS05MjA1OGFjZjQ1YjUifQ.eyJpYXQiOjE2MjQ0NTM5MDQsImp0aSI6ImNjMTVhZTlmLWJiNDAtNGQ1Zi04NTNhLTIyMGYyZjc1NzZjYSIsImlzcyI6Imh0dHBzOi8vbGl2ZS5ldXJvcGVhbi1sYW5ndWFnZS1ncmlkLmV1L2F1dGgvcmVhbG1zL0VMRyIsImF1ZCI6Imh0dHBzOi8vbGl2ZS5ldXJvcGVhbi1sYW5ndWFnZS1ncmlkLmV1L2F1dGgvcmVhbG1zL0VMRyIsInN1YiI6ImE1N2U3NGI3LTVjNGEtNGJjOC04NmI1LWEyMWJmNDIzZTkxYyIsInR5cCI6Ik9mZmxpbmUiLCJhenAiOiJlbGctb29iIiwic2Vzc2lvbl9zdGF0ZSI6IjBjYzQzNmI5LTdjMGMtNDhhMy1iMGZiLWQxZjUxMDViZDMxYSIsInNjb3BlIjoiRUxHLXByb2ZpbGUgcHJvZmlsZSBlbWFpbCBvZmZsaW5lX2FjY2VzcyJ9.ioYyBowIxvf71wejvQ1qkKxg6E0Kg4PU9QXFaKIJowY";
//		String refreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJlNTJmMmMxYi01N2Y1LTQxYjEtOGJkZS05MjA1OGFjZjQ1YjUifQ.eyJpYXQiOjE2MjQ0NTM5MDQsImp0aSI6ImNjMTVhZTlmLWJiNDAtNGQ1Zi04NTNhLTIyMGYyZjc1NzZjYSIsImlzcyI6Imh0dHBzOi8vbGl2ZS5ldXJvcGVhbi1sYW5ndWFnZS1ncmlkLmV1L2F1dGgvcmVhbG1zL0VMRyIsImF1ZCI6Imh0dHBzOi8vbGl2ZS5ldXJvcGVhbi1sYW5ndWFnZS1ncmlkLmV1L2F1dGgvcmVhbG1zL0VMRyIsInN1YiI6ImE1N2U3NGI3LTVjNGEtNGJjOC04NmI1LWEyMWJmNDIzZTkxYyIsInR5cCI6Ik9mZmxpbmUiLCJhenAiOiJlbGctb29iIiwic2Vzc2lvbl9zdGF0ZSI6IjBjYzQzNmI5LTdjMGMtNDhhMy1iMGZiLWQxZjUxMDViZDMxYSIsInNjb3BlIjoiRUxHLXByb2ZpbGUgcHJvZmlsZSBlbWFpbCBvZmZsaW5lX2FjY2VzcyJ9.ioYyBowIxvf71wejvQ1qkKxg6E0Kg4PU9QXFaKIJowY";
		etp.getRefreshToken(refreshToken);
		
		
		
//		String srvId = "487";
//		//String url = "https://accounts.google.com/ServiceLoginAuth";
//		String url = "https://live.european-language-grid.eu/auth/realms/ELG/protocol/openid-connect/auth?client_id=react-client&redirect_uri=https%3A%2F%2Flive.european-language-grid.eu%2Fcatalogue%2F%23%2Fsearch%2FASR&state=ae0215c5-9da8-48f4-8c23-fd4a3403a78b&response_mode=fragment&response_type=code&scope=openid&nonce=387a852e-e9d8-4b33-8711-0b84423a88cc";
//		String url3 = "https://live.european-language-grid.eu/auth/realms/ELG/login-actions/authenticate?session_code=Q3BWflaQ_0AJyj8MuLpD7UEiPoUQw-H7fCojq9iwtV0&execution=9060cd0f-add4-435b-b03c-ec7bcec5f277&client_id=angular-frontend&tab_id=ELPYIiwKR7E";
//		String url2 = "https://live.european-language-grid.eu/auth/realms/ELG/login-actions/authenticate?session_code=hdPyeivoHBIy6QcLLRTLc7BaYNClXG7NWcBzuh0efIw&execution=9060cd0f-add4-435b-b03c-ec7bcec5f277&client_id=react-client&tab_id=j9qDZbzq294";
//		String gmail = "https://live.european-language-grid.eu/catalogue/#/resource/service/tool/"+srvId;
//
//		ELGTokenParser http = new ELGTokenParser();
//
//		// make sure cookies is turn on
//		CookieHandler.setDefault(new CookieManager());
//
//		// 1. Send a "GET" request, so that you can extract the form's data.
////		String page = http.GetPageContent(url);
////		String postParams = http.getFormParams(page, "username@gmail.com", "password");
////		String postParams = http.getFormParams(page, "julian.moreno_schneider@dfki.de", "passwd");
//
//		System.setProperty("webdriver.gecko.driver", "/Users/julianmorenoschneider/Downloads/gecko/geckodriver_macos");
//		WebDriver driver = new FirefoxDriver();
//	    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
//
//	    driver.get(url);
//	    driver.findElement(By.id("username")).sendKeys("email");
//	    driver.findElement(By.id("password")).sendKeys("password");
//	    //driver.findElement(By.id("submtLogIn")).click();
//	    
////	    System.out.println("SLEEPING for submitting");
////	    Thread.sleep(5000);
//	    driver.findElement(By.id("kc-login")).submit();
//
//	    Thread.sleep(3000);
//	    Set<Cookie> cooks = driver.manage().getCookies();
//	    driver.navigate().to(gmail);
//	    
//	    for (Cookie cookie : cooks) {
//		    driver.manage().addCookie(cookie);
//		}
////	    driver.get(gmail);
////	    System.out.println("SLEEPING getting website");
//	    Thread.sleep(5000);
//	    String str = driver.getPageSource();
//////	    driver.close();
//	    System.out.println(str);
//
//	    String token = "";
//		Document doc = Jsoup.parse(str);
//		Elements codes = doc.select("code");
//		//codes.forEach(cd -> System.out.println("code: " + cd.html()));
//		for (Element element : codes) {
//			if(element.html().startsWith("curl")) {
//				
//			}
//			else {
//				token = element.html();
//			}
//		}
//
//		driver.close();
	}

	public String getELGServiceToken(String srvId) throws Exception {
		String url = "https://live.european-language-grid.eu/auth/realms/ELG/protocol/openid-connect/auth?client_id=react-client&redirect_uri=https%3A%2F%2Flive.european-language-grid.eu%2Fcatalogue%2F%23%2Fsearch%2FASR&state=ae0215c5-9da8-48f4-8c23-fd4a3403a78b&response_mode=fragment&response_type=code&scope=openid&nonce=387a852e-e9d8-4b33-8711-0b84423a88cc";
//		String url3 = "https://live.european-language-grid.eu/auth/realms/ELG/login-actions/authenticate?session_code=Q3BWflaQ_0AJyj8MuLpD7UEiPoUQw-H7fCojq9iwtV0&execution=9060cd0f-add4-435b-b03c-ec7bcec5f277&client_id=angular-frontend&tab_id=ELPYIiwKR7E";
//		String url2 = "https://live.european-language-grid.eu/auth/realms/ELG/login-actions/authenticate?session_code=hdPyeivoHBIy6QcLLRTLc7BaYNClXG7NWcBzuh0efIw&execution=9060cd0f-add4-435b-b03c-ec7bcec5f277&client_id=react-client&tab_id=j9qDZbzq294";
		String gmail = "https://live.european-language-grid.eu/catalogue/#/resource/service/tool/"+srvId;

		System.setProperty("webdriver.gecko.driver", "/Users/julianmorenoschneider/Downloads/gecko/geckodriver_macos");
		WebDriver driver = new FirefoxDriver();
	    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	    driver.get(url);
	    driver.findElement(By.id("username")).sendKeys("julian.moreno_schneider@dfki.de");
	    driver.findElement(By.id("password")).sendKeys("(Juli22)");
	    driver.findElement(By.id("kc-login")).submit();

	    Thread.sleep(3000);
	    Set<Cookie> cooks = driver.manage().getCookies();
	    driver.navigate().to(gmail);	    
	    for (Cookie cookie : cooks) {
		    driver.manage().addCookie(cookie);
		}
	    Thread.sleep(5000);
	    String str = driver.getPageSource();
	    String token = "";
		Document doc = Jsoup.parse(str);
		Elements codes = doc.select("code");
		for (Element element : codes) {
			if(element.html().startsWith("curl")) {	
			}
			else {
				token = element.html();
			}
		}
		driver.close();
		return token;
	}
	
	public String getRefreshToken(String refreshToken) {

		try {
			String LIVE_DOMAIN = "https://live.european-language-grid.eu";
			String tokenUrl = LIVE_DOMAIN+"/auth/realms/ELG/protocol/openid-connect/token";
//	        String sData = "{\"grant_type\": \"refresh_token\", \"client_id\": \"elg-oob\", \"refresh_token\": "+refreshToken+"}";
//			String urlParameters  = "param1=data1&param2=data2&param3=data3";
//			String urlParameters  = "grant_type=refresh_token&client_id=elg-oob&refresh_token=eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJlNTJmMmMxYi01N2Y1LTQxYjEtOGJkZS05MjA1OGFjZjQ1YjUifQ.eyJpYXQiOjE2MjQ0NTM5MDQsImp0aSI6ImNjMTVhZTlmLWJiNDAtNGQ1Zi04NTNhLTIyMGYyZjc1NzZjYSIsImlzcyI6Imh0dHBzOi8vbGl2ZS5ldXJvcGVhbi1sYW5ndWFnZS1ncmlkLmV1L2F1dGgvcmVhbG1zL0VMRyIsImF1ZCI6Imh0dHBzOi8vbGl2ZS5ldXJvcGVhbi1sYW5ndWFnZS1ncmlkLmV1L2F1dGgvcmVhbG1zL0VMRyIsInN1YiI6ImE1N2U3NGI3LTVjNGEtNGJjOC04NmI1LWEyMWJmNDIzZTkxYyIsInR5cCI6Ik9mZmxpbmUiLCJhenAiOiJlbGctb29iIiwic2Vzc2lvbl9zdGF0ZSI6IjBjYzQzNmI5LTdjMGMtNDhhMy1iMGZiLWQxZjUxMDViZDMxYSIsInNjb3BlIjoiRUxHLXByb2ZpbGUgcHJvZmlsZSBlbWFpbCBvZmZsaW5lX2FjY2VzcyJ9.ioYyBowIxvf71wejvQ1qkKxg6E0Kg4PU9QXFaKIJowY";
			String urlParameters  = "grant_type=refresh_token&client_id=elg-oob&refresh_token=eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJlNTJmMmMxYi01N2Y1LTQxYjEtOGJkZS05MjA1OGFjZjQ1YjUifQ.eyJpYXQiOjE2MzMzNTM1NjUsImp0aSI6IjQxZWQ5NzRlLWExMzgtNDFiMi1iZmFlLWI3Y2M2ZjA2ZTY0MyIsImlzcyI6Imh0dHBzOi8vbGl2ZS5ldXJvcGVhbi1sYW5ndWFnZS1ncmlkLmV1L2F1dGgvcmVhbG1zL0VMRyIsImF1ZCI6Imh0dHBzOi8vbGl2ZS5ldXJvcGVhbi1sYW5ndWFnZS1ncmlkLmV1L2F1dGgvcmVhbG1zL0VMRyIsInN1YiI6ImE1N2U3NGI3LTVjNGEtNGJjOC04NmI1LWEyMWJmNDIzZTkxYyIsInR5cCI6Ik9mZmxpbmUiLCJhenAiOiJlbGctb29iIiwic2Vzc2lvbl9zdGF0ZSI6IjQ5MDAwZGMzLTE1NWQtNDI3NC04MzM5LTRlZGE0YTQzNmRjZCIsInNjb3BlIjoiRUxHLXByb2ZpbGUgcHJvZmlsZSBlbWFpbCBvZmZsaW5lX2FjY2VzcyJ9.TkDgLma1ogCn5WjAgCZqbAPSdKmrjcefc-SA1AgiDWQ";
			byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
			int postDataLength = postData.length;
			URL url = new URL( tokenUrl );
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();           
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
			conn.setRequestProperty("charset", "utf-8");
			conn.setRequestProperty("Content-Length", Integer.toString(postDataLength ));
			conn.setUseCaches(false);
			try(DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
				wr.write( postData );
				wr.flush();
				wr.close();
			}
			int responseCode = conn.getResponseCode();
			System.out.println("RESPONSE: "+responseCode);
			System.out.println("\nSending 'POST' request to URL : " + url);
//			System.out.println("Post parameters : " + postParams);
//			System.out.println("Response Code : " + responseCode);	
			BufferedReader in =
					new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			JSONObject json2 = new JSONObject(response.toString());
//			System.out.println(json2.toString(1));
//			System.out.println(response.toString());
			if(json2.has("access_token")) {
				return json2.getString("access_token");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private WebDriver driver;

	public void setup() {
	    this.driver = new FirefoxDriver();
	    this.driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	protected void next(String url, List<String> argUrlsList) {
	    this.driver.get(url);
	    String htmlContent = this.driver.getPageSource();
	}
	
	private void sendPost(String url, String postParams) throws Exception {

		URL obj = new URL(url);
		conn = (HttpsURLConnection) obj.openConnection();

		// Acts like a browser
		conn.setUseCaches(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Host", "accounts.google.com");
		conn.setRequestProperty("User-Agent", USER_AGENT);
		conn.setRequestProperty("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		for (String cookie : this.cookies) {
			conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
		}
		conn.setRequestProperty("Connection", "keep-alive");
		conn.setRequestProperty("Referer", "https://accounts.google.com/ServiceLoginAuth");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", Integer.toString(postParams.length()));

		conn.setDoOutput(true);
		conn.setDoInput(true);

		// Send post request
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.writeBytes(postParams);
		wr.flush();
		wr.close();

		int responseCode = conn.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + postParams);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in =
				new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		// System.out.println(response.toString());

	}

	private String GetPageContent(String url) throws Exception {

		URL obj = new URL(url);
		conn = (HttpsURLConnection) obj.openConnection();

		// default is GET
		conn.setRequestMethod("GET");

		conn.setUseCaches(false);

		// act like a browser
		conn.setRequestProperty("User-Agent", USER_AGENT);
		conn.setRequestProperty("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		if (cookies != null) {
			for (String cookie : this.cookies) {
				conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
			}
		}
		int responseCode = conn.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in =
				new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// Get the response cookies
		setCookies(conn.getHeaderFields().get("Set-Cookie"));

		return response.toString();

	}

	public String getFormParams(String html, String username, String password)
			throws UnsupportedEncodingException {

		System.out.println("Extracting form's data...");

		Document doc = Jsoup.parse(html);

		// Google form id
		Element loginform = doc.getElementById("kc-form-login");
		Elements inputElements = loginform.getElementsByTag("input");
		List<String> paramList = new ArrayList<String>();
		for (Element inputElement : inputElements) {
			String key = inputElement.attr("name");
			String value = inputElement.attr("value");

			if (key.equals("username"))
				value = username;
			else if (key.equals("password"))
				value = password;
			else if (key.equals("rememberMe"))
				value = "false";
			paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
		}

		// build parameters list
		StringBuilder result = new StringBuilder();
		for (String param : paramList) {
			if (result.length() == 0) {
				result.append(param);
			} else {
				result.append("&" + param);
			}
		}
		return result.toString();
	}

	public List<String> getCookies() {
		return cookies;
	}

	public void setCookies(List<String> cookies) {
		this.cookies = cookies;
	}


}

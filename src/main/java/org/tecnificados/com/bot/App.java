package org.tecnificados.com.bot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tecnificados.com.bot.util.Messages;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class App 
{	private static final Logger log = LoggerFactory.getLogger(App.class);
	
	private static JSONParser parser=new JSONParser();
	
	private static JSONArray starWars=new JSONArray();
	private static Twitter twitter = null;
	
	private static void configuration() {
		Properties prop = new Properties();
		Properties twitterProp = new Properties();
    	try 
    	{
    		InputStream input = new FileInputStream(Constant.CONF_PROPERTIES);
    		prop.load(input);    	         	    
    	} 
    	catch (IOException ex) {
    	    log.error(Messages.getString("App.3"),ex); 
    	}
    	
    	try 
    	{
    		InputStream input = new FileInputStream(Constant.TWITTER_PROPERTIES);
    		twitterProp.load(input);    	         	    
    	} 
    	catch (IOException ex) {
    	    log.error(Messages.getString("twitterConf.ko"),ex); 
    	    return;
    	}
    	
    	
    	ConfigurationBuilder twitterConfigurationBuilder = new ConfigurationBuilder();
    	twitterConfigurationBuilder.setDebugEnabled(true)
    	  .setOAuthConsumerKey(twitterProp.getProperty("oauth.consumerKey"))
    	  .setOAuthConsumerSecret(twitterProp.getProperty("oauth.consumerSecret"))
    	  .setOAuthAccessToken(twitterProp.getProperty("oauth.accessToken"))
    	  .setOAuthAccessTokenSecret(twitterProp.getProperty("oauth.accessTokenSecret"));
    	
    	TwitterFactory tf = new TwitterFactory(twitterConfigurationBuilder.build());
    	twitter = tf.getInstance();
	}
	
	
	public static String createTweet(String tweet) throws TwitterException {
		String statusText="";
	    if (twitter!=null)
	    {
	    	Status status = twitter.updateStatus(tweet);
	    	statusText= status.getText();
	    }else {
	    	log.info(Messages.getString("twitter.init.ko"));
	    }
	    return statusText;
	}
	
    public static void main( String[] args )
    {    	
    	Locale.setDefault(new Locale("es_ES"));
    
    	
    	log.info(Messages.getString("App.5")); 
    	    	  	
    	configuration();    	
    	
    	String content="";
    	
        try {
        	content = FileUtils.readFileToString(new File(Constant.swFilePath),Constant.UTF_8);
        	log.info(Messages.getString("fichero.sw.ok"));        	
			
		} catch (IOException e) {
			log.error(Messages.getString("fichero.sw.ko"),e); 
			return;
		}
       
        
        try {
        	JSONObject frases= (JSONObject) parser.parse(content);
        	starWars=(JSONArray) frases.get("frases");
		} catch (ParseException e) {
			log.error(Messages.getString("fichero.sw.parser.ko"),e); 
			return;
		}
        
        log.info(Messages.getString("frases.sw")+" "+starWars.size()); 
        
        try {
			createTweet("Tweet inicial automatizado de prueba");
		} catch (TwitterException e) {
			log.error("Error twiteando:",e);
		}
    }

	
}

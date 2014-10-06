package twitter_Augur;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class Client_Builder {

	Twitter twitter;
	ConfigurationBuilder configBuilder;

	public Client_Builder() {

		configBuilder = new ConfigurationBuilder();
		configBuilder.setDebugEnabled(true);
		configBuilder.setOAuthConsumerKey("IEYRolf3RsQaWWkC25O2PokCR");
		configBuilder
				.setOAuthConsumerSecret("I4GlsuPLsMQW8Ee5ZRMCIEeLgl9uunyDYWGBpuTBQopuTBybaU");
		configBuilder
				.setOAuthAccessToken("46418038-njLMoyIZwQyFJJHaEVWST7phWz91CQ79nm5b48lLE");
		configBuilder
				.setOAuthAccessTokenSecret("rc9ZgKLq7gblaAYJIswvAX4VZNfdGVBefvXDUWk0XcNla");
		// use the ConfigBuilder.build() method and pass the result to the
		// TwitterFactory
		TwitterFactory tf = new TwitterFactory(configBuilder.build());
		// you can now get authenticated instance of Twitter object.
		twitter = tf.getInstance();
	}
}

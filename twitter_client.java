//https://twittercommunity.com/t/pagination-in-search-1-1/10228
//https://dev.twitter.com/rest/tools/console
//https://dev.twitter.com/rest/public/search
//https://dev.twitter.com/rest/reference/get/search/tweets
//http://stackoverflow.com/questions/23341215/extracting-tweets-of-a-specific-hashtag-using-twitter4j

package twitter_Augur;

import twitter4j.*;

public class twitter_client extends Client_Builder {
	public void getReview(String movie_name) throws TwitterException {
		Query query = new Query("#" + movie_name);
		QueryResult result = twitter.search(query);
		System.out.println(result);
		for (Status status : result.getTweets()) {
			System.out.println(status.getText());
		}
	}

	public static void main(String[] args) {
		twitter_client t = new twitter_client();
		try {
			t.getReview("Modi");
		} catch (TwitterException x) {

		}
	}

}

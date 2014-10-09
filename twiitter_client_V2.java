//https://twittercommunity.com/t/pagination-in-search-1-1/10228
//https://dev.twitter.com/rest/tools/console
//https://dev.twitter.com/rest/public/search
//https://dev.twitter.com/rest/reference/get/search/tweets
//http://stackoverflow.com/questions/23341215/extracting-tweets-of-a-specific-hashtag-using-twitter4j

package twitter_Augur;

import twitter4j.*;
import java.util.*;

public class twitter_client extends Client_Builder {
	public QueryResult getReview(String movie_name,long max_id) throws TwitterException {
		Query query = new Query( movie_name + " movie");
		query.setCount(100);
		query.setLang("en");
		if (max_id != -1)
			query.setMaxId(max_id);
		//query.
		QueryResult result = twitter.search(query);
		return result;
		/*for (Status status : result.getTweets()) {
			System.out.println(status.getText());
			System.out.println("# " + status.getId());
		}
		
		int size = result.getTweets().size();
		Status last_status =  result.getTweets().get(size-1);
		System.out.println(last_status.getId());
		System.out.println(result.getMaxId());
		System.out.println(result.getSinceId());*/
	}
	
	public List<String> extractTweets(QueryResult result)
	{
		List <String> temp = new LinkedList<String>();
		for (Status status : result.getTweets()) 
		{
			temp.add(status.getText());
		}
		return temp;
	}

	public List<String> getAllReviews(String movie_name) throws TwitterException
	{
		QueryResult initial = getReview(movie_name,-1);
		List<String> reviews = new LinkedList<String>();
		int size = initial.getTweets().size();
		Status last_status =  initial.getTweets().get(size-1);
		long last_id = last_status.getId();
		long max_id = last_id;
		int count = 1;
		
		reviews.addAll(extractTweets(initial));
		while (size > 99 && count < 3)
		{
			initial =  getReview(movie_name,max_id);
			size = initial.getTweets().size();
			last_status =  initial.getTweets().get(size-1);
			last_id = last_status.getId();
			max_id = last_id;
			reviews.addAll(extractTweets(initial));
			count ++;
		}
		
		return reviews;
		
		
		
	}
	public static void main(String[] args) {
		twitter_client t = new twitter_client();
		List<String> reviews = new LinkedList<String>();
		try {
			 reviews = t.getAllReviews("gone girl");
		} catch (TwitterException x) {

		}
		System.out.println(reviews.size());
		for (int i = 0;i<reviews.size();i++)
		{
			System.out.println(reviews.get(i));
		}
	}

}

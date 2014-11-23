//https://twittercommunity.com/t/pagination-in-search-1-1/10228
//https://dev.twitter.com/rest/tools/console
//https://dev.twitter.com/rest/public/search
//https://dev.twitter.com/rest/reference/get/search/tweets
//http://stackoverflow.com/questions/23341215/extracting-tweets-of-a-specific-hashtag-using-twitter4j

package twitter_Augur;


import twitter4j.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class twitter_client extends Client_Builder {
	
   	public static final String TEXT_DATA = "T";
	public static final String NUMERIC_DATA = "N";
	public static final String TWITTER = "T";
	public static final String CRITIC = "C";
	public static final String AUDIENCE = "A";
	public static final String FILENAME = "Twitter.txt";
	
	public QueryResult getReview(String movie_name,long max_id) throws TwitterException {
		Query query = new Query( movie_name + " movie" +" -filter:retweets -filter:links -filter:replies -filter:images");
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
		while (size > 99 && count < 30)
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
	
	   public  String generateOutputFile(String movie_name) throws Exception
	      {
	    	String Filename = movie_name;
			File inputFile = new File(Filename);
			FileWriter fileWriter;
			List <String> reviews = getAllReviews( movie_name);
			String commentMeta = movie_name + "\t" + TEXT_DATA + 
					 TWITTER  + AUDIENCE + " ";	
			try {

				inputFile.createNewFile();
				fileWriter = new FileWriter(inputFile);
				BufferedWriter bw = new BufferedWriter(fileWriter);
				for (String review : reviews) {
					String data = commentMeta + review;
					bw.write(data);
					bw.newLine();
				}
				for (String review : reviews) {
					String data = commentMeta + review;
					bw.write(data);
					bw.newLine();
				}
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return Filename;
		}
	public static void main(String[] args) throws Exception
	{
		if (args.length < 3)
    		{
			System.out.println("Usage:javac -c jar <bucket-name> <folder> <movie1> <movie2>...<movie-n>");
			throw new UnsupportedOperationException();
    		}
		twitter_client t = new twitter_client();
		String bucket_name = args[0];
    	S3Utility s3client = new S3Utility(bucket_name,args[1]);
    	int arg_count = 2;
    	String filename;
    	List<String> movies = new ArrayList<String>();
    	while (arg_count < args.length)
    	{
    		String temp_movie = args[arg_count].replace("_", " ");//for anirrudha's code.
    		movies.add(temp_movie);
    		arg_count++;
    	}
    	//System.out.println((movies.toString()));
    	for (int i = 0;i < movies.size();i++)
    	{
    		Thread.sleep(100000);//sleep for 100 seconds, so that API limit is not violated.
    		filename =   t.generateOutputFile(movies.get(i));
    		s3client.uploadFile(filename);
   
    	}
	}

}

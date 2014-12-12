//https://twittercommunity.com/t/pagination-in-search-1-1/10228
//https://dev.twitter.com/rest/tools/console
//https://dev.twitter.com/rest/public/search
//https://dev.twitter.com/rest/reference/get/search/tweets
//http://stackoverflow.com/questions/23341215/extracting-tweets-of-a-specific-hashtag-using-twitter4j

package twitter_Augur;
import twitter4j.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
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
		Query query = new Query( movie_name + "-filter:retweets -filter:links -filter:replies -filter:images");
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

	public List<String> getAllReviews(String movie_name) throws TwitterException, InterruptedException
	{
		QueryResult initial = getReview(movie_name,-1);
		List<String> reviews = new LinkedList<String>();
		int size = initial.getTweets().size();
		System.out.println("initial size "+size);
		Status last_status;
		if (size > 0)
		{
			 last_status =  initial.getTweets().get(size-1);
			 long last_id = last_status.getId();
				long max_id = last_id;
				int count = 1;
				reviews.addAll(extractTweets(initial));
				while (size > 99 && count < 5)
				{
					Thread.sleep(1000);//sleep necessary so that requests/sec limit is not violated.
					initial =  getReview(movie_name,max_id);
					size = initial.getTweets().size();
					if (size > 0) {
						last_status = initial.getTweets().get(size - 1);
						last_id = last_status.getId();
						max_id = last_id;
						reviews.addAll(extractTweets(initial));
						System.out.println("count: " + count
								+ " comments retrieved: " + reviews.size());
					}
					count ++;
				}
				System.out.println("Total calls made for " + movie_name +" " + count);
		}
		
		return reviews;
			
	}
	
	   public  String generateOutputFile(String movie_name) throws Exception
	      {
	    	String Filename = movie_name;
			File inputFile = new File(Filename);
			FileWriter fileWriter;
			List<String> reviews;
			try {
				reviews = getAllReviews( movie_name);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				reviews = null;
				e1.printStackTrace();
			}
			if (reviews.isEmpty() != true && (!reviews.equals(null))) {
				String commentMeta = movie_name + "\t" + TEXT_DATA + TWITTER
						+ AUDIENCE + " ";
				try {

					inputFile.createNewFile();
					fileWriter = new FileWriter(inputFile);
					BufferedWriter bw = new BufferedWriter(fileWriter);
					for (String review : reviews) {
						review = review.replaceAll("\n", " ").replaceAll("\r", " ");
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
			else
				return "blank_file";
		}
	public static void main(String[] args) throws Exception
	{
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
			Thread.sleep(26000);//sleep for 26 seconds, so that API limit is not violated.
			filename =   t.generateOutputFile(movies.get(i));
			if (filename.equalsIgnoreCase("blank_file") != true)
				{
					System.out.println("initiating upload for "+movies.get(i));
					s3client.uploadFile(filename);
				}
			else
			{
				System.out.println("no tweets found for "+movies.get(i));
			}
		}
	}

}



////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
//twitter_client t = new twitter_client();
//String path = args[0];
//String bucketname = args[1];
//String folder_name = args[2];
//String CurrentLine;
//String filename;
//S3Utility s3client = new S3Utility(bucketname,folder_name);
//String[] parts = {"1","2"};
//List <String> movielist = new LinkedList<String>();
//FileReader fr = new FileReader(path);
//BufferedReader br = new BufferedReader(fr);
//while ((CurrentLine = br.readLine()) != null) {
//	parts = CurrentLine.split("\t");
//	movielist.add(parts[0]);
//}
//System.out.println(movielist);
//for (int i = 0;i<movielist.size();i++)
//{
//	Thread.sleep(26000);//sleep for 26 seconds, so that API limit is not violated.
//	filename =   t.generateOutputFile(movielist.get(i));
//	if (filename.equalsIgnoreCase("blank_file") != true)
//		{
//			System.out.println("initiating upload for "+movielist.get(i));
//			s3client.uploadFile(filename);
//		}
//	else
//	{
//		System.out.println("no tweets found for "+movielist.get(i));
//	}
//}
///*if (args.length < 3)
//	{
//	System.out.println("Usage:javac -c jar <bucket-name> <folder> <movie1> <movie2>...<movie-n>");
//	throw new UnsupportedOperationException();
//	}
//twitter_client t = new twitter_client();
//String bucket_name = args[0];
//S3Utility s3client = new S3Utility(bucket_name,args[1]);
//int arg_count = 2;
//String filename;
//List<String> movies = new ArrayList<String>();
//while (arg_count < args.length)
//{
//	String temp_movie = args[arg_count].replace("_", " ");//for anirrudha's code.
//	movies.add(temp_movie);
//	arg_count++;
//}
////System.out.println((movies.toString()));
//for (int i = 0;i < movies.size();i++)
//{
//	Thread.sleep(60000);//sleep for 60 seconds, so that API limit is not violated.
//	filename =   t.generateOutputFile(movies.get(i));
//	if (filename.equalsIgnoreCase("blank_file") != true)
//	s3client.uploadFile(filename);
//}*/

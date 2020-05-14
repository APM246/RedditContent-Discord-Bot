package me.name;

import me.name.exceptions.SubredditDoesNotExistException;
import net.dean.jraw.*;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.*;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.pagination.BarebonesPaginator;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.pagination.Paginator;
import net.dean.jraw.references.SubredditReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RedditComments
{
    private UserAgent userAgent;
    private Credentials credentials;
    private NetworkAdapter adapter;
    private RedditClient reddit;

    public RedditComments()
    {
        userAgent = new UserAgent("desktop", "net.dean.awesomescript", "v0.1", "APM369");
        credentials = Credentials.script("APM369", "lordarun", "VBouI4DFgiC2Mw", "5OzSdoDKXPSw2K1F2x95zSWNg84");
        adapter = new OkHttpNetworkAdapter(userAgent);
        reddit = OAuthHelper.automatic(adapter, credentials);
    }

    public String[] getPhotoLink(String subredditName) throws SubredditDoesNotExistException
    {
        try 
        {
            DefaultPaginator<Submission> posts = reddit.subreddit(subredditName).posts().build();
            List<String[]> images = new ArrayList<>();
            Listing<Submission> photo_list = posts.next();

            // Throw error if subreddit does not exist
            if (photo_list.isEmpty()) throw new SubredditDoesNotExistException();

            for (Submission s : photo_list) {
                if (!s.isSelfPost() && (s.getUrl().contains("i.imgur.com") || s.getUrl().contains("i.redd.it")))
                {
                    images.add(new String[] {s.getUrl(), "https://www.reddit.com/" + s.getPermalink(), s.getTitle()});
                }
            }

            if (images.size() == 0) return null;
            int random_number = (int) (images.size()*Math.random());
            return images.get(random_number);
        }

        catch (Exception e)
        {
            throw new SubredditDoesNotExistException();
        }
    }

    public String[] getGIFLink(String subredditname) throws SubredditDoesNotExistException
    {
        try 
        {
            DefaultPaginator<Submission> posts = reddit.subreddit(subredditname).posts().build();
            List<String[]> gifs = new ArrayList<>();
            Listing<Submission> page = posts.next();

            // Throw error if subreddit name is wrong
            if (page.isEmpty()) throw new SubredditDoesNotExistException();

            for (Submission s: page)
            {
                if (!s.isSelfPost() && (s.getUrl().contains("gfycat.com") || s.getUrl().contains(".gifv")))
                {
                    gifs.add(new String[] {s.getUrl(), "https://www.reddit.com/" + s.getPermalink(), s.getTitle()});
                }
            }
        
            if (gifs.size() == 0) return null;
            int random_number = (int) (gifs.size()*Math.random());
            return gifs.get(random_number);
        }

        catch (Exception e)
        {
            throw new SubredditDoesNotExistException();
        }
    }


    public String findComment(String subredditName) throws SubredditDoesNotExistException
    {
        try
        {
            SubredditReference subreddit = reddit.subreddit(subredditName);
            BarebonesPaginator.Builder<Comment> comments = subreddit.comments();
            BarebonesPaginator<Comment> built = comments.build();
            List<Comment> commentslist = built.accumulateMerged(1);

            // Throw error if subreddit does not exist
            if (commentslist.isEmpty()) throw new SubredditDoesNotExistException();

            return commentslist.get(0).getBody();
        }

        catch (NetworkException e)
        {
            throw new SubredditDoesNotExistException();
        }
    }

    public String searchSubreddits(String searchwords) 
    {
        try
        {
            List<SubredditSearchResult> list = reddit.searchSubredditsByName(searchwords);
            if (list.isEmpty()) throw new SubredditDoesNotExistException();

            String result= "";

            for (SubredditSearchResult subreddit: list)
            {
                result += "\n" + subreddit.getName();
            }

            return result;
        }

        catch (Exception e)
        {
            return "No subreddits could be found based on this search query";
        }
    }

    public String[] guessCity() 
    {
        try 
        {
            DefaultPaginator<Submission> posts = reddit.subreddit("CityPorn").posts().limit(Paginator.RECOMMENDED_MAX_LIMIT).build();
            List<Submission> submissions = new ArrayList<>();
            Listing<Submission> page = posts.next();

            for (Submission s: page)
            {
                if (!s.isSelfPost() && s.getUrl().contains("i.redd.it"))
                {
                    submissions.add(s);
                }
            }

            int random_number = (int) (submissions.size()*Math.random());
            Submission question = submissions.get(random_number);
            return new String[] {question.getUrl(), question.getTitle().replace(",", "").replace(".", "").toLowerCase()};
        }
        catch (Exception e) {return null;}
    }

    public static void main(String[] args) throws Exception
    {
        RedditComments main = new RedditComments();
        //System.out.println(main.findComment("pcmasterrace"));
        //System.out.println(main.getPhotoLink("earthporn"));
        //System.out.println(main.searchSubreddits("nvidia 2070"));
        System.out.println(Arrays.toString(main.guessCity()));
    }
}
package me.name;

import net.dean.jraw.*;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.*;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.pagination.BarebonesPaginator;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.references.SubredditReference;

import java.util.ArrayList;
import java.util.Iterator;
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

    public String getPhotoLink(String subredditName)
    {
        DefaultPaginator<Submission> posts = reddit.subreddit(subredditName).posts().build();

        List<String> images = new ArrayList<String>();
        Listing<Submission> photo_list = posts.next();
        for (Submission s : photo_list) {
            if (!s.isSelfPost() && s.getUrl().contains("i.imgur.com"))
            {
                images.add(s.getUrl());
            }
            else if (!s.isSelfPost() && s.getUrl().contains("i.redd.it"))
            {
                images.add(s.getUrl());
            }
        }

        if (images.size() == 0) return "This subreddit does not contain image-based posts";
        int random_number = (int) (images.size()*Math.random());
        return images.get(random_number);
    }

    public String getGIFLink(String subredditname)
    {
        DefaultPaginator<Submission> posts = reddit.subreddit(subredditname).posts().build();
        List<String> gifs = new ArrayList<String>();
        
            for (Listing<Submission> page: posts)
            {
                List<Submission> submissions = page.getChildren();

                for (Submission s: submissions)
                {
                    if (!s.isSelfPost() && s.getUrl().contains("gfycat.com"))
                    {
                        gifs.add(s.getUrl());
                    }
                    else if (!s.isSelfPost() && s.getUrl().contains(".gifv"))
                    {
                        gifs.add(s.getUrl());
                    }
                }
            }

        //if s.isNsfw()
        
        if (gifs.size() == 0) return "This subreddit does not contain gif-based posts";
        int random_number = (int) (gifs.size()*Math.random());
        return gifs.get(random_number);
    }


    public String findComment(String subredditName)
    {
        SubredditReference subreddit = reddit.subreddit(subredditName);
        BarebonesPaginator.Builder<Comment> comments = subreddit.comments();
        BarebonesPaginator<Comment> built = comments.build();
        List<Comment> commentslist = built.accumulateMerged(1);

        return commentslist.get(0).getBody();

        /*for (Comment comment: built.accumulateMerged(1))
        {
            System.out.println(comment.getBody());
        }*/
    }

    public static void main(String[] args)
    {
        RedditComments main = new RedditComments();
        //System.out.println(main.findComment("pcmasterrace"));
        //System.out.println(main.getPhotoLink("minecraft"));
        System.out.println(main.getGIFLink("RocketLeague"));
    }
}
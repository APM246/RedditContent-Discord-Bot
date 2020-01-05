package me.name.bot;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import me.name.RedditComments;


public class Bot extends ListenerAdapter
{
    public static void main(String[] args) throws LoginException
    {
        JDA api = new JDABuilder("NjUyNTY0MjQ0MDY3Mzg1MzQ1.XhFuPw.gO6pEDK0EnT214I5fpVBvUM720I").addEventListeners(new Bot()).
                setActivity(Activity.playing("type >help")).build();
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event)
    {
        if (event.getAuthor().isBot()) return;
        // don't respond to bots (including self)

        Message message = event.getMessage();
        if (message.getAuthor().isBot()) return;
        String content = message.getContentRaw();
        String[] args = content.split(" ");
        MessageChannel channel = event.getChannel();

        if (args[0].equals(">help"))
        {
            channel.sendMessage(">comment [subreddit name] \n Displays newest comment from that subreddit\n \n>photo [subreddit name]\n Displays a photo from that subreddit").queue();
        }

        else
        {
            try
            {
                RedditComments reddit = new RedditComments();

                if (args[0].equals(">comment"))
                {
                    String reddit_message = reddit.findComment(args[1]);
                    channel.sendMessage(reddit_message).queue();
                }
                else if (args[0].equals(">photo"))
                {
                    String url = reddit.getPhotoLink(args[1]);
                    channel.sendMessage(url).queue();
                }
            }

            catch (IndexOutOfBoundsException e)
            {
                channel.sendMessage("No such subreddit exists buddy").queue();
            }
        }
    }
}

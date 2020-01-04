package me.name.bot;

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
        JDA api = new JDABuilder("NjUyNTY0MjQ0MDY3Mzg1MzQ1.XhDrWw.gwimOIh66mXl5nZv5hRoTAMJWHA").addEventListeners(new Bot()).build();
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event)
    {
        if (event.getAuthor().isBot()) return;
        // don't respond to bots (including self)

        Message message = event.getMessage();
        String content = message.getContentRaw();
        String[] args = content.split(" ");
        MessageChannel channel = event.getChannel();


        if (args[0].equals(">comment"))
        {
            RedditComments reddit = new RedditComments();
            String reddit_message = reddit.findComment(args[1]);
            channel.sendMessage(reddit_message).queue();
        }

        else if (args[0].equals(">help"))
        {
            channel.sendMessage(">comment [subreddit name] \n Displays newest comment from that subreddit").queue();
        }

    }
}

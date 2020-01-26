package me.name.bot;

import me.name.ConfigReader;
import me.name.DadJokes;
import me.name.NewsUpdates;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import me.name.RedditComments;


public class Bot extends ListenerAdapter
{
    public static void main(String[] args) throws Exception
    {
        new JDABuilder(ConfigReader.retrieveBotToken()).addEventListeners(new Bot()).
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
            channel.sendMessage("```css\n" +
                                        ">comment [subreddit name] \n Displays newest comment from that subreddit\n \n>photo [subreddit name] \n " +
                                        "Displays a random photo from that subreddit\n \n>gif [subreddit name]\n Displays a random gif from that subreddit " +
                                        "\n\n>joke \n Generates a random Dad Joke " + "\n\n>news \n Gets latest news headline from New York Times "
                                        + "\n\n>about \n Link to Github repository " + "\n```").queue();
        }

        else
        {
            try
            {
                RedditComments reddit = new RedditComments();
                NewsUpdates newsUpdates = new NewsUpdates();

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

                else if (args[0].equals(">gif"))
                {
                    String url = reddit.getGIFLink(args[1]);
                    channel.sendMessage(url).queue();
                }

                else if (args[0].equals(">joke"))
                {
                    String joke = DadJokes.generateDadJoke();
                    channel.sendMessage(joke).queue();
                }

                else if (args[0].equals(">news"))
                {
                    String url = newsUpdates.retrieveURL(true);
                    channel.sendMessage(url).queue();
                }

                else if (args[0].equals(">news top"))
                {
                    String url = newsUpdates.retrieveURL(false);
                    channel.sendMessage(url).queue();
                }

                else if (args[0].equals(">about"))
                {
                    String url = "https://github.com/APM246/RedditContent-Discord-Bot";
                    channel.sendMessage(url).queue();
                }
            }

            catch (IndexOutOfBoundsException e)
            {
                String[] error_messages = {"No such subreddit exists buddy", "That subreddit doesn't exist...", "You spelt it wrong idiot", "Do you know how to use a keyboard?"};
                int random_number = (int) (Math.random()*error_messages.length);
                channel.sendMessage(error_messages[random_number]).queue();
            }

            catch (Exception e)
            {
                e.printStackTrace();
                channel.sendMessage("Something went wrong  :'(").queue();
            }
        }
    }
}

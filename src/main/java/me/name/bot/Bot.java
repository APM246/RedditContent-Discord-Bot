package me.name.bot;

import me.name.ConfigReader;
import me.name.DadJokes;
import me.name.NewsUpdates;
import me.name.exceptions.SubredditDoesNotExistException;
import me.name.music.Music;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;

import me.name.RedditComments;


public class Bot extends ListenerAdapter
{
    private static String read_md()
    {
        String message = "";

        try
        {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = classloader.getResourceAsStream("help.txt");
            String encoding = null;
            message = IOUtils.toString(inputStream, encoding);
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }

        return message;
    }

    private static boolean isInappropriate(String redditName)
    {
        String[] banned_list = {"gayporn","dick","demirosemawby","realscatgirls","IndiansGoneWild"};

        for (String reddit: banned_list) 
        {
            if (redditName.equals(reddit)) 
            {
                return true;
            }
        }

        return false;
    }

    public static void main(String[] args) throws Exception
    {
        //System.out.println(read_md());
        new JDABuilder(ConfigReader.retrieveBotToken()).addEventListeners(new Bot()).
                setActivity(Activity.playing("type >help")).build();
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event)
    {
        if (event.getAuthor().isBot()) return; // don't respond to bots (including self)
        Message message = event.getMessage();
        if (message.getAuthor().isBot()) return;
        String content = message.getContentRaw();
        String[] args = content.split(" ");
        MessageChannel channel = event.getChannel();

        if (args[0].equals(">help"))
        {
            channel.sendMessage(read_md()).queue();
        }

        else
        {
            try
            {
                RedditComments reddit = new RedditComments();
                NewsUpdates newsUpdates = new NewsUpdates();
                Music musicBot = new Music();

                /*if (event.getTextChannel().isNSFW())
                {
                    if (args[0].equals(">photo"))
                    {
                        String url = reddit.getPhotoLink(args[1]);
                        channel.sendMessage(url).queue();
                        return;
                    }

                    else if (args[0].equals(">gif"))
                    {
                        String url = reddit.getGIFLink(args[1]);
                        channel.sendMessage(url).queue();
                        return;
                    }
                }*/

                if (args[0].equals(">comment"))
                {
                    String reddit_message = reddit.findComment(args[1]);
                    channel.sendMessage(reddit_message).queue();
                }
                else if (args[0].equals(">photo"))
                {
                    if (isInappropriate(args[1]))
                    {
                        String special_message = 
                        "WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN?" + 
                        "WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN?" + 
                        "WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN?" + 
                        "WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN?";
                        channel.sendMessage(special_message).queue();
                    }

                    else 
                    {
                        String url = reddit.getPhotoLink(args[1]);
                        channel.sendMessage(url).queue();
                    }
                }

                else if (args[0].equals(">gif"))
                {
                    if (isInappropriate(args[1]))
                    {
                        String special_message = 
                        "WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN?" + 
                        "WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN?" + 
                        "WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN?" + 
                        "WHEN? WHEN? WHEN? WHEN? WHEN? WHEN? WHEN?";
                        channel.sendMessage(special_message).queue();
                    }   
                    
                    else 
                    {
                        String url = reddit.getGIFLink(args[1]);
                        channel.sendMessage(url).queue();
                    }
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

                else if (args[0].equals(">play"))
                {
                    args = content.split(" ", 2);
                    System.out.println(args.toString());
                    musicBot.loadAndPlay(event.getTextChannel(), musicBot.searchTermtoURL(args[1]));
                }

                else if (args[0].equals(">skip"))
                {
                    musicBot.skipTrack(event.getTextChannel());
                }

                else if (args[0].equals(">stop"))
                {
                    musicBot.kickBot();
                }
            }

            catch (SubredditDoesNotExistException e)
            {
                
                    String id = event.getAuthor().getDiscriminator();

                    if (id.equals("0998"))
                    {
                        channel.sendMessage("Unluckeeeeeeeeeeeeeeeeeee try again").queue();
                    }

                    else if (id.equals("6934"))
                    {
                        channel.sendMessage("Do they teach spelling in Serbia?").queue();
                    }

                    else if (id.equals("4360"))
                    {
                        channel.sendMessage("Shit at Rocket League and spelling").queue();
                    }
                    
                    else if (id.equals("0588"))
                    {
                        channel.sendMessage("LING LONG DING DONG TRY AGAIN").queue();
                    }

                    else if (id.equals("2201"))
                    {
                        channel.sendMessage(",d ,d ,d ,d ,d ,d ,d ,d ,d ,d").queue();
                    }

                    else if (id.equals("4469"))
                    {
                        channel.sendMessage("Shouldn't you be studying Python right now").queue();
                    }

                    else if (id.equals("8389"))
                    {
                        channel.sendMessage("You didn't spell r/raven right").queue();
                    }
                    
                    else 
                    {
                        String[] error_messages = {"Wrong spelling", "PLEASE learn how to spell", "Incorrectly spelt u cunt", "You spelt it wrong idiot",
                        "Do you know how to use a keyboard?", "You didn't spell that right :/", "Hayden ur so shit at spelling",
                        "Fuck off with that shit spelling", "Try again with the spelling"};
                        int random_number = (int) (Math.random()*error_messages.length);
                        channel.sendMessage(error_messages[random_number]).queue();
                    }
            }

            catch (Exception e)
            {
                e.printStackTrace();
                channel.sendMessage("```\nSomething went wrong.\n```").queue();
            }
        }
    }
}

package me.name.bot;

import me.name.ConfigReader;
import me.name.DadJokes;
import me.name.NewsUpdates;
import me.name.exceptions.SubredditDoesNotExistException;
import me.name.music.Music;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Footer;
import net.dv8tion.jda.api.entities.MessageEmbed.ImageInfo;
import net.dv8tion.jda.api.entities.MessageEmbed.VideoInfo;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.entities.Role;

import javax.annotation.Nonnull;

import java.awt.Color;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;

import me.name.RedditComments;


public class Bot extends ListenerAdapter
{
    private boolean isLocked = false;
    private Player player;
    private String[] output; 
    private int n_tries;

    private static String read_file(String fileName)
    {
        String message = "";

        try
        {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = classloader.getResourceAsStream(fileName);
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
        String[] banned_list = {"gayporn","dick","demirosemawby","realscatgirls", "realscatguys", "IndiansGoneWild", "balls", "manass", "ttotm", "trap", "pooping", "sounding", "tgirls"};

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
        String[] args = content.split(" ", 2);
        MessageChannel channel = event.getChannel();
        String command = args[0];

        try {
            StatsRecorder statRecorder = new StatsRecorder(); 
        if (command.equals(">help"))
        {
            channel.sendMessage(read_file("help.txt")).queue();
            statRecorder.incrementCount(command.replace(">",""));
        }

        else if (command.equals(">update"))
        {
            channel.sendMessage(read_file("update.txt")).queue();
            statRecorder.incrementCount(command.replace(">",""));
        }

        else 
        {
                RedditComments reddit = new RedditComments();
                NewsUpdates newsUpdates = new NewsUpdates();
                Music musicBot = new Music();

                if (command.equals(">game"))
                {
                    if (!isLocked) 
                    {
                        n_tries = 0;
                        player = new Player(message.getChannel().getIdLong());
                        isLocked = true;
                        output = reddit.guessCity();
                        channel.sendMessage(output[0] + "\n" + "Try and guess the name of the city or country").queue();
                        statRecorder.incrementCount(command.replace(">",""));

                    }

                    else channel.sendMessage("Game is being played in the " + channel.getName() + " channel").queue();
                }

                else if (command.equals(">guess") && isLocked)
                {
                    if (message.getChannel().getIdLong() == player.getchannelID())
                    {
                        statRecorder.incrementCount(command.replace(">",""));
                        String attempt = args[1].toLowerCase();
                        attempt = attempt.replace("the", "");
                        attempt = attempt.replace("city", "");
                        attempt = attempt.replace("town", "");
                        attempt = attempt.replace("river", "");

                        for (String word: output[1].split(" "))
                        {
                            System.out.println("attempt: " + attempt);
                            System.out.println("word: " + word);
                            for (String attempt_word: attempt.split(" "))
                            {                           
                                if (attempt_word.equals(word))
                                {
                                    channel.sendMessage("Correct! (☞ ͡° ͜ʖ ͡°)☞").queue();
                                    channel.sendMessage("The title of the post was: " + output[1]).queue();
                                    player = null;
                                    isLocked = false;
                                    return;
                                }
                            }
                        }
            
                        channel.sendMessage("Wrong (´･_･`)").queue();
                        n_tries++;
                        if (n_tries > 10) 
                        {
                            player = null;
                            isLocked = false;
                            channel.sendMessage("10 guesses are over (╯°□°)╯︵ ┻━┻").queue();
                            channel.sendMessage("The title of the post was: " + output[1]).queue();
                        }
                        
                    }

                    else channel.sendMessage("The game is not being played in this channel!");
                }

                else if (command.equals(">quit") && isLocked)
                {
                    if (message.getChannel().getIdLong() == player.getchannelID())
                    {
                        channel.sendMessage("The title of the post was: " + output[1]).queue();
                        player = null;
                        isLocked = false;
                    }
                }

                else if (command.equals(">comment"))
                {
                    String reddit_message = reddit.findComment(args[1]);
                    channel.sendMessage(reddit_message).queue();
                    statRecorder.incrementCount(command.replace(">",""));
                }
                else if (command.equals(">photo"))
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
                        statRecorder.incrementCount(command.replace(">",""));
                        if (args[1].contains("raven")) statRecorder.incrementCount(args[1]);
                        String[] photo_properties = reddit.getPhotoLink(args[1]);
                        if (photo_properties == null) channel.sendMessage("This subreddit does not contain image-based posts").queue();
                        else 
                        {
                            String clickable_link = photo_properties[1]; 
                            MessageEmbed embed = new MessageEmbed(clickable_link, photo_properties[2], null, EmbedType.valueOf("IMAGE"), null,
                            25, null, null, null, null, null, new ImageInfo(photo_properties[0], photo_properties[0],
                            MessageEmbed.EMBED_MAX_LENGTH_BOT, MessageEmbed.EMBED_MAX_LENGTH_BOT), null);
                            channel.sendMessage(embed).queue();
                        }
                    }
                }

                else if (command.equals(">gif"))
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
                        String[] gif_properties = reddit.getGIFLink(args[1]);
                        statRecorder.incrementCount(command.replace(">",""));
                        if (gif_properties == null) channel.sendMessage("This subreddit does not contain gif-based posts").queue();
                        else 
                        {
                            String clickable_link = gif_properties[1]; 
                            MessageEmbed embed = new MessageEmbed(clickable_link, gif_properties[2], gif_properties[0], EmbedType.valueOf("UNKNOWN"), null,
                            100, null, null, null, null, null, null, null);
                            channel.sendMessage(embed).queue();
                        }
                    }
                }

                else if (command.equals(">stat")) 
                {
                    statRecorder.incrementCount(command.replace(">",""));
                    channel.sendMessage("This command has been requested " + statRecorder.getCount(args[1]) + " times since May 1 2020.").queue();

                }

                else if (command.equals(">search"))
                {
                    channel.sendMessage(reddit.searchSubreddits(args[1])).queue();
                    statRecorder.incrementCount(command.replace(">",""));
                }

                else if (command.equals(">joke"))
                {
                    statRecorder.incrementCount(command.replace(">",""));
                    String joke = DadJokes.generateDadJoke();
                    channel.sendMessage(joke).queue();
                    channel.sendMessage("hi");
                }

                else if (command.equals(">news"))
                {
                    statRecorder.incrementCount(command.replace(">",""));
                    String url = newsUpdates.retrieveURL(true);
                    channel.sendMessage(url).queue();
                }

                else if (command.equals(">news top"))
                {
                    statRecorder.incrementCount(command.replace(">",""));
                    String url = newsUpdates.retrieveURL(false);
                    channel.sendMessage(url).queue();
                }

                else if (command.equals(">about"))
                {
                    statRecorder.incrementCount(command.replace(">",""));
                    String url = "https://github.com/APM246/RedditContent-Discord-Bot";
                    channel.sendMessage(url).queue();
                }

                else if (command.equals(">play"))
                {
                    statRecorder.incrementCount(command.replace(">",""));
                    System.out.println(args.toString());
                    musicBot.loadAndPlay(event.getTextChannel(), musicBot.searchTermtoURL(args[1]));
                }

                else if (command.equals(">skip"))
                {
                    statRecorder.incrementCount(command.replace(">",""));
                    musicBot.skipTrack(event.getTextChannel());
                }

                else if (command.equals(">stop"))
                {
                    statRecorder.incrementCount(command.replace(">",""));
                    musicBot.kickBot();
                }
            }
        }

            catch (SubredditDoesNotExistException e)
            {
                    String id = event.getAuthor().getDiscriminator();

                    if (id.equals("0998"))
                    {
                        channel.sendMessage("Go watch some more lectures").queue();
                    }

                    else if (id.equals("6934"))
                    {
                        channel.sendMessage("You're an okay friend").queue();
                    }

                    else if (id.equals("4360"))
                    {
                        channel.sendMessage("Learn how to aerial (and spell)").queue();
                    }
                    
                    else if (id.equals("0588"))
                    {
                        channel.sendMessage("We get it u study business").queue();
                    }

                    else if (id.equals("2201"))
                    {
                        channel.sendMessage(",d ,d ,d ,d ,d ,d ,d ,d ,d ,d").queue();
                    }

                    else if (id.equals("4469"))
                    {
                        channel.sendMessage("Haydendelap").queue();
                    }

                    else if (id.equals("8389"))
                    {
                        channel.sendMessage("You're shit at skribble and spelling").queue();
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

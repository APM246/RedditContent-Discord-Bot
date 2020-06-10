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
import net.dv8tion.jda.api.entities.MessageEmbed.ImageInfo;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

import java.io.InputStream;
import org.apache.commons.io.IOUtils;

import me.name.RedditComments;


public class Bot extends ListenerAdapter
{
    private boolean isLocked = false;
    private Player player;
    private String[] output; 
    private int n_tries;
    private StatsRecorder statRecorder;
    private CommandsManager commandsManager;

    public Bot() throws Exception
    {
        statRecorder = new StatsRecorder();
        commandsManager = new CommandsManager(new RedditComments(), new NewsUpdates(), new Music());
    }

    public static void main(String[] args) throws Exception
    {
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
                commandsManager.executeCommand(channel, args);
                Music musicBot = new Music();

                if (command.equals(">game"))
                {
                    if (!isLocked) 
                    {
                        n_tries = 0;
                        player = new Player(message.getChannel().getIdLong(), message.getChannel().getName());
                        isLocked = true;
                        // REMOVE 
                        RedditComments reddit = new RedditComments();
                        output = reddit.guessCity();
                        channel.sendMessage(output[0] + "\n" + "Try and guess the name of the city or country").queue();
                        statRecorder.incrementCount(command.replace(">",""));

                    }

                    else channel.sendMessage("Game is being played in the " + player.getChannelName() + " channel").queue();
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

                    else channel.sendMessage("Game is being played in the " + player.getChannelName() + " channel").queue();
                }

                else if (command.equals(">quit") && isLocked)
                {
                    if (message.getChannel().getIdLong() == player.getchannelID())
                    {
                        channel.sendMessage("The title of the post was: " + output[1]).queue();
                        player = null;
                        isLocked = false;
                    }

                    else channel.sendMessage("Game is being played in the " + player.getChannelName() + " channel").queue();
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

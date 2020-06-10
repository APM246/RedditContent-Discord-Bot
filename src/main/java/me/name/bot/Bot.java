package me.name.bot;

import me.name.ConfigReader;
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
import me.name.RedditComments;


public class Bot extends ListenerAdapter
{
    private CommandsManager commandsManager;

    public Bot() throws Exception
    {
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

        try 
        { 
            commandsManager.executeCommand(channel, args, event);
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

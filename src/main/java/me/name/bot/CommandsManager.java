package me.name.bot;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import me.name.DadJokes;
import me.name.NewsUpdates;
import me.name.RedditComments;
import me.name.music.Music;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.ImageInfo;

public class CommandsManager {
    
    final private RedditComments reddit;
    final private NewsUpdates newsUpdates;
    final private Music musicBot;
    private Player player;
    private MessageChannel channel;
    private StatsRecorder statRecorder;
    private String command;
    private String args;

    public CommandsManager(RedditComments reddit, NewsUpdates newsUpdates, Music musicBot) throws Exception 
    {
        this.reddit = reddit;
        this.newsUpdates = newsUpdates;
        this.musicBot = musicBot;
        statRecorder = new StatsRecorder();
    }

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
        String[] banned_list = {"dadsgonewild", "menkissing", "gayporn","dick","demirosemawby","realscatgirls", "realscatguys", "IndiansGoneWild", "balls", "manass", "dilf", "ttotm", "trap", "pooping", "sounding", "tgirls"};

        for (String reddit: banned_list) 
        {
            if (redditName.equals(reddit)) 
            {
                return true;
            }
        }

        return false;
    }
   
    public void executeCommand(MessageChannel channel, String[] input) throws Exception
    {
        this.channel = channel;
        command = input[0];
        args = null;
        if (input.length > 1) args = input[1];

        if (command.equals(">help")) {
            help();
        }

        else if (command.equals(">update")) {
            update();
        }

        else if (command.equals(">comment")) {
            comment();
        }

        else if (command.equals(">photo")) {
            photo();
        }

        else if (command.equals(">gif")) {
            gif();
        }

        else if (command.equals(">search")) {
            search();
        }

        else if (command.equals(">news")) {
            if (args != null && args.contains("top")) newsTop();
            else news();
        }

        else if (command.equals(">about")) {
            about();
        }

        else if (command.equals(">stat")) {
            stat();
        }

        else if (command.equals(">joke")) {
            joke();
        }
    }

    private void stat() throws Exception {
        statRecorder.incrementCount(command.replace(">",""));
        channel.sendMessage("This command has been requested " + statRecorder.getCount(args) + " times since May 1 2020.").queue();
    }

    private void joke() throws Exception {
        statRecorder.incrementCount(command.replace(">",""));
        String joke = DadJokes.generateDadJoke();
        channel.sendMessage(joke).queue();
        channel.sendMessage("hi");
    }

    private void help() throws Exception {
        channel.sendMessage(read_file("help.txt")).queue();
        statRecorder.incrementCount(command.replace(">",""));
    }

    private void update() throws Exception {
        channel.sendMessage(read_file("update.txt")).queue();
        statRecorder.incrementCount(command.replace(">",""));
    }

    private void about() throws Exception {
        statRecorder.incrementCount(command.replace(">",""));
        String url = "https://github.com/APM246/RedditContent-Discord-Bot";
        channel.sendMessage(url).queue();
    }

    private void comment() throws Exception {
        String reddit_message = reddit.findComment(args);
        channel.sendMessage(reddit_message).queue();
        statRecorder.incrementCount(command.replace(">",""));
    }

    private void photo() throws Exception {
        if (isInappropriate(args))
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
            if (args.contains("raven")) statRecorder.incrementCount(args);
            String[] photo_properties = reddit.getPhotoLink(args);
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

    private void gif() throws Exception {
        if (isInappropriate(args))
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
            String[] gif_properties = reddit.getGIFLink(args);
            statRecorder.incrementCount(command.replace(">",""));
            if (gif_properties == null) channel.sendMessage("This subreddit does not contain gif-based posts").queue();
            else channel.sendMessage(gif_properties[0]).queue();
        }
    }

    private void search() throws Exception {
        channel.sendMessage(reddit.searchSubreddits(args)).queue();
        statRecorder.incrementCount(command.replace(">",""));
    }

    private void news() throws Exception {
        statRecorder.incrementCount(command.replace(">",""));
        String url = newsUpdates.retrieveURL(true);
        channel.sendMessage(url).queue(); 
    }

    private void newsTop() throws Exception {
        statRecorder.incrementCount(command.replace(">",""));
        String url = newsUpdates.retrieveURL(false);
        channel.sendMessage(url).queue();
    }
                

}
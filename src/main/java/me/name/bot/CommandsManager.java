package me.name.bot;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import me.name.DadJokes;
import me.name.NewsUpdates;
import me.name.Reddit;
import me.name.music.Music;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.ImageInfo;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import java.util.Random;

public class CommandsManager {
    
    final private Random random;
    final private Reddit reddit;
    final private NewsUpdates newsUpdates;
    final private Music musicBot;
    private Player player;
    private MessageChannel channel;
    private StatsRecorder statRecorder;
    private String command;
    private String args;
    private boolean isLocked = false;
    private String[] output; 
    private int n_tries;

    public CommandsManager(Reddit reddit, NewsUpdates newsUpdates, Music musicBot) throws Exception 
    {
        random = new Random();
        this.reddit = reddit;
        this.newsUpdates = newsUpdates;
        this.musicBot = musicBot;
        statRecorder = new StatsRecorder();
    }

    public void memeify(String message, MessageChannel channel) 
    {
        String modified = "";
        String letter;
        for (int i = 0; i < message.length(); i++) {
            if (i % 2 == 0) letter = message.substring(i, i+1).toUpperCase();
            else letter = message.substring(i, i+1);

            modified += letter;
        }

        channel.sendMessage(modified).queue();
    }

    public boolean shouldMeme() 
    {
        if (random.nextInt(100) < 10) return true;
        else return false;
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
   
    public void executeCommand(MessageChannel channel, String[] input, MessageReceivedEvent event) throws Exception
    {
        this.channel = channel;
        command = input[0];
        args = null;
        if (input.length > 1) args = input[1];

        if (command.equals(">game")) game();

        else if (command.equals(">guess") && isLocked) guess();            

        else if (command.equals(">quit") && isLocked) quit();

        else if (command.equals(">help")) help();

        else if (command.equals(">update")) update();

        else if (command.equals(">comment")) comment();
        
        else if (command.equals(">photo")) deprecated_message();

        else if (command.equals(">gif")) deprecated_message();

        else if (command.equals(">search")) search();

        else if (command.equals(">post")) post();

        else if (command.equals(">news")) {
            if (args != null && args.contains("top")) newsTop();
            else news();
        }

        else if (command.equals(">about")) about();

        else if (command.equals(">stat")) stat();

        else if (command.equals(">joke")) joke();
        
        else if (command.equals(">play")) play(event);
        
        else if (command.equals(">stop")) stop(event);

        else if (command.equals(">skip")) skip(event);
    }

    private void game() throws Exception {
        if (!isLocked) 
        {
            n_tries = 0;
            player = new Player(channel.getIdLong(), channel.getName());
            isLocked = true;
            output = reddit.guessCity();
            channel.sendMessage(output[0] + "\n" + "Try and guess the name of the city or country").queue();
            statRecorder.incrementCount(command.replace(">",""));
        }

        else channel.sendMessage("Game is being played in the " + player.getChannelName() + " channel").queue();
    }

    private void guess() throws Exception {
        if (channel.getIdLong() == player.getchannelID())
                    {
                        statRecorder.incrementCount(command.replace(">",""));
                        String attempt = args.toLowerCase();
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

    private void quit() {
        if (channel.getIdLong() == player.getchannelID())
        {
            channel.sendMessage("The title of the post was: " + output[1]).queue();
            player = null;
            isLocked = false;
        }

        else channel.sendMessage("Game is being played in the " + player.getChannelName() + " channel").queue();
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
        String reddit_message = reddit.getComment(args);
        channel.sendMessage(reddit_message).queue();
        statRecorder.incrementCount(command.replace(">",""));
    }

    private void deprecated_message() {
        channel.sendMessage("This command has been deprecated, use >post [subreddit name] instead!");
    }

    private void photo(String[] photo_properties) throws Exception {
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

    private void gif(String[] gif_properties) throws Exception {
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
     
    private void play(MessageReceivedEvent event) throws Exception {
        statRecorder.incrementCount(command.replace(">",""));
        System.out.println(args.toString());
        musicBot.loadAndPlay(event.getTextChannel(), musicBot.searchTermtoURL(args));
    }

    private void skip(MessageReceivedEvent event) throws Exception {
        statRecorder.incrementCount(command.replace(">",""));
        musicBot.skipTrack(event.getTextChannel());
    }

    private void stop(MessageReceivedEvent event) throws Exception {
        statRecorder.incrementCount(command.replace(">",""));
        musicBot.kickBot();
    }

    private void post() throws Exception {
        String[] content = reddit.getPost(args);
        if (content[content.length - 1].equals("photo")) photo(content);
        else if (content[content.length - 1].equals("gif")) gif(content);
        else channel.sendMessage(content[0]).queue(); // self-post
        statRecorder.incrementCount(command.replace(">",""));
    }

    private void copyPasta() {
        
    }
}
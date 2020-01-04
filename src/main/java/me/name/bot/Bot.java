package me.name.bot;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;


public class Bot extends ListenerAdapter
{
    public static void main(String[] args) throws LoginException
    {
        JDA api = new JDABuilder("NjUyNTY0MjQ0MDY3Mzg1MzQ1.XhC_EA.vXUV-kUMX3MpbX4qzQnxYLcyd2s").addEventListeners(new Bot()).build();
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event)
    {
        if (event.getAuthor().isBot()) return;
        // don't respond to bots (including self)

        Message message = event.getMessage();
        String content = message.getContentRaw();

        if (content.equals("%ping"))
        {
            MessageChannel channel = event.getChannel();
            channel.sendMessage("Pong!").queue();
        }

    }
}

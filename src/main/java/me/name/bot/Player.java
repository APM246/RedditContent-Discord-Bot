package me.name.bot;

public class Player 
{
    private long channelID;


    public Player(long channelID)
    {
        this.channelID = channelID;
    }

    public long getchannelID()
    {
        return channelID;
    }
}
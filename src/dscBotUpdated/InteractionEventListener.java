package dscBotUpdated;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class InteractionEventListener extends ListenerAdapter {
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		new SlashCommandHandler().handle(event);
	}
}
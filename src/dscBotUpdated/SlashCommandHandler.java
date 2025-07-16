package dscBotUpdated;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import java.awt.Color;

public class SlashCommandHandler {

	public void handle(SlashCommandInteractionEvent event) {
		String command = event.getName();
		switch (command) {
		case "ping":
			event.reply("Pong!").queue();
			break;
		case "serverinfo":
			Guild guild = event.getGuild();
			
			  if (guild == null) {
			  event.reply("Эта команда доступна только на сервере.").setEphemeral(true).
			  queue(); return; }
			EmbedBuilder embedServer = new EmbedBuilder();
			int memberCount = guild.getMemberCount();
			String originalDate = guild.getTimeCreated().toString();
			String modifiedDate = originalDate.replace("T", " ").substring(0, originalDate.length() - 5);
			Color embedColorServer = new Color(139, 0, 0);
			embedServer.setTitle("Информация о сервере «" + guild.getName() + "»")
					.addField("Количество участников на сервере:", String.valueOf(memberCount), false)
					.addField("Владелец:",
							guild.getOwner() != null ? guild.getOwner().getEffectiveName() : "Неизвестно", false)
					.addField("Дата создания сервера:", modifiedDate, false).setColor(embedColorServer)
					.setThumbnail(guild.getIconUrl())
					.setFooter("Футер Embed-сообщения: " + event.getUser().getEffectiveName(), event.getUser().getAvatarUrl());

			event.replyEmbeds(embedServer.build()).queue();
			break;
		case "help":
			EmbedBuilder embedHelp = new EmbedBuilder();
			Color embedColor = new Color(139, 0, 0);
			embedHelp.setColor(embedColor);
			embedHelp.setTitle("Список команд");
			embedHelp.setDescription("**/ping** - ответит \"Pong!\"\n" + "**/serverinfo** - информация о сервере\n"
					+ "**/avatar** - аватар участника сервера\n" + "**/clear** - очистка сообщений");
			embedHelp.setFooter("Футер Embed-сообщения: " + event.getUser().getEffectiveName(), event.getUser().getAvatarUrl());
			event.replyEmbeds(embedHelp.build()).queue();
			break;
		case "avatar":
		    Member member = event.getOption("user").getAsMember();

		    if (member == null) {
		        event.reply("❌ Участник не найден на сервере.").setEphemeral(true).queue();
		        return;
		    }

		    String avatarUrl = member.getUser().getEffectiveAvatarUrl();
		    String displayName = member.getEffectiveName();

		    EmbedBuilder embed = new EmbedBuilder()
		            .setColor(new Color(139, 0, 0))
		            .setTitle("Аватар " + displayName)
		            .setImage(avatarUrl)
		            .setFooter("Футер Embed-сообщения: " + event.getUser().getEffectiveName(), event.getUser().getAvatarUrl());

		    event.replyEmbeds(embed.build()).queue();
		    break;
		case "clear":
		    if (!event.getMember().hasPermission(net.dv8tion.jda.api.Permission.MESSAGE_MANAGE)) {
		        EmbedBuilder noPermEmbed = new EmbedBuilder()
		            .setColor(new Color(139, 0, 0))
		            .setDescription(":x: Вы не имеете права удалять сообщения!");
		        event.replyEmbeds(noPermEmbed.build()).setEphemeral(true).queue();
		        return;
		    }

		    int amount = (int) event.getOption("amount").getAsLong();

		    if (amount < 1 || amount > 100) {
		        EmbedBuilder invalidAmountEmbed = new EmbedBuilder()
		            .setColor(new Color(139, 0, 0))
		            .setDescription(":x: Укажите количество от 1 до 100.");
		        event.replyEmbeds(invalidAmountEmbed.build()).setEphemeral(true).queue();
		        return;
		    }

		    event.getChannel().getHistory()
		        .retrievePast(amount)
		        .queue(messages -> {
		            event.getChannel().purgeMessages(messages);

		            EmbedBuilder successEmbed = new EmbedBuilder()
		                .setColor(new Color(139, 0, 0))
		                .setDescription(":white_check_mark: Очищено **" + amount + "** сообщений!");

		            event.replyEmbeds(successEmbed.build()).setEphemeral(true).queue();
		        }, error -> {
		            EmbedBuilder errorEmbed = new EmbedBuilder()
		                .setColor(new Color(139, 0, 0))
		                .setDescription(":x: Не удалось удалить сообщения.");
		            event.replyEmbeds(errorEmbed.build()).setEphemeral(true).queue();
		        });
		    break;
		}
	}
}
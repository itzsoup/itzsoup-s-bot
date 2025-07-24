package dscBotUpdated;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.awt.Color;
import java.util.Comparator;
import java.util.EnumSet;

import java.util.List;
import java.util.stream.Collectors;

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
				EmbedBuilder serverUnavailable = new EmbedBuilder()
					.setColor(new Color(139, 0, 0))
					.setDescription(":x: Эта команда доступна только на сервере.");
				event.replyEmbeds(serverUnavailable.build()).setEphemeral(true).queue();
				return;
			}

			String formattedDate = "<t:" + guild.getTimeCreated().toEpochSecond() + ":f>";
			String relativeTime = "<t:" + guild.getTimeCreated().toEpochSecond() + ":R>";

			EmbedBuilder embedServer = new EmbedBuilder()
				.setColor(new Color(139, 0, 0))
				.setTitle("Информация о сервере «" + guild.getName() + "»")
				.addField("Количество участников на сервере:", String.valueOf(guild.getMemberCount()), false)
				.addField("Владелец:", guild.getOwner() != null ? guild.getOwner().getEffectiveName() : "Неизвестно", false)
				.addField("Дата создания сервера:", formattedDate + "\n" + relativeTime, false)
				.setThumbnail(guild.getIconUrl())
				.setFooter("Футер Embed-сообщения", event.getUser().getAvatarUrl());
			event.replyEmbeds(embedServer.build()).queue();
			break;
		
		case "help":
			String commandsDescription =
				"**/ping** - ответит \"Pong!\"\n" +
				"**/serverinfo** - отображает информацию о сервере\n" +
				"**/avatar** - отображает аватар пользователя\n" + 
				"**/clear** - удаляет указанное количество сообщений\n" +
				"**/roleinfo** - показывает информацию о роли";
			
			EmbedBuilder embedHelp = new EmbedBuilder()
				.setColor(new Color(139, 0, 0))
				.setTitle("Список команд")
				.setDescription(commandsDescription)
				.setFooter("Футер Embed-сообщения", event.getUser().getAvatarUrl());
			event.replyEmbeds(embedHelp.build()).queue();
			break;
		
		case "avatar":
			User user = event.getOption("user").getAsUser();

			if (user == null) {
				EmbedBuilder memberUnavailable = new EmbedBuilder()
					.setColor(new Color(139, 0, 0))
					.setDescription(":x: Пользователь не найден.");
				event.replyEmbeds(memberUnavailable.build()).setEphemeral(true).queue();
				return;
			}

			String avatarUrl = user.getEffectiveAvatarUrl();
			String displayName = user.getEffectiveName();

			EmbedBuilder embed = new EmbedBuilder()
					.setColor(new Color(139, 0, 0))
					.setTitle("Аватар " + displayName)
					.setImage(avatarUrl)
					.setFooter("Футер Embed-сообщения", event.getUser().getAvatarUrl());
			event.replyEmbeds(embed.build()).queue();
			break;
		
		case "clear":
			if (event.getGuild() == null) {
				EmbedBuilder clearUnavailable = new EmbedBuilder()
					.setColor(new Color(139, 0, 0))
					.setDescription(":x: Эта команда доступна только на сервере.");
				event.replyEmbeds(clearUnavailable.build()).setEphemeral(true).queue();
				return;
			}
			
			if (!event.getMember().hasPermission(net.dv8tion.jda.api.Permission.MESSAGE_MANAGE)) {
				EmbedBuilder noPermEmbed = new EmbedBuilder()
						.setColor(new Color(139, 0, 0))
						.setDescription(":x: Вы не имеете права удалять сообщения!");
				event.replyEmbeds(noPermEmbed.build()).setEphemeral(true).queue();
				return;
			}

			int amount = (int) event.getOption("amount").getAsLong();

			if (amount < 1 || amount > 100) {
				EmbedBuilder invalidAmountEmbed = new EmbedBuilder().setColor(new Color(139, 0, 0))
						.setDescription(":x: Укажите количество от 1 до 100.");
				event.replyEmbeds(invalidAmountEmbed.build()).setEphemeral(true).queue();
				return;
			}

			event.getChannel().getHistory().retrievePast(amount).queue(messages -> {
				event.getChannel().purgeMessages(messages);

				EmbedBuilder successEmbed = new EmbedBuilder()
						.setColor(new Color(139, 0, 0))
						.setDescription(":white_check_mark: Очищено **" + amount + "** сообщений!");
				event.replyEmbeds(successEmbed.build()).queue();
			}, error -> {
				EmbedBuilder errorEmbed = new EmbedBuilder()
						.setColor(new Color(139, 0, 0))
						.setDescription(":x: Не удалось удалить сообщения.");
				event.replyEmbeds(errorEmbed.build()).setEphemeral(true).queue();
			});
			break;
		
		case "roleinfo":
			Role role = event.getOption("role").getAsRole();

			if (role == null) {
				EmbedBuilder roleMissing = new EmbedBuilder()
					.setColor(new Color(139, 0, 0))
					.setDescription(":x: Роль не найдена.");
				event.replyEmbeds(roleMissing.build()).setEphemeral(true).queue();
				return;
			}

			EnumSet<Permission> permissions = role.getPermissions();
			StringBuilder permsBuilder = new StringBuilder();

			if (permissions.isEmpty()) {
				permsBuilder.append("Нет прав.");
			} else {
				List<String> permNames = permissions.stream()
				        .sorted(Comparator.comparing(Permission::getName))
				        .map(Permission::getName)
				        .collect(Collectors.toList());

				    permsBuilder.append(String.join("\n• ", permNames));
				    permsBuilder.insert(0, "• ");
			}

			EmbedBuilder embedRole = new EmbedBuilder()
				.setColor(role.getColor() != null ? role.getColor() : new Color(139, 0, 0))
				.setTitle("Информация о роли «" + role.getName() + "»")
				.addField("Права:", permsBuilder.toString(), false)
				.addField("Участников с ролью:", String.valueOf(event.getGuild().getMembersWithRoles(role).size()), true)
				.addField("Цвет:", role.getColor() != null
				? "#" + Integer.toHexString(role.getColor().getRGB()).substring(2).toUpperCase()
				: "Нет", true)
				.addField("ID:", role.getId(), true)
				.setFooter("Футер Embed-сообщения", event.getUser().getAvatarUrl());

			event.replyEmbeds(embedRole.build()).queue();
			break;
		}
	}
}
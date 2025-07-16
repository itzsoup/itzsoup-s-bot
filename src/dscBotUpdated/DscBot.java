package dscBotUpdated;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class DscBot {
	public static void main(String[] args) throws LoginException {
		JDABuilder jda = JDABuilder.create("BOT_TOKEN",

				GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGES,
				GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MEMBERS

		).disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS);
		jda.setActivity(Activity.playing("/help"));
		jda.setStatus(OnlineStatus.ONLINE);
		jda.addEventListeners(new InteractionEventListener());
		jda.setMemberCachePolicy(MemberCachePolicy.ALL);
		jda.build()
			.updateCommands()
			.addCommands(
				Commands.slash("ping", "Ответит Pong!"), 
				Commands.slash("serverinfo", "Отображает информацию о сервере"),
				Commands.slash("help", "Отображает список всех команд"),
				Commands.slash("avatar", "Отображает аватар пользователя")
		        .addOption(OptionType.USER, "user", "Упомянутый пользователь", true),
		        Commands.slash("clear", "Удаляет указанное количество сообщений")
		        .addOption(OptionType.INTEGER, "amount", "Количество сообщений для удаления", true)).queue();
		System.out.println("Bot started!");
	}
}
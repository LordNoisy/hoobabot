package com.lordnoisy.hoobabot.registry;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.discordjson.json.ImmutableApplicationCommandRequest;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class ApplicationCommandRegistry {

    public static Mono<Void> registerApplicationCommands (GatewayDiscordClient gateway) {
        return createGlobalCommandsMono(createApplicationCommandRequests(), gateway);
    }

    public static List<ApplicationCommandRequest> createApplicationCommandRequests () {
        List<ApplicationCommandRequest> applicationCommandRequests = new ArrayList<>();

        //Create commands

        ImmutableApplicationCommandRequest.Builder pollCommandBuilder = ApplicationCommandRequest.builder()
                .name("poll")
                .description("Create a poll that everyone can vote on!")
                .addOption(ApplicationCommandOptionData.builder()
                        .name("question")
                        .description("What question would you like to ask?")
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .maxLength(255)
                        .required(true)
                        .build())
                .addOption(ApplicationCommandOptionData.builder()
                        .name("option_1")
                        .description("Add a custom option to your poll")
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .maxLength(40)
                        .required(false)
                        .build());

        for (int i = 0; i < 18; i++) {
            pollCommandBuilder.addOption(ApplicationCommandOptionData.builder()
                    .name("option_"+(i+2))
                    .description("Add a custom option to your poll")
                    .type(ApplicationCommandOption.Type.STRING.getValue())
                    .maxLength(40)
                    .required(false)
                    .build());
        }

        pollCommandBuilder.addOption(ApplicationCommandOptionData.builder()
                .name("description")
                .description("Optionally add a longer description to your poll.")
                .type(ApplicationCommandOption.Type.STRING.getValue())
                .maxLength(255)
                .required(false)
                .build());

        pollCommandBuilder.addOption(ApplicationCommandOptionData.builder()
                .name("image")
                .description("Optionally include an image to be included in your poll")
                .type(ApplicationCommandOption.Type.ATTACHMENT.getValue())
                .required(false)
                .build());

        pollCommandBuilder.addOption(ApplicationCommandOptionData.builder()
                .name("open_poll")
                .description("Setting this to true will allow user submitted responses to your poll")
                .type(ApplicationCommandOption.Type.BOOLEAN.getValue())
                .required(false)
                .build());

        ApplicationCommandRequest pollCommand = pollCommandBuilder.build();
        applicationCommandRequests.add(pollCommand);

        ApplicationCommandRequest uptimeCommand = ApplicationCommandRequest.builder()
                .name("uptime")
                .description("Get the current uptime of the bot")
                .build();
        applicationCommandRequests.add(uptimeCommand);

        return applicationCommandRequests;
    }

    public static Mono<Void> createGlobalCommandsMono(List<ApplicationCommandRequest> applicationCommandRequestList, GatewayDiscordClient gateway) {
        Mono<Void> commandsMono = Mono.empty();
        for (ApplicationCommandRequest current : applicationCommandRequestList) {
            Mono<Void> createGlobalApplicationCommand = gateway.getRestClient().getApplicationId().flatMap(applicationID -> gateway.getRestClient().getApplicationService().createGlobalApplicationCommand(applicationID, current).then());
            commandsMono = commandsMono.and(createGlobalApplicationCommand);
        }
        return commandsMono;
    }
}

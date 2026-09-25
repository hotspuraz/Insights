package dev.frankheijden.insights.commands.util;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.incendo.cloud.SenderMapper;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class CommandSenderMapper implements SenderMapper<CommandSourceStack, CommandSender> {
    @Override
    public CommandSender map(CommandSourceStack source) {
        return source.getSender();
    }

    @SuppressWarnings("NonExtendableApiUsage")
    @Override
    public CommandSourceStack reverse(CommandSender sender) {
        return new CommandSourceStack() {
            @Override
            public Location getLocation() {
                if (sender instanceof Entity entity) {
                    return entity.getLocation();
                }

                var worlds = Bukkit.getWorlds();
                return new Location(worlds.isEmpty() ? null : worlds.getFirst(), 0, 0, 0); // Best effort lol
            }

            @Override
            public CommandSender getSender() {
                return sender;
            }

            @Override
            public @Nullable Entity getExecutor() {
                return sender instanceof Entity entity ? entity : null;
            }

            @Override
            public Player getPlayerOrThrow() throws CommandSyntaxException {
                if (sender instanceof Player player) {
                    return player;
                }
                throw new SimpleCommandExceptionType(
                        new LiteralMessage("The command source is not a player")
                ).create();
            }

            @Override
            public Entity getEntityOrThrow() throws CommandSyntaxException {
                if (sender instanceof Entity entity) {
                    return entity;
                }
                throw new SimpleCommandExceptionType(
                        new LiteralMessage("The command source is not an entity")
                ).create();
            }

            @Override
            public CommandSourceStack withLocation(Location location) {
                return CommandSenderMapper.this.reverse(sender).withLocation(location);
            }

            @Override
            public CommandSourceStack withExecutor(Entity e) {
                return CommandSenderMapper.this.reverse(sender).withExecutor(e);
            }
        };
    }
}

package com.github.piatrashkakanstantinass.ftpserver;

import java.util.Objects;

public final class CommandInfo {
    private final Command command;
    private final CommandArgumentRequirement argumentRequirement;

    public CommandInfo(Command command, CommandArgumentRequirement argumentRequirement) {
        this.command = command;
        this.argumentRequirement = argumentRequirement;
    }

    public CommandInfo(NoArgCommand command, CommandArgumentRequirement argumentRequirement) {
        this.command = command;
        this.argumentRequirement = argumentRequirement;
    }

    public Command command() {
        return command;
    }

    public CommandArgumentRequirement argumentRequirement() {
        return argumentRequirement;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CommandInfo) obj;
        return Objects.equals(this.command, that.command) &&
                Objects.equals(this.argumentRequirement, that.argumentRequirement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(command, argumentRequirement);
    }

    @Override
    public String toString() {
        return "CommandInfo[" +
                "command=" + command + ", " +
                "argumentRequirement=" + argumentRequirement + ']';
    }

}

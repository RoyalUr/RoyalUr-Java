package net.royalur.cli;

public record CLIArgument(
        String name,
        CLIArgumentType type,
        String[] descriptionLines,
        boolean required
) {

    public CLIArgument {
        for (String line : descriptionLines) {
            if (line.length() > 60)
                throw new IllegalArgumentException("Description line too long: " + line.length() + " > 60");
        }
        if (!name.startsWith("-")) {
            if (required && (!name.startsWith("<") || !name.endsWith(">")))
                throw new IllegalArgumentException("Required args should be surrounded by angled brackets <>");
            if (!required && (!name.startsWith("[") || !name.endsWith("]")))
                throw new IllegalArgumentException("Optional args should be surrounded by square brackets []");
        }
    }

    public boolean isKeywordArg() {
        return name.startsWith("-");
    }

    public boolean isPositionalArg() {
        return !isKeywordArg();
    }

    private void appendNameAndType(StringBuilder builder) {
        builder.append(name);
        if (isKeywordArg() && !type.equals(CLIArgumentType.NONE)) {
            builder.append("=<").append(type.name().toLowerCase()).append(">");
        }
    }

    public String getNameAndType() {
        StringBuilder builder = new StringBuilder();
        appendNameAndType(builder);
        return builder.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        appendNameAndType(builder);
        if (isKeywordArg() && required) {
            builder.append(" (required)");
        }
        return builder.toString();
    }
}

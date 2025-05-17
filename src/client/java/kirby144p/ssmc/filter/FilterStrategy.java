//	Stop spamming my Chat (SSMC) is a modification for Minecraft which
//  allows for filtering and performing certain actions on chat messages.
//	Copyright (C) 2025  Kirby144p
//	
//	This program is free software: you can redistribute it and/or modify
//	it under the terms of the GNU General Public License as published by
//	the Free Software Foundation, either version 3 of the License, or
//	(at your option) any later version.
//	
//	This program is distributed in the hope that it will be useful,
//	but WITHOUT ANY WARRANTY; without even the implied warranty of
//	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//	GNU General Public License for more details.
//	
//	You should have received a copy of the GNU General Public License
//	along with this program.  If not, see <https://www.gnu.org/licenses/>.


package kirby144p.ssmc.filter;

import java.util.function.BiFunction;

public enum FilterStrategy {
    CONTAINS((message, pattern) -> {
        return message.contains(pattern);
    }),
    STARTS_WITH((message, pattern) -> {
        return message.startsWith(pattern);
    }),
    ENDS_WITH((message, pattern) -> {
        return message.endsWith(pattern);
    }),
    EXACT_MATCH((message, pattern) -> {
        return message.equals(pattern);
    }),
    REGEX((message, pattern) -> {
        return message.matches(pattern);
    });

    private final BiFunction<String, String, Boolean> matcher;

    private FilterStrategy(BiFunction<String, String, Boolean> matcher) {
        this.matcher = matcher;
    }

    public boolean isMatch(String message, String pattern) {
        return this.matcher.apply(message, pattern);
    }
}

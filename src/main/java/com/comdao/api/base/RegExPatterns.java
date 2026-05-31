package com.comdao.api.base;

public class RegExPatterns {
    public static final String EMAIL = "^([a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,})?$";
    public static final String EMAIL_MESSAGE = "Invalid email format";

    public static final String PHONE = "^(0[\\d]{9})?$";
    public static final String PHONE_MESSAGE = "Please provide with a valid phone number";

    public static final String FULLNAME = "^([\\p{L}.'\\s-]+)?$";
    public static final String FULLNAME_MESSAGE = "Name can only contain letters, spaces, hyphens, apostrophes, or periods";

    public static final String USERNAME = "^[a-zA-Z_][a-zA-Z0-9_-]{2,36}$";
    public static final String USERNAME_MESSAGE = "Username must start with a letter or a underscore, be 3-36 characters, and only contain letters, numbers, underscores, or hyphens";

}
